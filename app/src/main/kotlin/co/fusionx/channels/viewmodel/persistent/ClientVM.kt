package co.fusionx.channels.viewmodel.persistent

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.ObservableField
import android.databinding.ObservableList
import co.fusionx.channels.BR
import co.fusionx.channels.R
import co.fusionx.channels.relay.configuration.Configuration
import co.fusionx.channels.viewmodel.helper.UserMessageParser
import co.fusionx.relay.EventListener
import co.fusionx.relay.RelayClient

class ClientVM(private val context: Context,
               private val configuration: Configuration,
               private val client: RelayClient,
               private val userMessageParser: UserMessageParser,
               val server: ServerVM,
               val channels: ObservableList<ChannelVM>) : BaseObservable(), EventListener {

    val name: CharSequence
        get() = configuration.name
    val hostname: CharSequence
        get() = configuration.connectionConfiguration.hostname

    val isActive: Boolean
        @Bindable get() = _status != STOPPED
    var status: String = context.getString(STOPPED)
        @Bindable get

    val selectedChild: ObservableField<ClientChildVM>

    private var _status: Int = STOPPED
        set(it) {
            field = it
            status = context.getString(it)

            notifyPropertyChanged(BR.status)
            notifyPropertyChanged(BR.active)
        }

    init {
        selectedChild = ObservableField(server)
    }

    fun select(child: ClientChildVM) {
        selectedChild.set(child)
    }

    fun onSelected(): Boolean {
        val newConnect = !isActive
        if (newConnect) {
            _status = CONNECTING
            client.start()
        }
        selectedChild.set(server)
        return newConnect
    }

    fun sendUserMessage(message: String, context: ClientChildVM) {
        userMessageParser.parse(message, context, server)
    }

    override fun onSocketConnect() {
        _status = SOCKET_CONNECTED
    }

    override fun onWelcome(target: String, text: String) {
        _status = CONNECTED
    }

    companion object {
        const val STOPPED: Int = R.string.status_stopped
        const val CONNECTING: Int = R.string.status_connecting
        const val SOCKET_CONNECTED: Int = R.string.status_socket_connected
        const val CONNECTED: Int = R.string.status_connected
        const val RECONNECTING: Int = R.string.status_reconnecting
        const val DISCONNECTED: Int = R.string.status_disconnected
    }
}