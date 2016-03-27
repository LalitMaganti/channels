package co.fusionx.channels.viewmodel

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.ObservableField
import android.databinding.ObservableList
import co.fusionx.channels.BR
import co.fusionx.channels.R
import co.fusionx.channels.configuration.ChannelsConfiguration
import co.fusionx.channels.viewmodel.helper.UserMessageParser
import co.fusionx.relay.EventListener
import co.fusionx.relay.RelayClient

class ClientVM(private val context: Context,
               private val configuration: ChannelsConfiguration,
               private val client: RelayClient,
               private val userMessageParser: UserMessageParser,
               val server: ServerVM,
               val channels: ObservableList<ChannelVM>) : BaseObservable(), EventListener {

    val name: CharSequence
        get() = configuration.name
    val hostname: CharSequence
        get() = configuration.server.hostname

    var status: String = context.getString(CONNECTING)
        @Bindable get

    val selectedChild: ObservableField<ClientChildVM>

    init {
        selectedChild = ObservableField(server)
        client.start()
    }

    fun select(child: ClientChildVM) {
        selectedChild.set(child)
    }

    private fun updateStatus(s: Int) {
        status = context.getString(s)
        notifyPropertyChanged(BR.status)
    }

    fun sendUserMessage(message: String, context: ClientChildVM) {
        val line = userMessageParser.parse(message, context, server) ?: return
        client.send(line)
    }

    override fun onSocketConnect() {
        updateStatus(SOCKET_CONNECTED)
    }

    override fun onWelcome(target: String, text: String) {
        updateStatus(CONNECTED)
    }

    companion object {
        const val CONNECTING: Int = R.string.status_connecting
        const val SOCKET_CONNECTED: Int = R.string.status_socket_connected
        const val CONNECTED: Int = R.string.status_connected
        const val RECONNECTING: Int = R.string.status_reconnecting
        const val DISCONNECTED: Int = R.string.status_disconnected
    }
}