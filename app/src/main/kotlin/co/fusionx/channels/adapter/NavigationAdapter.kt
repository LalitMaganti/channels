package co.fusionx.channels.adapter

import android.content.Context
import android.databinding.Observable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.fusionx.channels.R
import co.fusionx.channels.base.relayHost
import co.fusionx.channels.relay.RelayHost

public class NavigationAdapter(
        private val context: Context,
        private var contentAdapter: RecyclerView.Adapter<NavigationAdapter.ViewHolder>) :
        RecyclerView.Adapter<NavigationAdapter.ViewHolder>() {

    private val inflater: LayoutInflater

    private val headerCount = 1
    private val contentCount: Int get() = contentAdapter.itemCount

    private val observer = ChildAdapterObserver()

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

    override fun onCreateViewHolder(parent: ViewGroup?, type: Int): ViewHolder? = when (type) {
        VIEW_TYPE_HEADER -> HeaderViewHolder(
                inflater.inflate(R.layout.navigation_header_clients, parent, false))
        VIEW_TYPE_CONTENT -> contentAdapter.onCreateViewHolder(parent, type)
        else -> null
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        if (viewType == VIEW_TYPE_CONTENT) {
            holder.bind(position - headerCount)
        } else {
            holder.bind(position)
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        val position = holder.adapterPosition
        if (position == -1) return

        val viewType = getItemViewType(position)
        if (viewType == VIEW_TYPE_CONTENT) {
            holder.unbind(position - headerCount)
        } else {
            holder.unbind(position)
        }
    }

    override fun getItemCount(): Int {
        return headerCount + contentCount
    }

    override fun getItemViewType(position: Int): Int {
        if (position < headerCount) {
            return VIEW_TYPE_HEADER
        }
        return VIEW_TYPE_CONTENT
    }

    inner class HeaderViewHolder(itemView: View) : ViewHolder(itemView) {

        private val background = itemView.findViewById(R.id.view_navigation_drawer_header_image)

        override fun bind(position: Int) {
        }

        /*
        private val listener = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                onChanged()
            }
        }

        override fun bind(position: Int) {
            relayHost.selectedClient.addOnPropertyChangedCallback(listener)
            onChanged()
        }

        override fun unbind(position: Int) {
            relayHost.selectedClient.removeOnPropertyChangedCallback(listener)
        }

        fun onChanged() {
            if (relayHost.selectedClient.get() == null) {
                background.setOnClickListener(null)
            } else {
                background.setOnClickListener {
                    if (currentType == VIEW_TYPE_CHILD) {
                        updateCurrentType(VIEW_TYPE_CLIENT)
                    } else {
                        updateCurrentType(VIEW_TYPE_CHILD)
                    }
                }
            }
        }
        */
    }

    abstract class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        public abstract fun bind(position: Int)
        public open fun unbind(position: Int) = Unit
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