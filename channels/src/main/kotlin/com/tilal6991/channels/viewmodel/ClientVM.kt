package com.tilal6991.channels.viewmodel

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.ObservableField
import android.os.Handler
import com.tilal6991.channels.BR
import com.tilal6991.channels.R
import com.tilal6991.channels.configuration.ChannelsConfiguration
import com.tilal6991.channels.util.failAssert
import com.tilal6991.channels.viewmodel.helper.UserMessageParser
import com.tilal6991.listen.Listener
import com.tilal6991.relay.ClientGenerator
import com.tilal6991.relay.EventListener
import com.tilal6991.relay.MetaListener
import com.tilal6991.relay.RelayClient
import timber.log.Timber

class ClientVM(private val context: Context,
               private val client: RelayClient,
               private val userMessageParser: UserMessageParser,
               val configuration: ChannelsConfiguration,
               val server: ServerVM,
               val channelManager: ChannelManagerVM) : BaseObservable(), EventListener, MetaListener {

    val name: CharSequence
        get() = configuration.name
    val hostname: CharSequence
        get() = configuration.server.hostname

    var status: String = context.getString(CONNECTING)
        @Bindable get

    var statusInt: Int = CONNECTING
        @Bindable get

    var active: Boolean = true
        @Bindable get

    val selectedChild: ObservableField<ClientChildVM>

    private val reconnectHandler: ReconnectHandler
    private val statusDispatcher: StatusListenerDispatcher

    init {
        selectedChild = ObservableField(server)
        reconnectHandler = ReconnectHandler()

        statusDispatcher = StatusListenerDispatcher()
        statusDispatcher.addListener(server)
        statusDispatcher.addListener(channelManager)

        client.init()
        client.connect()

        statusDispatcher.onConnecting()
    }

    fun select(child: ClientChildVM) {
        selectedChild.set(child)
    }

    fun disconnect() {
        if (statusInt == DISCONNECTED || statusInt == DISCONNECTING) {
            return
        } else if (statusInt == RECONNECTING) {
            // Because the reconnecting state is one which we introduced, we cannot
            // delegate to the RelayClient and wait for a response.
            updateStatus(DISCONNECTED)
            statusDispatcher.onDisconnected()
        }

        if (statusInt == CONNECTED) {
            client.send(ClientGenerator.quit())
        }
        client.disconnect()

        updateStatus(DISCONNECTING)
        statusDispatcher.onDisconnecting()
    }

    fun reconnect() {
        reconnectHandler.resetCounter()
        internalReconnect()
    }

    fun close() {
        active = false
        client.close()
    }

    fun partSelected() {
        val child = selectedChild.get()
        if (child is ChannelVM) {
            client.send(ClientGenerator.part(child.name))
        } else {
            Timber.asTree().failAssert()
        }
    }

    fun sendUserMessage(message: String, context: ClientChildVM) {
        val line = userMessageParser.parse(message, context, server) ?: return
        client.send(line)
    }

    // Event handling.
    override fun onSocketConnect() {
        updateStatus(SOCKET_CONNECTED)
        reconnectHandler.onSocketConnect()

        statusDispatcher.onSocketConnect()
    }

    override fun onDisconnect() {
        statusDispatcher.onDisconnected()

        if (statusInt == DISCONNECTING) {
            updateStatus(DISCONNECTED)
        } else if (reconnectHandler.onConnectionLost()) {
            updateStatus(RECONNECTING)
            statusDispatcher.onReconnecting()
        }
    }

    override fun onConnectFailed() {
        if (statusInt == DISCONNECTING) {
            statusDispatcher.onDisconnected()

            updateStatus(DISCONNECTED)
        } else {
            statusDispatcher.onConnectFailed()

            if (reconnectHandler.onConnectionLost()) {
                updateStatus(RECONNECTING)
                statusDispatcher.onReconnecting()
            }
        }
    }

    override fun onWelcome(target: String, text: String) {
        updateStatus(CONNECTED)
    }

    private fun internalReconnect() {
        updateStatus(CONNECTING)
        client.connect()

        statusDispatcher.onConnecting()
    }

    private fun updateStatus(newStatus: Int) {
        statusInt = newStatus
        status = context.getString(newStatus)

        notifyPropertyChanged(BR.statusInt)
        notifyPropertyChanged(BR.status)
    }

    inner class ReconnectHandler {
        private var reconnectCount: Int
        private val handler: Handler

        init {
            reconnectCount = 0
            handler = Handler()
        }

        fun onSocketConnect() {
            resetCounter()
        }

        fun onConnectionLost(): Boolean {
            return onTryReconnect()
        }

        private fun onTryReconnect(): Boolean {
            if (reconnectCount >= 3) {
                return false
            }

            handler.postDelayed({
                // Check if we still want to proceed with reconnection.
                if (statusInt == RECONNECTING) {
                    reconnectCount++
                    internalReconnect()
                }
            }, 5000)
            return true
        }

        fun resetCounter() {
            reconnectCount = 0
        }
    }

    @Listener
    interface StatusListener {
        fun onSocketConnect() = Unit
        fun onConnectFailed() = Unit
        fun onDisconnecting() = Unit
        fun onDisconnected() = Unit
        fun onConnecting() = Unit
        fun onReconnecting() = Unit
    }

    companion object {
        const val CONNECTING: Int = R.string.status_connecting
        const val SOCKET_CONNECTED: Int = R.string.status_socket_connected
        const val CONNECTED: Int = R.string.status_connected
        const val DISCONNECTING: Int = R.string.status_disconnecting
        const val DISCONNECTED: Int = R.string.status_disconnected
        const val RECONNECTING: Int = R.string.status_reconnecting
    }
}