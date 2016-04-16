package com.tilal6991.channels.redux.presenter

import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.tilal6991.channels.R
import com.tilal6991.channels.redux.MainItemAdapter
import com.tilal6991.channels.redux.selectedChild
import com.tilal6991.channels.redux.subscribe
import com.tilal6991.channels.view.EventRecyclerView
import trikita.anvil.Anvil.currentView
import trikita.anvil.DSL.*
import trikita.anvil.recyclerview.Recycler

class EventPresenter(private val context: Context) {
    private lateinit var eventAdapter: MainItemAdapter

    private lateinit var subscription: Runnable

    fun setup() {
        eventAdapter = MainItemAdapter(context)
        eventAdapter.setData(selectedChild()?.buffer)
        eventAdapter.setHasStableIds(true)
    }

    fun bind() {
        subscription = subscribe(context) {
            eventAdapter.setData(selectedChild()?.buffer)
        }
    }

    fun unbind() {
        subscription.run()
    }

    fun view() {
        v(EventRecyclerView::class.java) {
            init {
                val linearLayoutManager = LinearLayoutManager(context)
                val listener = BottomPinningLayoutEventListener(
                        currentView(), linearLayoutManager, eventAdapter)
                attr({ v, n, o -> (v as EventRecyclerView).addOnScrollListener(n) }, listener)
                attr({ v, n, o -> v.addOnLayoutChangeListener(n) }, listener)
                Recycler.layoutManager(linearLayoutManager)
            }

            val layoutParams = CoordinatorLayout.LayoutParams(MATCH, MATCH)
            layoutParams.behavior = AppBarLayout.ScrollingViewBehavior()
            layoutParams(layoutParams)

            id(R.id.event_recycler)
            padding(dip(8))
            clipToPadding(false)
            visibility(selectedChild() != null)

            Recycler.adapter(eventAdapter)
        }
    }

    private class BottomPinningLayoutEventListener(
            private val recyclerView: RecyclerView,
            private val layoutManager: LinearLayoutManager,
            private val adapter: MainItemAdapter) : RecyclerView.OnScrollListener(), View.OnLayoutChangeListener {

        private var mIsScrolledToBottom = true

        private val adapterWatcher = object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (mIsScrolledToBottom) {
                    recyclerView.post { recyclerView.scrollToPosition(adapter.itemCount - 1) }
                }
            }

            override fun onChanged() {
                if (adapter.itemCount > 0) {
                    recyclerView.post { recyclerView.scrollToPosition(adapter.itemCount - 1) }
                }
                mIsScrolledToBottom = true
            }
        }

        init {
            adapter.registerAdapterDataObserver(adapterWatcher)
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState);

            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                mIsScrolledToBottom = layoutManager.findLastCompletelyVisibleItemPosition() ==
                        adapter.itemCount - 1;
            }
        }

        override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int,
                                    oldTop: Int, oldRight: Int, oldBottom: Int) {
            if ((bottom < oldBottom) && mIsScrolledToBottom) {
                recyclerView.post { recyclerView.scrollToPosition(adapter.itemCount - 1) }
            }
        }
    }
}