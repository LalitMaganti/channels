package co.fusionx.channels.view

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import co.fusionx.channels.adapter.MainItemAdapter
import co.fusionx.channels.base.relayHost
import co.fusionx.channels.observable.ObservableList
import kotlin.properties.Delegates

public class EventRecyclerView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null) : RecyclerView(context, attrs),
        ObservableList.Observer {

    /* Internal variables */
    private var layoutManager: LinearLayoutManager by Delegates.notNull()
    private val mainItemAdapter: MainItemAdapter
        get() = adapter as MainItemAdapter

    /* The default state should be to snap to the bottom */
    // private var pendingScrollState = PendingScrollState.SNAP_TO_BOTTOM
    private var pendingScrollPosition = -1
    private var data: ObservableList<CharSequence>? = null

    override fun onFinishInflate() {
        super.onFinishInflate()

        layoutManager = LinearLayoutManager(context)
        setLayoutManager(layoutManager)

        addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                pendingScrollPosition = -1
            }
        })

        adapter = MainItemAdapter(context)
    }

    /*
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val count = mainItemAdapter.itemCount
        if (h < oldh && count != 0) {
            val lastVisible = layoutManager.findLastCompletelyVisibleItemPosition()
            post { scrollToEnd(lastVisible, count, count) }
        }
    }
    */

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()


    }

    public override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable(SUPER_STATE, super.onSaveInstanceState())

        /*
        val scrollPair = calculateSavedScrollState()

        // Put the ints into the bundle
        bundle.putInt(PENDING_SCROLL_STATE, scrollPair.first)
        */
        bundle.putInt(PENDING_SCROLL_POSITION, layoutManager.findLastCompletelyVisibleItemPosition())

        return bundle
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        val superState: Parcelable
        if (state is Bundle) {
            superState = state.getParcelable(SUPER_STATE)

            /*
            pendingScrollState = state.getInt(PENDING_SCROLL_STATE)
            */
            val position = state.getInt(PENDING_SCROLL_POSITION)
            post { layoutManager.scrollToPositionWithOffset(position, 0) }

            // Re-add all the saved items to the adapter
            switchContent()
        } else {
            superState = state
        }
        super.onRestoreInstanceState(superState)
    }

    fun switchContent() {
        val buffer = relayHost.selectedClient?.selectedChild?.buffer
        mainItemAdapter.setBuffer(buffer)
        mainItemAdapter.notifyDataSetChanged()

        data?.removeObserver(this)
        data = buffer
        data?.addObserver(this)

        /*
        val endOfList = oldCount + buffer.size - 1
        val lastVisible = layoutManager.findLastCompletelyVisibleItemPosition()
        post { scrollToEnd(lastVisible, oldCount, endOfList) }
        */
    }

    override fun onAdd(position: Int) {
        adapter.notifyItemInserted(position)
        scroll(position)
    }

    private fun scroll(position: Int) {
        post {
            // If the last item visible is the final one then keep scrolling.
            val last = layoutManager.findLastCompletelyVisibleItemPosition()
            if (scrollState == SCROLL_STATE_IDLE &&
                    (last == position - 1 || pendingScrollPosition == position - 1)) {
                layoutManager.scrollToPositionWithOffset(position, 0)
                pendingScrollPosition = position
            }
        }
    }

    /* Intelligently scrolls to end of the list */
    /*
    private fun scrollToEnd(lastVisible: Int, oldCount: Int, endOfList: Int) {
        // We do a case analysis over the scroll state and try and figure out what we need to scroll to
        // and if we need to scroll at all
        if (pendingScrollState == PendingScrollState.NONE) {
            // Check if we were at the bottom in the old view of the model - if so then scroll
            if (lastVisible == oldCount - 1) {
                layoutManager.scrollToPosition(endOfList)
            }
        } else if (pendingScrollState == PendingScrollState.SNAP_TO_BOTTOM) {
            // We are snapped to the bottom - keep scrolling as new messages come in
            layoutManager.scrollToPosition(endOfList)
            pendingScrollState = PendingScrollState.NONE
        } else if (pendingScrollState == PendingScrollState.FIRST_VISIBLE_POSITION) {
            // We haven't reached the first visible position item yet - don't scroll at all
            if (pendingScrollPosition < endOfList) {
                layoutManager.scrollToPositionWithOffset(pendingScrollPosition, 0)
                pendingScrollState = PendingScrollState.NONE
            }
        }
    }

    /*
     * If the existing state is NONE then we need to calculate a new state - otherwise we haven't
     * manged to apply the previous state - do that in the next cycle
     */
    private fun calculateSavedScrollState(): Pair<Int, Int> {
        if (pendingScrollState == PendingScrollState.NONE) {
            val lastVisible = layoutManager.findLastCompletelyVisibleItemPosition()
            val lastListItem = mainItemAdapter.itemCount - 1

            if (lastVisible == lastListItem) {
                return Pair(PendingScrollState.SNAP_TO_BOTTOM, -1)
            } else {
                return Pair(PendingScrollState.FIRST_VISIBLE_POSITION,
                        layoutManager.findFirstVisibleItemPosition())
            }
        } else {
            return Pair(pendingScrollState, pendingScrollPosition)
        }
    }
    */

    companion object {
        private val PENDING_SCROLL_POSITION = "pending_scroll_position"

        /*
        private val PENDING_SCROLL_STATE = "pending_scroll_state"

        private object PendingScrollState {
            public val NONE: Int = 0
            public val SNAP_TO_BOTTOM: Int = 1
            public val FIRST_VISIBLE_POSITION: Int = 2
        }
        */
    }
}