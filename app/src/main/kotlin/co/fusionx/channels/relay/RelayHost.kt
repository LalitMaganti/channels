package co.fusionx.channels.relay

import android.content.Context
import android.databinding.ObservableField
import android.support.v7.util.SortedList
import co.fusionx.channels.databinding.SortedListAdapterProxy
import co.fusionx.channels.databinding.SortedListDispatcher
import co.fusionx.channels.db.connectionDb
import co.fusionx.channels.util.compareTo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
public class RelayHost @Inject constructor(private val context: Context) {
    private val dispatcher = SortedListDispatcher(clientComparator.value)

    public val clients: SortedList<ClientHost> = SortedList(ClientHost::class.java,
            SortedList.BatchedCallback(dispatcher))
    public val selectedClient: ObservableField<ClientHost?> = ObservableField(null)

    init {
        context.connectionDb.getConfigurations()
                .subscribe { it -> clients.addAll(it.map { ClientHost(it) }) }
    }

    public fun addClientObserver(callback: SortedListDispatcher.Callback) {
        dispatcher.addCallback(callback)
    }

    fun removeClientObserver(callback: SortedListDispatcher.Callback) {
        dispatcher.removeCallback(callback)
    }

    public fun select(client: ClientHost): Boolean {
        if (selectedClient.get() == client) return true

        selectedClient.set(client)
        client.onSelected()
        return false
    }

    companion object {
        private val clientComparator = lazy { ClientComparator() }
    }

    private class ClientComparator : SortedListDispatcher.Comparator<ClientHost> {
        override fun areItemsTheSame(item1: ClientHost, item2: ClientHost): Boolean {
            return item1.name == item2.name
        }

        override fun areContentsTheSame(oldItem: ClientHost, newItem: ClientHost): Boolean {
            return oldItem.name == newItem.name
        }

        override fun compare(o1: ClientHost, o2: ClientHost): Int {
            return o1.name.compareTo(o2.name)
        }
    }
}
