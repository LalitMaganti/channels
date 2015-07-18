package co.fusionx.channels.relay

import android.content.Context
import android.os.HandlerThread
import co.fusionx.channels.observable.ObservableList
import co.fusionx.relay.ConnectionConfiguration
import co.fusionx.relay.RelayClient
import co.fusionx.relay.message.AndroidMessageLoop
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
public class RelayHost @Inject constructor(private val context: Context) {
    public val clients: ObservableList<ClientHost> = ObservableList(arrayListOf())
    public var selectedClient: ClientHost? = null
        private set

    init {
        val client = ClientHost(ConnectionConfiguration.create {
            hostname = "irc.freenode.net"
            port = 6667
        })
        clients.add(client)
    }

    public fun select(client: ClientHost): Boolean {
        if (selectedClient == client) return true

        selectedClient = client
        client.onSelected()
        return false
    }
}