package co.fusionx.channels.databinding

import android.support.v7.widget.RecyclerView

open class SortedListAdapterProxy(private val adapter: RecyclerView.Adapter<*>) : SortedListCallbackRegistry.Callback {
    override fun onChanged(position: Int, count: Int) {
        adapter.notifyDataSetChanged()
    }

    override fun onInserted(position: Int, count: Int) {
        adapter.notifyItemRangeInserted(position, count)
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        adapter.notifyItemMoved(fromPosition, toPosition)
    }

    override fun onRemoved(position: Int, count: Int) {
        adapter.notifyItemRangeRemoved(position, count)
    }
}