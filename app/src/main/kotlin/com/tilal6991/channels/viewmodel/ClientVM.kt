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

    fun reconnect() {
        reconnectHandler.resetCounter()
        internalReconnect()
    }

    fun disconnect() {
        if (statusInt == DISCONNECTED) {
            return
        }
        client.send(ClientGenerator.quit())
        client.disconnect()
    }

    fun close() {
        client.close()
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

    fun sendUserMessage(message: String, context: ClientChildVM) {
        val line = userMessageParser.parse(message, context, server) ?: return
        client.send(line)
    }

    override fun onSocketConnect() {
        updateStatus(SOCKET_CONNECTED)
        reconnectHandler.onSocketConnect()

        server.onSocketConnect()
    }

    override fun onAlreadyDisconnected() {
        val oldStatus = statusInt
        updateStatus(DISCONNECTED)

        if (oldStatus != DISCONNECTED) {
            server.onDisconnect()
        }
    }

    override fun onAlreadyConnected() {
        // Intentional do nothing here.
    }

    override fun onDisconnect(triggered: Boolean) {
        if (triggered) {
            updateStatus(DISCONNECTED)
            server.onDisconnect()
        } else {
            server.onDisconnect()

            if (reconnectHandler.onUnexpectedDisconnect()) {
                updateStatus(RECONNECTING)
                server.onReconnecting()
            }
        }
    }

    override fun onConnectFailed() {
        server.onConnectFailed()

        if (reconnectHandler.onConnectFailed()) {
            updateStatus(RECONNECTING)
            server.onReconnecting()
        }
    }

    override fun onWelcome(target: String, text: String) {
        updateStatus(CONNECTED)
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

        fun onConnectFailed(): Boolean {
            return onTryReconnect()
        }

        fun onUnexpectedDisconnect(): Boolean {
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
        const val DISCONNECTED: Int = R.string.status_disconnected
        const val RECONNECTING: Int = R.string.status_reconnecting
    }
}