package co.fusionx.channels.relay

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import android.os.Handler
import android.support.v4.util.SimpleArrayMap
import co.fusionx.channels.BR
import co.fusionx.relay.EventListener
import co.fusionx.relay.RelayClient
import co.fusionx.relay.message.AndroidMessageLoop
import co.fusionx.relay.protocol.ClientGenerator
import co.fusionx.relay.util.PrefixExtractor
import co.fusionx.relay.util.isChannel

public class ClientHost(public val configuration: ClientConfiguration) : BaseObservable() {
    public val name: CharSequence get() = configuration.name
    public val children: ObservableList<ClientChild> = ObservableArrayList()

    private val client: RelayClient = RelayClient.create(configuration.connectionConfiguration,
            AndroidMessageLoop.create())
    private val channels: SimpleArrayMap<String, ChannelHost> = SimpleArrayMap()
    private var server: ServerHost = ServerHost(name)

    // Bindable properties.
    public var selectedChild: ClientChild = server
        @Bindable get
        private set(it) {
            field = it
            notifyPropertyChanged(BR.selectedChild)
        }
    public var status: Long = STOPPED
        @Bindable get
        private set(it) {
            field = it
            notifyPropertyChanged(BR.status)
        }
    // TODO(tilal6991) Fix this to do the correct thing.
    public var nick: String = "tilal6993"
        @Bindable get
        private set(it) {
            field = it
            notifyPropertyChanged(BR.nick)
        }

    init {
        client.addEventListener(DispatchingEventListener())
        client.addEventListener(BasicEventListener(client))

        children.add(server)
    }

    public fun select(child: ClientChild) {
        if (selectedChild == child) return
        selectedChild = child
    }

    private inner class DispatchingEventListener : EventListener {
        private val handler: Handler = Handler()

        override fun onSocketConnect() {
            handler.post {
                status = SOCKET_CONNECTED
                server.onSocketConnect()
            }
        }

        override fun onNames(channelName: String, nickList: List<String>) {
            handler.post { channels.get(channelName).onNames(nickList) }
        }

        public override fun onJoin(prefix: String, channel: String) {
            handler.post {
                val c: ChannelHost
                if (PrefixExtractor.nick(prefix) == nick) {
                    c = ChannelHost(channel)
                    children.add(c)
                    channels.put(channel, c)
                } else {
                    c = channels.get(channel)
                }
                c.onJoin(prefix)
            }
        }

        public override fun onOtherCode(code: Int, arguments: List<String>) {
            handler.post { server.onOtherCode(code, arguments) }
        }

        public override fun onWelcome(target: String, text: String) {
            handler.post {
                status = CONNECTED
                server.onWelcome(target, text)

                nick = target
            }
        }

        override fun onPrivmsg(prefix: String, target: String, message: String) {
            handler.post {
                if (target.isChannel()) {
                    channels[target].onPrivmsg(prefix, message)
                }
                // TODO(tilal6991) handle the private message case
            }
        }
    }

    private class BasicEventListener(private val client: RelayClient) : EventListener {
        override fun onPing(server: String) {
            client.send(ClientGenerator.pong(server))
        }

        override fun onSocketConnect() {
            client.send(ClientGenerator.nick("tilal6993"))
            client.send(ClientGenerator.user("tilal6993", "Lalit"))
            client.send(ClientGenerator.join("#channels"))
        }
    }

    fun onSelected() {
        selectedChild = server

        if (status == STOPPED) {
            status = CONNECTING
            client.start()
        }
    }

    companion object {
        public const val STOPPED: Long = 0
        public const val CONNECTING: Long = 1
        public const val SOCKET_CONNECTED: Long = 2
        public const val CONNECTED: Long = 3
        public const val RECONNECTING: Long = 4
        public const val DISCONNECTED: Long = 5
    }
}