package co.fusionx.channels.adapter

import android.content.Context
import android.databinding.OnRebindCallback
import android.databinding.ViewDataBinding
import android.support.v7.util.SortedList
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import co.fusionx.channels.base.relayHost
import co.fusionx.channels.databinding.NavigationClientBinding
import co.fusionx.channels.model.Client

class NavigationClientAdapter(
        private val context: Context,
        private val clientClickListener: (Client) -> Unit) : RecyclerView.Adapter<NavigationAdapter.ViewHolder>() {

    private val inflater: LayoutInflater

    private val clients: SortedList<Client> get() = context.relayHost.clients

    init {
        inflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ClientViewHolder {
        val holder = ClientViewHolder(NavigationClientBinding.inflate(inflater, parent, false))
        /*
        holder.binding.addOnRebindCallback(object : OnRebindCallback<NavigationClientBinding>() {
            public override fun onPreBind(binding: NavigationClientBinding): Boolean {
                return recyclerView != null && recyclerView.isComputingLayout;
            }

            public override fun onCanceled(binding: NavigationClientBinding) {
                if (recyclerView == null || recyclerView.isComputingLayout) {
                    return;
                }
                val position = holder.adapterPosition;
                if (position != RecyclerView.NO_POSITION) {
                    notifyItemChanged(position, DATA_INVALIDATION);
                }
            }
        });
        */
        return holder
    }

    override fun onBindViewHolder(holder: NavigationAdapter.ViewHolder, position: Int) {
        return Unit
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