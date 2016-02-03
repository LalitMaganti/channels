package co.fusionx.channels.viewmodel.persistent

import android.content.Context
import android.databinding.Observable
import android.databinding.ObservableField
import co.fusionx.channels.databinding.ObservableSortedList
import co.fusionx.channels.databinding.SortedListCallbackRegistry
import co.fusionx.channels.db.connectionDb
import co.fusionx.channels.model.Client
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
public class RelayVM @Inject constructor(private val context: Context) {
    public val clients: ObservableSortedList<ClientVM> = ObservableSortedList(
            ClientVM::class.java, SortedListCallbackRegistry(ClientComparator.instance))
    public val selectedClient: SelectedClientsVM = SelectedClientsVM()
    public val clientCount: ObservableField<Int> = ObservableField(0)

    init {
        context.connectionDb.getConfigurations()
                .map { c -> Array(c.size) { ClientVM(Client(c[it])) } }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    clients.clear()
                    clients.addAll(it, true)
                }
    }

    public fun select(client: ClientVM): Boolean {
        if (selectedClient.latest == client) {
            return true
        }
        selectedClient.select(client)

        val newConnect = client.onSelected()
        if (newConnect) {
            clientCount.set(clientCount.get())
        }
        return false
    }

    private class ClientComparator private constructor() : SortedListCallbackRegistry.Comparator<ClientVM> {
        override fun areItemsTheSame(item1: ClientVM, item2: ClientVM): Boolean {
            return item1.areItemsTheSame(item2)
        }

        override fun areContentsTheSame(oldItem: ClientVM, newItem: ClientVM): Boolean {
            return oldItem.areContentsTheSame(newItem)
        }

        override fun compare(o1: ClientVM, o2: ClientVM): Int {
            return o1.compareTo(o2)
        }

        companion object {
            public val instance by lazy { ClientComparator() }
        }
    }
}
