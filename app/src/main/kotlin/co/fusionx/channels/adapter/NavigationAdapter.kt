package co.fusionx.channels.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.fusionx.channels.R
import co.fusionx.channels.relay.ClientChild
import co.fusionx.channels.relay.ClientHost
import co.fusionx.channels.view.EmptyViewRecyclerViewLayout
import timber.log.Timber

public class NavigationAdapter(
        private val context: Context,
        clientClickListener: (ClientHost) -> Unit,
        childClickListener: (ClientChild) -> Unit) :
        EmptyViewRecyclerViewLayout.Adapter<NavigationAdapter.ViewHolder>() {

    internal var currentType: Int = VIEW_TYPE_CLIENT
        private set

    private val inflater: LayoutInflater

    private val childAdapter: NavigationChildAdapter
    private val clientAdapter: NavigationClientAdapter

    private val headerCount = 1
    private val footerCount = 0
    private val contentCount: Int
        get() {
            if (currentType == VIEW_TYPE_CHILD) {
                return childAdapter.itemCount
            } else {
                return clientAdapter.itemCount
            }
        }

    private val observer = ChildAdapterObserver()

    init {
        inflater = LayoutInflater.from(context)

        clientAdapter = NavigationClientAdapter(context, clientClickListener)
        childAdapter = NavigationChildAdapter(context, childClickListener)

        clientAdapter.registerAdapterDataObserver(observer)
    }

    fun updateCurrentType(type: Int) {
        if (type == currentType) return

        if (currentType == VIEW_TYPE_CLIENT) {
            clientAdapter.unregisterAdapterDataObserver(observer)
        } else if (currentType == VIEW_TYPE_CHILD) {
            childAdapter.stopObserving()
            childAdapter.unregisterAdapterDataObserver(observer)
        } else {
            Timber.e("This should not be happening.")
        }

        notifyItemRangeRemoved(headerCount, contentCount + footerCount)
        currentType = type
        notifyItemRangeInserted(headerCount, contentCount + footerCount)

        if (currentType == VIEW_TYPE_CHILD) {
            childAdapter.startObserving()
            childAdapter.registerAdapterDataObserver(observer)
        } else if (currentType == VIEW_TYPE_CHILD) {
            clientAdapter.registerAdapterDataObserver(observer)
        } else {
            Timber.e("This should not be happening.")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, type: Int): ViewHolder? = when (type) {
        VIEW_TYPE_HEADER -> HeaderViewHolder(
                inflater.inflate(R.layout.navigation_header_clients, parent, false))
        VIEW_TYPE_CLIENT -> clientAdapter.onCreateViewHolder(parent, type)
        VIEW_TYPE_CLIENT_FOOTER -> ClientFooterViewHolder(
                inflater.inflate(R.layout.navigation_client_footer, parent, false))
        VIEW_TYPE_CHILD -> childAdapter.onCreateViewHolder(parent, type)
        VIEW_TYPE_DIVIDER -> DividerViewHolder(
                inflater.inflate(R.layout.recycler_divider, parent, false))
        else -> null
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        if (viewType == VIEW_TYPE_CHILD) {
            childAdapter.bindViewHolder(holder, position - headerCount)
        } else if (viewType == VIEW_TYPE_CLIENT) {
            clientAdapter.bindViewHolder(holder, position - headerCount)
        } else {
            holder.bind(position)
        }
    }

    override fun isEmpty(): Boolean = false
    override fun getItemCount(): Int = headerCount + contentCount + footerCount

    override fun getItemViewType(position: Int): Int =
            if (position < headerCount)
                VIEW_TYPE_HEADER
            else if (position < headerCount + contentCount)
                currentType
            else
                VIEW_TYPE_DIVIDER

    inner class HeaderViewHolder(itemView: View) : ViewHolder(itemView) {
        private val background = itemView.findViewById(R.id.view_navigation_drawer_header_image)

        override fun bind(position: Int) {
            /*
            background.setOnClickListener {
                if (relayHost.selectedClient != null) {
                    updateCurrentType(if (currentType == VIEW_TYPE_CHILD) VIEW_TYPE_CLIENT else VIEW_TYPE_CHILD)
                }
            }
            */
        }
    }

    class ClientFooterViewHolder(itemView: View) : ViewHolder(itemView) {
        override fun bind(position: Int) {
            throw UnsupportedOperationException()
        }
    }

    class DividerViewHolder(itemView: View) : ViewHolder(itemView) {
        override fun bind(position: Int) {
            throw UnsupportedOperationException()
        }
    }

    abstract class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        public abstract fun bind(position: Int)
    }

    companion object {
        const val VIEW_TYPE_HEADER: Int = 0
        const val VIEW_TYPE_CLIENT: Int = 1
        const val VIEW_TYPE_CLIENT_FOOTER: Int = 2
        const val VIEW_TYPE_CHILD: Int = 3
        const val VIEW_TYPE_DIVIDER: Int = 4
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
            for (i in 0..itemCount) {
                notifyItemMoved(fromPosition + i + headerCount, toPosition + i + headerCount)
            }
        }
    }
}