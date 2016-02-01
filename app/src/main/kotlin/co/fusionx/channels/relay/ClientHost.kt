package co.fusionx.channels.relay

import android.os.Handler
import android.support.annotation.IntDef
import android.support.v4.util.SimpleArrayMap
import co.fusionx.channels.observable.ObservableList
import co.fusionx.relay.ConnectionConfiguration
import co.fusionx.relay.EventListener
import co.fusionx.relay.RelayClient
import co.fusionx.relay.message.AndroidMessageLoop
import co.fusionx.relay.protocol.ClientGenerator
import co.fusionx.relay.util.PrefixExtractor
import java.util.*

public class ClientHost(private val configuration: ConnectionConfiguration) {
    public val children: ObservableList<ClientChild> = ObservableList(ArrayList())
    public var selectedChild: ClientChild
        private set
    public val status: String
        get() = ClientHost.statusAsString(statusValue)

    // TODO(tilal6991) Fix this to do the correct thing.
    public val name: CharSequence
        get() = "Freenode"

    // TODO(tilal6991) Fix this to do the correct thing.
    private var nick: String = "tilal6993"
    private var statusValue: Long = STOPPED
        @Status get
        set(@Status i) {
            field = i
        }

    private var server: ServerHost
    private val client: RelayClient
    private val channels: SimpleArrayMap<String, ChannelHost> = SimpleArrayMap()

    init {
        client = RelayClient.create(configuration, AndroidMessageLoop.create())
        client.addEventListener(DispatchingEventListener())
        client.addEventListener(BasicEventListener(client))

        server = ServerHost("Freenode")
        children.add(server)

        selectedChild = server
    }

    public fun select(child: ClientChild): Boolean {
        if (selectedChild == child) return true
        selectedChild = child
        return false
    }

    private inner class DispatchingEventListener : EventListener {
        private val handler: Handler = Handler()

        override fun onSocketConnect() {
            handler.post {
                statusValue = SOCKET_CONNECTED
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

        public override fun onGenericCode(code: Int, text: String) {
            handler.post { server.onGenericCode(code, text) }
        }

        public override fun onWelcome(target: String, text: String) {
            handler.post {
                statusValue = CONNECTED
                server.onWelcome(target, text)

                nick = target
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
        if (statusValue == STOPPED) {
            statusValue = CONNECTING
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

        @IntDef(STOPPED, CONNECTING, SOCKET_CONNECTED, CONNECTED, RECONNECTING, DISCONNECTED)
        @Retention(AnnotationRetention.SOURCE)
        public annotation class Status

        fun statusAsString(statusValue: Long): String = when (statusValue) {
            STOPPED -> "Stopped"
            CONNECTING -> "Connecting"
            SOCKET_CONNECTED -> "Socket connected"
            CONNECTED -> "Connected"
            RECONNECTING -> "Reconnecting"
            DISCONNECTED -> "Disconnected"
            else -> "Invalid status - this is a bug"
        }
    }
}