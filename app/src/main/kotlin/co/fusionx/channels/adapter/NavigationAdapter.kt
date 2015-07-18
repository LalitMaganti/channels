package co.fusionx.channels.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ExpandableListView
import android.widget.TextView
import co.fusionx.channels.R
import co.fusionx.channels.base.objectProvider
import co.fusionx.channels.observable.ObservableList
import co.fusionx.channels.relay.ClientChild
import co.fusionx.channels.relay.ClientHost
import co.fusionx.channels.relay.RelayHost
import co.fusionx.channels.view.EmptyViewRecyclerViewLayout

public class NavigationAdapter(
        private val context: Context,
        private val clientClickListener: (ClientHost) -> Unit,
        private val childClickListener: (ClientChild) -> Unit) :
        EmptyViewRecyclerViewLayout.Adapter<NavigationAdapter.ViewHolder>(),
        ObservableList.Observer {

    internal var currentType: Int = VIEW_TYPE_CLIENT
        private set

    private val inflater = lazy(LazyThreadSafetyMode.NONE) { LayoutInflater.from(context) }
    private val relayHost: RelayHost

    private val headerCount: Int
        get() = 1
    private val contentCount: Int
        get() = if (currentType == VIEW_TYPE_CHILD) {
            relayHost.selectedClient!!.children.size
        } else {
            relayHost.clients.size
        }
    private val footerCount: Int
        get() = if (currentType == VIEW_TYPE_CHILD) 0 else 0

    init {
        relayHost = context.objectProvider.relayHost()
        relayHost.clients.addObserver(object : ObservableList.Observer {
            override fun onAdd(position: Int) {
                if (currentType == VIEW_TYPE_CLIENT) notifyItemInserted(headerCount + position)
            }
        })
    }

    fun updateCurrentType(type: Int) {
        notifyItemRangeRemoved(headerCount, contentCount + footerCount)
        currentType = type
        notifyItemRangeInserted(headerCount, contentCount + footerCount)

        if (currentType == VIEW_TYPE_CHILD) {
            relayHost.selectedClient!!.children.forEach { it.buffer.addObserver(this) }
        } else {
            relayHost.selectedClient?.children?.forEach { it.buffer.removeObserver(this) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, type: Int): ViewHolder? = when (type) {
        VIEW_TYPE_HEADER -> HeaderViewHolder(
                inflater.value.inflate(R.layout.navigation_header_clients, parent, false))
        VIEW_TYPE_CLIENT -> ClientViewHolder(
                inflater.value.inflate(R.layout.navigation_client, parent, false))
        VIEW_TYPE_CLIENT_FOOTER -> ClientFooterViewHolder(
                inflater.value.inflate(R.layout.navigation_client_footer, parent, false))
        VIEW_TYPE_CHILD -> ChildViewHolder(
                inflater.value.inflate(R.layout.navigation_client_children, parent, false))
        VIEW_TYPE_DIVIDER -> DividerViewHolder(
                inflater.value.inflate(R.layout.recycler_divider, parent, false))
        else -> null
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun isEmpty(): Boolean = false
    override fun getItemCount(): Int = headerCount + contentCount + footerCount

    override fun getItemViewType(position: Int): Int {
        if (position == 0) return VIEW_TYPE_HEADER
        return currentType
    }

    inner class HeaderViewHolder(itemView: View) : ViewHolder(itemView) {
        private val background = itemView.findViewById(R.id.view_navigation_drawer_header_image)

        override fun bind(position: Int) {
            background.setOnClickListener {
                if (relayHost.selectedClient != null) {
                    updateCurrentType(if (currentType == VIEW_TYPE_CHILD) VIEW_TYPE_CLIENT else VIEW_TYPE_CHILD)
                }
            }
        }
    }

    inner class ClientViewHolder(itemView: View) : ViewHolder(itemView) {
        private val title = itemView.findViewById(R.id.drawer_client_title) as TextView
        private val status = itemView.findViewById(R.id.drawer_client_status) as TextView

        override fun bind(position: Int) {
            val item = relayHost.clients[position - headerCount]
            itemView.setOnClickListener { clientClickListener(item) }
        }
    }

    inner class ChildViewHolder(itemView: View) : ViewHolder(itemView) {
        private val title = itemView.findViewById(R.id.drawer_client_children_title) as TextView
        private val message = itemView.findViewById(R.id.drawer_client_children_message) as TextView

        override fun bind(position: Int) {
            val child = relayHost.selectedClient!!.children[position - headerCount]
            title.text = child.name
            message.text = child.message
            itemView.setOnClickListener { childClickListener(child) }
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
        val VIEW_TYPE_HEADER: Int = 0
        val VIEW_TYPE_CLIENT: Int = 1
        val VIEW_TYPE_CLIENT_FOOTER: Int = 2
        val VIEW_TYPE_CHILD: Int = 3
        val VIEW_TYPE_DIVIDER: Int = 4
    }

    override fun onAdd(position: Int) {
        notifyItemRangeChanged(headerCount, contentCount)
    }
}