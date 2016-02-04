package co.fusionx.channels.viewmodel.persistent

import android.content.Context
import co.fusionx.channels.databinding.ObservableSortedList
import co.fusionx.channels.db.connectionDb
import co.fusionx.channels.model.Client
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
public class RelayVM @Inject constructor(private val context: Context) {

    public val activeClients: ObservableSortedList<ClientVM> = ObservableSortedList(
            ClientVM::class.java, ClientComparator.instance)
    public val inactiveClients: ObservableSortedList<ClientVM> = ObservableSortedList(
            ClientVM::class.java, ClientComparator.instance)
    public val selectedClients: SelectedClientsVM = SelectedClientsVM()

    init {
        /* TODO(lrm113) deal with handling constantly updating databases */
        context.connectionDb.getConfigurations()
                .first()
                .map { it -> it.map { ClientVM(Client(it)) } }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    inactiveClients.clear()
                    activeClients.clear()
                    inactiveClients.addAll(it)
                }
    }

    public fun select(client: ClientVM): Boolean {
        val index = inactiveClients.indexOf(client)
        if (selectedClients.latest == client) {
            return true
        }
        selectedClients.select(client)

        val newConnect = client.onSelected()
        if (newConnect) {
            val item = inactiveClients.removeAt(index)
            activeClients.add(item)
        }
        return false
    }

    private class ClientComparator private constructor() : ObservableSortedList.HyperComparator<ClientVM> {
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
