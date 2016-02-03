package co.fusionx.channels.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import co.fusionx.channels.base.relayVM
import co.fusionx.channels.databinding.NavigationClientChildrenBinding
import co.fusionx.channels.model.ClientChild
import co.fusionx.channels.viewmodel.persistent.ClientChildVM
import co.fusionx.channels.viewmodel.persistent.ClientVM

class NavigationChildAdapter(
        private val context: Context,
        private val childClickListener: (ClientChildVM) -> Unit) :
        RecyclerView.Adapter<NavigationAdapter.ViewHolder>() {

    private val inflater: LayoutInflater
    private val selectedClient: ClientVM?
        get() = context.relayVM.selectedClient.latest

    init {
        inflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ChildViewHolder {
        return ChildViewHolder(NavigationClientChildrenBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: NavigationAdapter.ViewHolder, position: Int) = Unit

    override fun getItemCount(): Int = selectedClient?.children?.size ?: 0

    override fun getItemViewType(position: Int): Int {
        return 2
    }

    inner class ChildViewHolder(private val binding: NavigationClientChildrenBinding) :
            NavigationAdapter.ViewHolder(binding.root) {
        override fun bind(position: Int) {
            binding.child = selectedClient!!.children[position]
            binding.executePendingBindings()

            binding.root.setOnClickListener { childClickListener(binding.child) }
        }
    }
}