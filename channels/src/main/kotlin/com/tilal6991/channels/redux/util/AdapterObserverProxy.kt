package com.tilal6991.channels.redux.util

import android.support.v7.widget.RecyclerView

class AdapterObserverProxy(private val adapter: RecyclerView.Adapter<*>) : RecyclerView.AdapterDataObserver() {

    override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
        for (i in 0..itemCount - 1) {
            adapter.notifyItemMoved(fromPosition + i, toPosition + i)
        }
    }

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        adapter.notifyItemRangeInserted(positionStart, itemCount)
    }

    override fun onChanged() {
        adapter.notifyDataSetChanged()
    }

    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
        adapter.notifyItemRangeRemoved(positionStart, itemCount)
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
        adapter.notifyItemRangeChanged(positionStart, itemCount)
    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
        adapter.notifyItemRangeChanged(positionStart, itemCount, payload)
    }
}