package co.fusionx.channels.adapter

import android.content.Context
import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import co.fusionx.channels.R
import co.fusionx.channels.base.relayHost
import co.fusionx.channels.databinding.NavigationClientChildrenBinding
import co.fusionx.channels.databinding.ObservableListAdapterProxy
import co.fusionx.channels.relay.ClientChild
import co.fusionx.channels.relay.ClientHost

class NavigationChildAdapter(
        private val context: Context,
        private val childClickListener: (ClientChild) -> Unit) :
        RecyclerView.Adapter<NavigationAdapter.ViewHolder>() {

    private val inflater: LayoutInflater
    private val selectedClient: ClientHost?
        get() = context.relayHost.selectedClient.get()

    private val listener = ObservableListAdapterProxy<ClientChild>(this)

    init {
        inflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ChildViewHolder {
        return ChildViewHolder(DataBindingUtil.inflate<NavigationClientChildrenBinding>(
                inflater, R.layout.navigation_client_children, parent, false))
    }

    override fun onBindViewHolder(holder: NavigationAdapter.ViewHolder, position: Int) = Unit

    override fun getItemCount(): Int = selectedClient?.children?.size ?: 0

    inner class ChildViewHolder(private val binding: NavigationClientChildrenBinding) :
            NavigationAdapter.ViewHolder(binding.root) {
        override fun bind(position: Int) {
            binding.child = selectedClient!!.children[position]
            binding.root.setOnClickListener { childClickListener(binding.child) }
        }
    }
}