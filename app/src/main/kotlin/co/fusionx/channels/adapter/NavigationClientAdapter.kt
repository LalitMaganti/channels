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
import co.fusionx.channels.relay.ClientHost

class NavigationClientAdapter(
        private val context: Context,
        private val clientClickListener: (ClientHost) -> Unit) :
        RecyclerView.Adapter<NavigationAdapter.ViewHolder>(),
        ObservableList.Observer {

    private val inflater: LayoutInflater
    private val clients: ObservableList<ClientHost>

    init {
        inflater = LayoutInflater.from(context)
        clients = context.objectProvider.relayHost().clients
    }

    fun startObserving() {
        clients.addObserver(this)
    }

    fun stopObserving() {
        clients.removeObserver(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ClientViewHolder {
        return ClientViewHolder(inflater.inflate(R.layout.navigation_client, parent, false))
    }

    override fun onBindViewHolder(holder: NavigationAdapter.ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return clients.size
    }

    override fun onAdd(position: Int) {
        notifyItemInserted(position)
    }

    inner class ClientViewHolder(itemView: View) : NavigationAdapter.ViewHolder(itemView) {
        private val title = itemView.findViewById(R.id.drawer_client_title) as TextView
        private val status = itemView.findViewById(R.id.drawer_client_status) as TextView

        override fun bind(position: Int) {
            val item = clients[position]
            title.text = item.name
            status.text = item.status

            itemView.setOnClickListener { clientClickListener(item) }
        }
    }
}