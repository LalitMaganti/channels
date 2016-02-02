package co.fusionx.channels.model

import android.content.Context
import android.databinding.ObservableField
import android.support.v7.util.SortedList
import co.fusionx.channels.databinding.SortedListCallbackRegistry
import co.fusionx.channels.db.connectionDb
import co.fusionx.channels.util.compareTo
import co.fusionx.relay.RelayClient
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
public class RelayHost @Inject constructor(private val context: Context) {
    private val dispatcher = SortedListCallbackRegistry(clientComparator.value)

    public val clients: SortedList<Client> = SortedList(Client::class.java, dispatcher)
    public val selectedClient: ObservableField<Client?> = ObservableField(null)

    init {
        context.connectionDb.getConfigurations()
                .map { c -> Array(c.size) { Client(c[it]) } }
                .subscribe {
                    clients.beginBatchedUpdates()
                    clients.clear()
                    clients.addAll(it, true)
                    clients.endBatchedUpdates()
                }
    }

    public fun addClientObserver(callback: SortedListCallbackRegistry.Callback) {
        dispatcher.addCallback(callback)
    }

    fun removeClientObserver(callback: SortedListCallbackRegistry.Callback) {
        dispatcher.removeCallback(callback)
    }

    public fun select(client: Client): Boolean {
        if (selectedClient.get() == client) return true

        selectedClient.set(client)
        client.onSelected()
        return false
    }

    companion object {
        private val clientComparator = lazy { ClientComparator() }
    }

    private class ClientComparator : SortedListCallbackRegistry.Comparator<Client> {
        override fun areItemsTheSame(item1: Client, item2: Client): Boolean {
            return item1.name == item2.name
        }

        override fun areContentsTheSame(oldItem: Client, newItem: Client): Boolean {
            return oldItem.name == newItem.name
        }

        override fun compare(o1: Client, o2: Client): Int {
            return o1.name.compareTo(o2.name)
        }
    }
}
