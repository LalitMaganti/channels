package co.fusionx.channels.relay

import android.content.Context
import android.databinding.ObservableArrayList
import android.databinding.ObservableField
import android.databinding.ObservableList
import co.fusionx.relay.ConnectionConfiguration
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
public class RelayHost @Inject constructor(private val context: Context) {
    public val clients: ObservableList<ClientHost> = ObservableArrayList()
    public val selectedClient: ObservableField<ClientHost?> = ObservableField(null)

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