package co.fusionx.channels.relay

import android.os.Handler
import android.support.annotation.IntDef
import co.fusionx.relay.ConnectionConfiguration
import co.fusionx.relay.EventListener
import co.fusionx.relay.RelayClient
import co.fusionx.relay.message.AndroidMessageLoop
import co.fusionx.relay.protocol.ClientGenerator
import java.util.*

public class ClientHost(private val configuration: ConnectionConfiguration) {
    public val children: List<ClientChild>
        get() = _children
    public var selectedChild: ClientChild
        private set
    public var status: Long = STOPPED
        @Status get
        set(@Status i) {
            field = i
        }

    private val _children: MutableList<ClientChild> = ArrayList()
    private var server: ServerHost
    private val client: RelayClient

    init {
        client = RelayClient.create(configuration, AndroidMessageLoop.create())
        client.addEventListener(DispatchingEventListener())
        client.addEventListener(BasicEventListener(client))

        server = ServerHost("Freenode")
        _children.add(server)

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

        public override fun onGenericCode(code: Int, text: String): Unit {
            handler.post { server.onGenericCode(code, text) }
        }

        public override fun onWelcome(text: String): Unit {
            handler.post {
                status = CONNECTED
                server.onWelcome(text)
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