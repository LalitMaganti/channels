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

    var callbacks: Callbacks? = null

    private var layoutManager: LinearLayoutManager by Delegates.notNull()
    private val mainItemAdapter: MainItemAdapter
        get() = adapter as MainItemAdapter

    private var firstVisible = -1
    private var lastVisible = -1
    private var data: ObservableList<CharSequence>? = null

    override fun onFinishInflate() {
        super.onFinishInflate()

        layoutManager = LinearLayoutManager(context)
        setLayoutManager(layoutManager)

        addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                firstVisible = layoutManager.findLastCompletelyVisibleItemPosition()
                lastVisible = layoutManager.findLastCompletelyVisibleItemPosition()
            }
        })

        adapter = MainItemAdapter(context)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val count = mainItemAdapter.itemCount
        if (h < oldh && count != 0 && lastVisible == count - 1) {
            post {
                layoutManager.scrollToPositionWithOffset(lastVisible, 0)
                callbacks?.onBottomScrollPosted()
            }
        }
    }

    public override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable(SUPER_STATE, super.onSaveInstanceState())
        bundle.putInt(PENDING_SCROLL_POSITION, layoutManager.findLastCompletelyVisibleItemPosition())
        return bundle
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        val superState: Parcelable
        if (state is Bundle) {
            superState = state.getParcelable(SUPER_STATE)

            val position = state.getInt(PENDING_SCROLL_POSITION)
            post { layoutManager.scrollToPositionWithOffset(position, 0) }
            switchContent()
        } else {
            superState = state
        }
        super.onRestoreInstanceState(superState)
    }

    fun switchContent() {
        val buffer = relayHost.selectedClient?.selectedChild?.buffer
        if (buffer == data) return

        mainItemAdapter.setBuffer(buffer)
        mainItemAdapter.notifyDataSetChanged()

        // Scroll to the bottom once the items are present.
        if (buffer != null) {
            post { layoutManager.scrollToPositionWithOffset(buffer.size - 1, 0) }
        }

        data?.removeObserver(this)
        data = buffer
        data?.addObserver(this)
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
                    (last == position - 1 || lastVisible == position - 1)) {
                layoutManager.scrollToPositionWithOffset(position, 0)
                callbacks?.onBottomScrollPosted()
                lastVisible = position
            }
        }
    }

    companion object {
        private const val PENDING_SCROLL_POSITION = "pending_scroll_position"
    }

    interface Callbacks {
        public fun onBottomScrollPosted()
    }
}