package co.fusionx.channels.viewmodel.persistent

import android.content.Context
import co.fusionx.channels.collections.ObservableSortedArrayMap
import co.fusionx.channels.collections.ObservableSortedList
import co.fusionx.channels.db.connectionDb
import co.fusionx.channels.relay.BasicEventListener
import co.fusionx.channels.relay.Configuration
import co.fusionx.channels.relay.ConnectionInformation
import co.fusionx.channels.relay.MainThreadEventListener
import co.fusionx.channels.viewmodel.helper.ChannelComparator
import co.fusionx.channels.viewmodel.helper.ClientComparator
import co.fusionx.channels.viewmodel.helper.UserMessageParser
import co.fusionx.relay.RelayClient
import co.fusionx.relay.message.AndroidMessageLoop
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*
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
                .map { it -> it.map { createClient(it) } }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    inactiveClients.clear()
                    activeClients.clear()
                    inactiveClients.addAll(it)
                }
    }

    private fun createClient(configuration: Configuration): ClientVM {
        val coreClient = RelayClient.create(configuration.connectionConfiguration, AndroidMessageLoop.create())

        val channelMap = ObservableSortedArrayMap<String, ChannelVM>(
                Comparator { o, t -> o.compareTo(t) }, ChannelComparator.instance)
        val connectionInformation = ConnectionInformation()
        val userChannelVM = UserChannelDao("tilal6993", channelMap, connectionInformation)
        val server = ServerVM("Server")
        val userMessageParser = UserMessageParser(userChannelVM)

        val clientVM = ClientVM(context, configuration, coreClient, userMessageParser, server, channelMap.valuesAsObservableList())

        val basicEventListener = BasicEventListener(coreClient)
        val mainThreadListener = MainThreadEventListener()
        coreClient.addEventListener(basicEventListener)
        coreClient.addEventListener(mainThreadListener)

        mainThreadListener.addEventListener(clientVM)
        mainThreadListener.addEventListener(server)
        mainThreadListener.addEventListener(userChannelVM)
        mainThreadListener.addEventListener(connectionInformation)
        return clientVM
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
