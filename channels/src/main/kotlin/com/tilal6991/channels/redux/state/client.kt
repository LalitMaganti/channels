package com.tilal6991.channels.redux.state

import com.github.andrewoma.dexx.collection.IndexedList
import com.github.andrewoma.dexx.collection.IndexedLists
import com.tilal6991.channels.configuration.ChannelsConfiguration
import com.tilal6991.channels.redux.util.TransactingIndexedList

data class Client(val configuration: ChannelsConfiguration,
                  val connectionInfo: ConnectionInfo = ConnectionInfo(),
                  val status: Int = Client.STATUS_STOPPED,
                  val nick: String = configuration.user.nicks.getOrNull(0) ?: "",
                  val server: Server = Server(configuration.name),
                  val channels: TransactingIndexedList<Channel> = TransactingIndexedList.empty(),
                  val selectedType: Int = SELECTED_SERVER,
                  val selectedIndex: Int = 0) : Comparable<Client> {

    override fun compareTo(other: Client): Int {
        return configuration.compareTo(other.configuration)
    }

    companion object {
        const val STATUS_STOPPED = 0
        const val STATUS_CONNECTING = 1
        const val STATUS_REGISTERING = 2
        const val STATUS_CONNECTED = 3
        const val STATUS_DISCONNECTING = 4
        const val STATUS_DISCONNECTED = 5
        const val STATUS_RECONNECTING = 6

        const val SELECTED_SERVER = 0
        const val SELECTED_CHANNEL = 1
    }
}

data class ConnectionInfo(val prefixes: IndexedList<Char> = IndexedLists.of('@', '+'))

fun Client.mutate(status: Int = this.status,
                  connectionInfo: ConnectionInfo = this.connectionInfo,
                  server: Server = this.server,
                  nick: String = this.nick,
                  channels: TransactingIndexedList<Channel> = this.channels,
                  selectedType: Int = this.selectedType,
                  selectedIndex: Int = this.selectedIndex): Client {
    if (nick === this.nick && server === this.server && channels === this.channels &&
            status === this.status && selectedType === this.selectedType &&
            selectedIndex === this.selectedIndex) {
        return this
    }
    return copy(configuration, connectionInfo, status, nick, server, channels, selectedType,
            selectedIndex)
}
