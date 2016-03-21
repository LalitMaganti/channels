package co.fusionx.channels.viewmodel.persistent

import android.content.Context
import android.support.v4.util.ArrayMap
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
                .first()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    inactiveConfigs.clear()
                    activeConfigs.clear()
                    inactiveConfigs.addAll(it)
                }
    }

    private fun createClient(configuration: ChannelsConfiguration): ClientVM {
        val relayConfig = RelayClient.Configuration.create {
            hostname = configuration.connection.hostname
            port = configuration.connection.port
        }

        val coreClient = RelayClient.create(relayConfig, AndroidMessageLoop.create())

        val channelMap = ObservableSortedArrayMap<String, ChannelVM>(
                Comparator { o, t -> o.compareTo(t) }, ChannelComparator.instance)
        val userChannelVM = ChannelManagerVM(configuration.user.nicks[0], channelMap)
        val server = ServerVM("Server")
        val userMessageParser = UserMessageParser(userChannelVM)

        val clientVM = ClientVM(context, configuration, coreClient, userMessageParser, server, channelMap.valuesAsObservableList())

        val basicEventListener = BasicEventListener(coreClient)
        val handshakeListener = HandshakeEventListener(coreClient, configuration.user, EMPTY_AUTH_HANDLER)
        val mainThreadListener = MainThreadEventListener()
        coreClient.addEventListener(basicEventListener)
        coreClient.addEventListener(handshakeListener)
        coreClient.addEventListener(mainThreadListener)

        mainThreadListener.addEventListener(clientVM)
        mainThreadListener.addEventListener(server)
        mainThreadListener.addEventListener(userChannelVM)
        return clientVM
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
}
