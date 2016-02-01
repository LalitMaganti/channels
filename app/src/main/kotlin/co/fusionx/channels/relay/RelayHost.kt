package co.fusionx.channels.relay

import android.content.Context
import android.os.HandlerThread
import co.fusionx.channels.observable.ObservableList
import co.fusionx.channels.observable.ObservableReference
import co.fusionx.relay.ConnectionConfiguration
import co.fusionx.relay.RelayClient
import co.fusionx.relay.message.AndroidMessageLoop
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
public class RelayHost @Inject constructor(private val context: Context) {
    public val clients: ObservableList<ClientHost> = ObservableList(arrayListOf())
    public var selectedClient: ObservableReference<ClientHost> = ObservableReference(null)
        private set

    init {
        val client = ClientHost(ConnectionConfiguration.create {
            hostname = "irc.freenode.net"
            port = 6667
        })
        clients.add(client)
    }

    public fun select(client: ClientHost): Boolean {
        if (selectedClient.get() == client) return true

        selectedClient.set(client)
        client.onSelected()
        return false
    }
}