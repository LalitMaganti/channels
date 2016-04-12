package com.tilal6991.channels.redux.state

import com.github.andrewoma.dexx.collection.IndexedList
import com.tilal6991.channels.configuration.ChannelsConfiguration
import com.tilal6991.channels.redux.util.SortedIndexedList

data class GlobalState(val clients: SortedIndexedList<Client>,
                       val selectedClients: IndexedList<ChannelsConfiguration>)

fun GlobalState.mutate(clients: SortedIndexedList<Client> = this.clients,
                       selectedClients: IndexedList<ChannelsConfiguration> = this.selectedClients): GlobalState {
    if (clients !== this.clients && selectedClients !== this.selectedClients) {
        return GlobalState(clients = clients, selectedClients = selectedClients)
    } else if (clients !== this.clients) {
        return GlobalState(clients = clients, selectedClients = this.selectedClients)
    } else if (selectedClients !== this.selectedClients) {
        return GlobalState(clients = this.clients, selectedClients = selectedClients)
    }
    return this
}