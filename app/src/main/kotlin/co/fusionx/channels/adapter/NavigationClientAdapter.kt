package co.fusionx.channels.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.ObservableList
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import co.fusionx.channels.R
import co.fusionx.channels.base.objectProvider
import co.fusionx.channels.databinding.NavigationClientBinding
import co.fusionx.channels.databinding.ObservableListRecyclerAdapterProxy
import co.fusionx.channels.relay.ClientHost

class NavigationClientAdapter(
        private val context: Context,
        private val clientClickListener: (ClientHost) -> Unit) :
        RecyclerView.Adapter<NavigationAdapter.ViewHolder>() {

    private val inflater: LayoutInflater
    private val clients: ObservableList<ClientHost>

    private val listener = ObservableListRecyclerAdapterProxy<ClientHost>(this)

    init {
        inflater = LayoutInflater.from(context)
        clients = context.objectProvider.relayHost().clients
    }

    fun startObserving() {
        clients.addOnListChangedCallback(listener)
    }

    fun stopObserving() {
        clients.removeOnListChangedCallback(listener)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ClientViewHolder {
        return ClientViewHolder(DataBindingUtil.inflate<NavigationClientBinding>(
                inflater, R.layout.navigation_client_children, parent, false))
    }

    override fun onBindViewHolder(holder: NavigationAdapter.ViewHolder, position: Int) = Unit

    override fun getItemCount(): Int {
        return clients.size
    }

    inner class ClientViewHolder(private val binding: NavigationClientBinding) :
            NavigationAdapter.ViewHolder(binding.root) {
        override fun bind(position: Int) {
            val item = clients[position]
            binding.client = item
            binding.root.setOnClickListener { clientClickListener(item) }
        }
    }
}