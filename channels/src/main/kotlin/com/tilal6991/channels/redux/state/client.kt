package com.tilal6991.channels.redux.state

import com.github.andrewoma.dexx.collection.Vector
import com.tilal6991.channels.configuration.ChannelsConfiguration
import com.tilal6991.channels.redux.util.SortedIndexedList

data class Client(val configuration: ChannelsConfiguration,
                  val status: Int = Client.STATUS_STOPPED,
                  val server: Server = Server(configuration.name),
                  val channels: SortedIndexedList<Channel> = SortedIndexedList(Vector.empty(), null),
                  val selectedType: Int = SELECTED_SERVER,
                  val selectedIndex: Int = 0) {

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

fun Client.mutate(status: Int = this.status,
                  server: Server = this.server,
                  channels: SortedIndexedList<Channel> = this.channels,
                  selectedType: Int = Client.SELECTED_SERVER,
                  selectedIndex: Int = 0): Client {
    if (server === this.server && channels === this.channels && status === this.status &&
            selectedType === this.selectedType && selectedIndex === this.selectedIndex) {
        return this
    }
    return copy(configuration, status, server, channels, selectedType, selectedIndex)
}
