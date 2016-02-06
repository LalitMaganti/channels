package co.fusionx.channels.viewmodel.persistent

import android.content.Context
import co.fusionx.channels.collections.ObservableSortedList
import co.fusionx.channels.db.connectionDb
import co.fusionx.channels.model.Client
import co.fusionx.channels.viewmodel.helper.ClientComparator
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton class RelayVM @Inject constructor(private val context: Context) {

    val activeClients: ObservableSortedList<ClientVM> = ObservableSortedList(
            ClientVM::class.java, ClientComparator.instance)
    val inactiveClients: ObservableSortedList<ClientVM> = ObservableSortedList(
            ClientVM::class.java, ClientComparator.instance)
    val selectedClients: SelectedClientsVM = SelectedClientsVM()

    init {
        /* TODO(lrm113) deal with handling constantly updating databases */
        context.connectionDb.getConfigurations()
                .first()
                .map { it -> it.map { ClientVM(context, Client(it)) } }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    inactiveClients.clear()
                    activeClients.clear()
                    inactiveClients.addAll(it)
                }
    }

    fun select(client: ClientVM): Boolean {
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
}
