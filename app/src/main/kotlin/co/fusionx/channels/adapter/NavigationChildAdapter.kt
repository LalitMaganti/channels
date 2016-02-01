package co.fusionx.channels.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import co.fusionx.channels.R
import co.fusionx.channels.base.objectProvider
import co.fusionx.channels.observable.ObservableList
import co.fusionx.channels.relay.ClientChild
import co.fusionx.channels.relay.ClientHost
import co.fusionx.channels.view.EmptyViewRecyclerViewLayout

class NavigationChildAdapter(
        private val context: Context,
        private val childClickListener: (ClientChild) -> Unit) :
        RecyclerView.Adapter<NavigationAdapter.ViewHolder>(),
        ObservableList.Observer {

    private val inflater: LayoutInflater
    private val selectedClient: ClientHost?
        get() = context.objectProvider.relayHost().selectedClient

    init {
        inflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): NavigationAdapter.ViewHolder
            = ChildViewHolder(inflater.inflate(R.layout.navigation_client_children, parent, false))

    override fun onBindViewHolder(holder: NavigationAdapter.ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = selectedClient?.children?.size ?: 0

    inner class ChildViewHolder(itemView: View) : NavigationAdapter.ViewHolder(itemView) {
        private val title = itemView.findViewById(R.id.drawer_client_children_title) as TextView
        private val message = itemView.findViewById(R.id.drawer_client_children_message) as TextView

        override fun bind(position: Int) {
            val child = selectedClient!!.children[position]
            title.text = child.name
            message.text = child.message
            itemView.setOnClickListener { childClickListener(child) }
        }
    }

    fun startObserving() {
        selectedClient!!.children.forEach { it.buffer.addObserver(this) }
    }

    fun stopObserving() {
        selectedClient?.children?.forEach { it.buffer.removeObserver(this) }
    }

    override fun onAdd(position: Int) {
        notifyItemRangeChanged(0, itemCount)
    }
}