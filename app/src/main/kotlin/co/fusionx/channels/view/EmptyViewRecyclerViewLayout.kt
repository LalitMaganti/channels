package co.fusionx.channels.view

import android.content.Context
import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout

public class EmptyViewRecyclerViewLayout(
        context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {

    var recycler: RecyclerView? = null
    var empty: View? = null
    var adapter: Adapter<*>? = null

    val observer = object : RecyclerView.AdapterDataObserver() {
        public override fun onChanged() = invalidateVisibility()

        public override fun onItemRangeInserted(positionStart: Int, itemCount: Int) =
                invalidateVisibility()

        public override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) =
                invalidateVisibility()
    }

    constructor(context: Context) : this(context, null)

    override fun onFinishInflate() {
        super.onFinishInflate()

        /* Throw if child count is off */
        if (childCount != 2) {
            throw IllegalArgumentException("Expected view count of 2")
        }

        /* Retrieve the recycler and the empty view */
        for (i in 0..childCount) {
            val child = getChildAt(i)
            if (child is RecyclerView) {
                recycler = child
            } else if (child != null) {
                empty = child
            }
        }

        /* Throw if we could not find both */
        if (recycler == null || empty == null) {
            throw IllegalArgumentException("Unable to find recycler and empty view")
        }
    }

    public fun setRecyclerAdapter(newAdapter: Adapter<out RecyclerView.ViewHolder>?) {
        /* Unregister the old adapter */
        // adapter?.unregisterAdapterDataObserver(observer)

        /* Set the new adapter */
        adapter = newAdapter
        recycler!!.adapter = newAdapter

        /* Register for the new adapter */
        // newAdapter?.registerAdapterDataObserver(observer)

        /* Let's find out about the new adapter */
        invalidateVisibility(false)
    }

    private fun invalidateVisibility(animate: Boolean = true) {
        val isEmpty = adapter?.isEmpty() ?: true
        val oldVisibility = empty!!.visibility
        val newVisibility = calculateNewEmptyVisibility(isEmpty)

        if (oldVisibility != newVisibility) {
            val newAlpha = alphaFromVisibility(newVisibility)
            if (animate) {
                animateView(empty!!, newVisibility, newAlpha)
            } else {
                ViewCompat.setAlpha(empty, newAlpha)
                empty!!.visibility = newVisibility
            }
        }
    }

    private fun animateView(view: View, visibility: Int, alpha: Float) {
        ViewCompat.animate(view)
                .withStartAction { view.visibility = View.VISIBLE }
                .withEndAction { view.visibility = visibility }
                .alpha(alpha)
                .setDuration(200)
                .start()
    }

    private fun alphaFromVisibility(visibility: Int): Float =
            if (visibility == View.VISIBLE) 1f else 0f

    private fun calculateNewEmptyVisibility(isEmpty: Boolean): Int =
            if (isEmpty) View.VISIBLE else View.GONE

    public abstract class Adapter<VH : RecyclerView.ViewHolder> : RecyclerView.Adapter<VH>() {
        public abstract fun isEmpty(): Boolean
    }
}