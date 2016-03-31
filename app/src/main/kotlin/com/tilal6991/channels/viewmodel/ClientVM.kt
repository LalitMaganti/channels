package com.tilal6991.channels.viewmodel

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.ObservableField
import android.databinding.ObservableList
import android.os.Handler
import com.tilal6991.channels.BR
import com.tilal6991.channels.R
import com.tilal6991.channels.configuration.ChannelsConfiguration
import com.tilal6991.channels.viewmodel.helper.UserMessageParser
import com.tilal6991.relay.EventListener
import com.tilal6991.relay.MetaListener
import com.tilal6991.relay.RelayClient
import com.tilal6991.relay.protocol.ClientGenerator

class ClientVM(private val context: Context,
               private val client: RelayClient,
               private val userMessageParser: UserMessageParser,
               val configuration: ChannelsConfiguration,
               val server: ServerVM,
               val channels: ObservableList<ChannelVM>) : BaseObservable(), EventListener, MetaListener {

    val name: CharSequence
        get() = configuration.name
    val hostname: CharSequence
        get() = configuration.server.hostname

    var status: String = context.getString(CONNECTING)
        @Bindable get

    var statusInt: Int = CONNECTING
        @Bindable get

    val selectedChild: ObservableField<ClientChildVM>

    private val reconnectHandler: ReconnectHandler

    init {
        selectedChild = ObservableField(server)
        reconnectHandler = ReconnectHandler()

        client.init()
        client.connect()

        server.onConnecting()
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
            server.onDisconnected()
        }

        if (statusInt == CONNECTED) {
            client.send(ClientGenerator.quit())
        }
        client.disconnect()

        updateStatus(DISCONNECTING)
        server.onDisconnecting()
    }

    fun reconnect() {
        reconnectHandler.resetCounter()
        internalReconnect()
    }

    fun close() {
        client.close()
    }

    fun sendUserMessage(message: String, context: ClientChildVM) {
        val line = userMessageParser.parse(message, context, server) ?: return
        client.send(line)
    }

    // Event handling.
    override fun onSocketConnect() {
        updateStatus(SOCKET_CONNECTED)
        reconnectHandler.onSocketConnect()

        server.onSocketConnect()
    }

    override fun onDisconnect() {
        if (statusInt == DISCONNECTING) {
            updateStatus(DISCONNECTED)

            server.onDisconnected()
        } else {
            server.onDisconnected()

            if (reconnectHandler.onConnectionLost()) {
                updateStatus(RECONNECTING)
                server.onReconnecting()
            }
        }
    }

    override fun onConnectFailed() {
        if (statusInt == DISCONNECTING) {
            server.onDisconnected()

            updateStatus(DISCONNECTED)
        } else {
            server.onConnectFailed()

            if (reconnectHandler.onConnectionLost()) {
                updateStatus(RECONNECTING)
                server.onReconnecting()
            }
        }
    }

    override fun onWelcome(target: String, text: String) {
        updateStatus(CONNECTED)
    }

    private fun internalReconnect() {
        updateStatus(CONNECTING)
        client.connect()

        server.onConnecting()
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

    companion object {
        const val CONNECTING: Int = R.string.status_connecting
        const val SOCKET_CONNECTED: Int = R.string.status_socket_connected
        const val CONNECTED: Int = R.string.status_connected
        const val DISCONNECTING: Int = R.string.status_disconnecting
        const val DISCONNECTED: Int = R.string.status_disconnected
        const val RECONNECTING: Int = R.string.status_reconnecting
    }
}