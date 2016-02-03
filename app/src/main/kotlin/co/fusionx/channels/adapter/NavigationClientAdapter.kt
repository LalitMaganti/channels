package co.fusionx.channels.adapter

import android.content.Context
import android.support.v7.util.SortedList
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import co.fusionx.channels.base.relayVM
import co.fusionx.channels.databinding.NavigationClientBinding
import co.fusionx.channels.model.Client
import co.fusionx.channels.viewmodel.persistent.ClientVM

class NavigationClientAdapter(
        private val context: Context,
        private val clientClickListener: (ClientVM) -> Unit) : RecyclerView.Adapter<NavigationAdapter.ViewHolder>() {

    private val inflater: LayoutInflater

    private val clients: SortedList<ClientVM>
        get() = context.relayVM.clients

    init {
        inflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ClientViewHolder {
        return ClientViewHolder(NavigationClientBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: NavigationAdapter.ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return clients.size()
    }

    override fun getItemViewType(position: Int): Int {
        return 3
    }

    inner class ClientViewHolder(val binding: NavigationClientBinding) : NavigationAdapter.ViewHolder(binding.root) {
        override fun bind(position: Int) {
            val item = clients[position]

            binding.client = item
            binding.root.setOnClickListener { clientClickListener(item) }

            binding.executePendingBindings()
        }
    }
}