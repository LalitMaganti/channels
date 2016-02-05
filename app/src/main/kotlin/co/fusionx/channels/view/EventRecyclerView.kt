package co.fusionx.channels.view

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import kotlin.properties.Delegates

class EventRecyclerView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null) : RecyclerView(context, attrs) {

    private var layoutManager: LinearLayoutManager by Delegates.notNull<LinearLayoutManager>()

    private var firstVisible = -1
    private var lastVisible = -1

    override fun onFinishInflate() {
        super.onFinishInflate()

        layoutManager = LinearLayoutManager(context)
        setLayoutManager(layoutManager)

        if (isInEditMode) {
            return
        }

        addOnScrollListener(object : OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                firstVisible = layoutManager.findLastCompletelyVisibleItemPosition()
                lastVisible = layoutManager.findLastCompletelyVisibleItemPosition()
            }
        })
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val count = adapter.itemCount
        if (h < oldh && count != 0 && lastVisible == count - 1) {
            post { layoutManager.scrollToPositionWithOffset(lastVisible, 0) }
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
        } else {
            superState = state
        }
        super.onRestoreInstanceState(superState)
    }

    fun forceScroll(position: Int) = post {
        layoutManager.scrollToPositionWithOffset(position, 0)
    }

    fun scroll(position: Int) = post {
        // If the last item visible is the final one then keep scrolling.
        val last = layoutManager.findLastCompletelyVisibleItemPosition()
        if (scrollState == SCROLL_STATE_IDLE &&
                (last == position - 1 || lastVisible == position - 1)) {
            layoutManager.scrollToPositionWithOffset(position, 0)
            lastVisible = position
        }
    }

    companion object {
        private const val PENDING_SCROLL_POSITION = "pending_scroll_position"
    }
}