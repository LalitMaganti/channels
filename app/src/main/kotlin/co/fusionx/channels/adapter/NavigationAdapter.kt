package co.fusionx.channels.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.fusionx.channels.base.relayHost
import co.fusionx.channels.databinding.NavigationHeaderClientsBinding
import co.fusionx.channels.databinding.ViewClickListener

public class NavigationAdapter(
        private val context: Context,
        private var contentAdapter: RecyclerView.Adapter<NavigationAdapter.ViewHolder>,
        private val headerClickListener: () -> Unit) :
        RecyclerView.Adapter<NavigationAdapter.ViewHolder>() {

    private val inflater: LayoutInflater

    private val headerCount = 1
    private val contentCount: Int
        get() = contentAdapter.itemCount

    private val observer = ChildAdapterObserver()
    private val viewClickListener = ViewClickListener()

    init {
        inflater = LayoutInflater.from(context)

        contentAdapter.registerAdapterDataObserver(observer)
    }

    public fun updateContentAdapter(adapter: RecyclerView.Adapter<NavigationAdapter.ViewHolder>) {
        contentAdapter.unregisterAdapterDataObserver(observer)
        notifyItemRangeRemoved(headerCount, contentCount)

        contentAdapter = adapter

        adapter.notifyItemRangeInserted(headerCount, contentCount)
        adapter.registerAdapterDataObserver(observer)
    }

    public fun updateHeader() {
        if (context.relayHost.selectedClient.get() == null) {
            viewClickListener.headerListener = null
        } else {
            viewClickListener.headerListener = View.OnClickListener { headerClickListener() }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, type: Int): ViewHolder? = when (type) {
        VIEW_TYPE_HEADER ->
            HeaderViewHolder(NavigationHeaderClientsBinding.inflate(inflater, parent, false))
        else -> contentAdapter.onCreateViewHolder(parent, type)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        if (viewType == VIEW_TYPE_HEADER) {
            holder.bind(position)
        } else {
            holder.bind(position - headerCount)
        }
    }

    override fun getItemCount(): Int {
        return headerCount + contentCount
    }

    override fun getItemViewType(position: Int): Int {
        if (position < headerCount) {
            return VIEW_TYPE_HEADER
        }
        return contentAdapter.getItemViewType(position)
    }

    inner class HeaderViewHolder(
            private val binding: NavigationHeaderClientsBinding) : ViewHolder(binding.root) {

        override fun bind(position: Int) {
            binding.header = viewClickListener
        }
    }

    abstract class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        public abstract fun bind(position: Int)
    }

    private inner class ChildAdapterObserver : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            notifyDataSetChanged()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            notifyItemRangeChanged(positionStart + headerCount, itemCount)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            notifyItemRangeChanged(positionStart + headerCount, itemCount, payload)
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            notifyItemRangeInserted(positionStart + headerCount, itemCount)
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            notifyItemRangeRemoved(positionStart + headerCount, itemCount)
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            for (i in 0..itemCount - 1) {
                notifyItemMoved(fromPosition + i + headerCount, toPosition + i + headerCount)
            }
        }
    }

    companion object {
        const val VIEW_TYPE_HEADER: Int = 0
        const val VIEW_TYPE_CONTENT: Int = 1
    }
}