package com.tilal6991.channels.viewmodel

import android.content.Context
import android.content.Intent
import android.support.v4.util.SimpleArrayMap
import android.support.v7.util.SortedList
import com.tilal6991.channels.collections.ObservableSortedArrayMap
import com.tilal6991.channels.collections.ObservableSortedList
import com.tilal6991.channels.configuration.ChannelsConfiguration
import com.tilal6991.channels.configuration.UserConfiguration
import com.tilal6991.channels.db.connectionDb
import com.tilal6991.channels.relay.*
import com.tilal6991.channels.ui.NotificationService
import com.tilal6991.channels.util.ChannelComparator
import com.tilal6991.channels.util.ConfigurationComparator
import com.tilal6991.channels.viewmodel.helper.UserMessageParser
import com.tilal6991.messageloop.AndroidHandlerMessageLoop
import com.tilal6991.relay.RelayClient
import de.duenndns.ssl.MemorizingTrustManager
import rx.android.schedulers.AndroidSchedulers
import java.util.*

class RelayVM(private val context: Context) {

    val activeConfigs: ObservableSortedList<ChannelsConfiguration> = ObservableSortedList(
            ChannelsConfiguration::class.java, ConfigurationComparator.Companion.instance)
    val inactiveConfigs: ObservableSortedList<ChannelsConfiguration> = ObservableSortedList(
            ChannelsConfiguration::class.java, ConfigurationComparator.Companion.instance)

    val selectedClients: SelectedClientsVM = SelectedClientsVM()

    val configActiveClients = SimpleArrayMap<ChannelsConfiguration, ClientVM>()

    init {
        context.connectionDb.getConfigurations()
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
            val index = newConfigs.binarySearch { it.name.compareTo(c.name) }
            if (index < 0) {
                indices.add(i)
            }
        }
        for (i in 0..indices.size - 1) {
            inactiveConfigs.removeAt(indices[i] - i)
        }

        // Add all new items.
        for (i in 0..newConfigs.size - 1) {
            val c = newConfigs[i]
            val index = activeConfigs.binarySearch { it.name.compareTo(c.name) }
            if (index < 0) {
                val inactIndex = inactiveConfigs.binarySearch { it.name.compareTo(c.name) }
                if (inactIndex >= 0) {
                    inactiveConfigs.updateItemAt(inactIndex, c)
                } else {
                    inactiveConfigs.add(c)
                }
            }
        }

        inactiveConfigs.endBatchedUpdates()
    }

    fun select(configuration: ChannelsConfiguration): Boolean {
        val index = inactiveConfigs.indexOf(configuration)
        var client = configActiveClients[configuration]

        if (client == null) {
            client = createClient(configuration)
            configActiveClients.put(configuration, client)

            val item = inactiveConfigs.removeAt(index)
            activeConfigs.add(item)
        }

        selectedClients.select(client)
        client.select(client.server)

        // Start the service for notifications if this is the first client added.
        if (activeConfigs.size == 1) {
            val app = context.applicationContext
            app.startService(Intent(app, NotificationService::class.java))
        }
        return index != SortedList.INVALID_POSITION
    }

    fun reconnectSelected() {
        selectedClients.latest!!.reconnect()
    }

    fun disconnectSelected() {
        selectedClients.latest!!.disconnect()
    }

    fun closeSelected() {
        val latest = selectedClients.latest!!
        latest.close()
        selectedClients.closeSelected()

        val configuration = latest.configuration
        configActiveClients.remove(configuration)
        activeConfigs.remove(configuration)
        inactiveConfigs.add(configuration)

        if (activeConfigs.isEmpty()) {
            val app = context.applicationContext
            app.stopService(Intent(app, NotificationService::class.java))
        }
    }

    private fun createClient(configuration: ChannelsConfiguration): ClientVM {
        val user = configuration.user
        val relayConfig = RelayClient.Configuration.create {
            hostname = configuration.server.hostname
            port = configuration.server.port

            ssl = configuration.server.ssl
            sslTrustManager = if (ssl) MemorizingTrustManager(context) else null
        }

        val core = RelayClient.create(relayConfig, { AndroidHandlerMessageLoop.create(it) })

        val channels = ObservableSortedArrayMap<String, ChannelVM>(
                Comparator { o, t -> o.compareTo(t) }, ChannelComparator.instance)
        val cManager = ChannelManagerVM(user.nicks[0], core.registrationDao, channels)
        val server = ServerVM("Server")
        val parser = UserMessageParser(cManager)

        val clientVM = ClientVM(context, core, parser, configuration, server, cManager)

        val authHandler: AuthHandler
        if (user.authType == UserConfiguration.SASL_AUTH_TYPE) {
            authHandler = PlainSASLHandler(core, user.authUsername!!, user.authPassword!!)
        } else if (user.authType == UserConfiguration.NICKSERV_AUTH_TYPE) {
            authHandler = NickServHandler(core, user.authPassword!!)
        } else {
            authHandler = EMPTY_AUTH_HANDLER
        }

        val basicEventListener = BasicEventListener(core)
        val handshakeListener = HandshakeEventListener(core, configuration, authHandler)
        val mainThreadListener = MainThreadEventListener()

        core.addEventListener(basicEventListener)
        core.addEventListener(handshakeListener)
        core.addEventListener(mainThreadListener)

        core.addMetaListener(handshakeListener)
        core.addMetaListener(mainThreadListener)

        mainThreadListener.addMetaListener(clientVM)
        mainThreadListener.addEventListener(clientVM)
        mainThreadListener.addEventListener(server)
        mainThreadListener.addEventListener(cManager)

        return clientVM
    }
}
