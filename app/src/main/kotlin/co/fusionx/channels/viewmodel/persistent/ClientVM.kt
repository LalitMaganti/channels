package co.fusionx.channels.viewmodel.persistent

import android.content.Context
import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.ObservableField
import android.databinding.ObservableList
import co.fusionx.channels.collections.ObservableSortedArrayMap
import co.fusionx.channels.relay.BasicEventListener
import co.fusionx.channels.relay.Configuration
import co.fusionx.channels.relay.MainThreadEventListener
import co.fusionx.channels.util.charSequenceComparator
import co.fusionx.channels.viewmodel.helper.ChannelComparator
import co.fusionx.channels.viewmodel.listener.ChannelDelegatingListener
import co.fusionx.channels.viewmodel.listener.ClientStateListener
import co.fusionx.channels.viewmodel.helper.UserMessageParser
import co.fusionx.channels.viewmodel.listener.ServerDelegatingListener
import co.fusionx.relay.RelayClient
import co.fusionx.relay.message.AndroidMessageLoop
import java.util.*

class ClientVM(private val context: Context,
               private val configuration: Configuration) : BaseObservable() {
    val name: CharSequence
        get() = configuration.name
    val hostname: CharSequence
        get() = configuration.connectionConfiguration.hostname

    val isActive: Boolean
        @Bindable get() = clientStateListener.isActive
    val status: String
        @Bindable get() = clientStateListener.status
    val user: UserVM
        get() = clientStateListener.user

    val selectedChild: ObservableField<ClientChildVM>
    val server: ServerVM
    val channels: ObservableList<ChannelVM>

    private val client: RelayClient
    private val channelMap: ObservableSortedArrayMap<CharSequence, ChannelVM>
    private val userMap: MutableMap<String, UserVM>
    private val clientStateListener: ClientStateListener
    private val userMessageParser: UserMessageParser

    init {
        server = ServerVM("Server")
        channelMap = ObservableSortedArrayMap(charSequenceComparator, ChannelComparator.instance)
        channels = channelMap.valuesAsObservableList()
        userMap = HashMap()
        selectedChild = ObservableField(server)
        clientStateListener = ClientStateListener(context, configuration, this)
        userMessageParser = UserMessageParser(Listener())

        client = RelayClient.create(configuration.connectionConfiguration, AndroidMessageLoop.create())

        val basicEventListener = BasicEventListener(client)
        val mainThreadListener = MainThreadEventListener()
        client.addEventListener(basicEventListener)
        client.addEventListener(mainThreadListener)

        val serverListener = ServerDelegatingListener(server)
        val channelMapListener = ChannelDelegatingListener(user, userMap, channelMap)

        mainThreadListener.addEventListener(clientStateListener)
        mainThreadListener.addEventListener(serverListener)
        mainThreadListener.addEventListener(channelMapListener)
    }

    fun sendUserMessage(userMessage: String, context: ClientChildVM) {
        val message = userMessageParser.parse(userMessage, context, server) ?: return
        client.send(message)
    }

    fun select(child: ClientChildVM) {
        selectedChild.set(child)
    }

    fun onSelected(): Boolean {
        val active = clientStateListener.onSelected()
        if (!active) {
            client.start()
        }
        selectedChild.set(server)
        return active
    }

    private inner class Listener : UserMessageParser.ParserListener {
        override fun onChannelMessage(channelVM: ChannelVM, message: String) {
            channelVM.onPrivmsg(user, message)
        }
    }
}