package co.fusionx.channels.viewmodel.persistent

import android.content.Context
import android.support.v4.util.ArrayMap
import android.support.v7.util.SortedList
import co.fusionx.channels.collections.ObservableSortedArrayMap
import co.fusionx.channels.collections.ObservableSortedList
import co.fusionx.channels.configuration.ChannelsConfiguration
import co.fusionx.channels.db.connectionDb
import co.fusionx.channels.relay.BasicEventListener
import co.fusionx.channels.relay.EMPTY_AUTH_HANDLER
import co.fusionx.channels.relay.HandshakeEventListener
import co.fusionx.channels.relay.MainThreadEventListener
import co.fusionx.channels.util.ChannelComparator
import co.fusionx.channels.util.ConfigurationComparator
import co.fusionx.channels.viewmodel.helper.UserMessageParser
import co.fusionx.relay.RelayClient
import co.fusionx.relay.message.AndroidMessageLoop
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton class RelayVM @Inject constructor(private val context: Context) {

    val activeConfigs: ObservableSortedList<ChannelsConfiguration> = ObservableSortedList(
            ChannelsConfiguration::class.java, ConfigurationComparator.instance)
    val inactiveConfigs: ObservableSortedList<ChannelsConfiguration> = ObservableSortedList(
            ChannelsConfiguration::class.java, ConfigurationComparator.instance)

    val selectedClients: SelectedClientsVM = SelectedClientsVM()

    val configActiveClients = ArrayMap<ChannelsConfiguration, ClientVM>()

    init {
        /* TODO(lrm113) deal with handling constantly updating databases */
        context.connectionDb.getConfigurations()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { mergeConfigs(it) }
    }

    private fun mergeConfigs(new: List<ChannelsConfiguration>) {
        if (activeConfigs.isEmpty() && inactiveConfigs.isEmpty()) {
            inactiveConfigs.addAll(new)
            return
        }

        val newConfigs = new.toMutableList()
        newConfigs.sortWith(ConfigurationComparator.instance)

        inactiveConfigs.beginBatchedUpdates()

        // Remove all configs which are no longer present.
        val indices = ArrayList<Int>()
        for (i in 0..inactiveConfigs.size - 1) {
            val c = inactiveConfigs[i]
            val index = newConfigs.binarySearch { c.name.compareTo(it.name) }
            if (index == -1) {
                indices.add(i)
            }
        }
        for (i in 0..indices.size - 1) {
            inactiveConfigs.removeAt(indices[i] - i)
        }

        // Add all new items.
        for (i in 0..newConfigs.size - 1) {
            val c = newConfigs[i]
            val index = inactiveConfigs.binarySearch { c.name.compareTo(it.name) }
            if (index < 0) {
                val actIndex = activeConfigs.binarySearch { c.name.compareTo(it.name) }
                if (actIndex < 0) {
                    inactiveConfigs.add(c)
                }
            }
        }

        inactiveConfigs.endBatchedUpdates()
    }

    fun select(configuration: ChannelsConfiguration): Boolean {
        val index = inactiveConfigs.indexOf(configuration)
        if (selectedClients.latest?.name == configuration.name) {
            return true
        }

        var client = configActiveClients[configuration]
        if (client == null) {
            client = createClient(configuration)
            configActiveClients[configuration] = client

            val item = inactiveConfigs.removeAt(index)
            activeConfigs.add(item)
        }

        selectedClients.select(client)
        client.select(client.server)
        return false
    }

    private fun createClient(configuration: ChannelsConfiguration): ClientVM {
        val relayConfig = RelayClient.Configuration.create {
            hostname = configuration.server.hostname
            port = configuration.server.port
        }

        val coreClient = RelayClient.create(relayConfig, AndroidMessageLoop.create())

        val channelMap = ObservableSortedArrayMap<String, ChannelVM>(
                Comparator { o, t -> o.compareTo(t) }, ChannelComparator.instance)
        val userChannelVM = ChannelManagerVM(configuration.user.nicks[0], channelMap)
        val server = ServerVM("Server")
        val userMessageParser = UserMessageParser(userChannelVM)

        val clientVM = ClientVM(context, configuration, coreClient, userMessageParser, server, channelMap.valuesAsObservableList())

        val basicEventListener = BasicEventListener(coreClient)
        val handshakeListener = HandshakeEventListener(coreClient, configuration, EMPTY_AUTH_HANDLER)
        val mainThreadListener = MainThreadEventListener()
        coreClient.addEventListener(basicEventListener)
        coreClient.addEventListener(handshakeListener)
        coreClient.addEventListener(mainThreadListener)

        mainThreadListener.addEventListener(clientVM)
        mainThreadListener.addEventListener(server)
        mainThreadListener.addEventListener(userChannelVM)
        return clientVM
    }
}
