package co.fusionx.channels.relay

import android.os.Handler
import android.support.annotation.IntDef
import android.support.v4.util.ArrayMap
import android.support.v4.util.SimpleArrayMap
import co.fusionx.channels.observable.ObservableList
import co.fusionx.relay.ConnectionConfiguration
import co.fusionx.relay.EventListener
import co.fusionx.relay.RelayClient
import co.fusionx.relay.message.AndroidMessageLoop
import co.fusionx.relay.protocol.ClientGenerator
import java.util.*

public class ClientHost(private val configuration: ConnectionConfiguration) {
    public val children: ObservableList<ClientChild> = ObservableList(ArrayList())
    public var selectedChild: ClientChild
        private set
    public var status: Long = STOPPED
        @Status get
        set(@Status i) {
            field = i
        }

    private var nick: String = "tilal6993"

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
                status = SOCKET_CONNECTED
                server.onSocketConnect()
            }
        }

        public override fun onJoin(prefix: String, channel: String) {
            handler.post {
                if (prefix == nick) {
                    val c = ChannelHost(channel)
                    children.add(c)
                    channels.put(channel, c)
                } else {
                    val c = channels.get(channel)
                    c.addUser(prefix)
                }
            }
        }

        public override fun onGenericCode(code: Int, text: String) {
            handler.post { server.onGenericCode(code, text) }
        }

        public override fun onWelcome(target: String, text: String) {
            handler.post {
                status = CONNECTED
                server.onWelcome(text)

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

        @IntDef(STOPPED, CONNECTING, SOCKET_CONNECTED, CONNECTED, RECONNECTING, DISCONNECTED)
        @Retention(AnnotationRetention.SOURCE)
        public annotation class Status
    }
}