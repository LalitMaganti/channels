package com.tilal6991.channels.redux.state

import com.tilal6991.channels.configuration.ChannelsConfiguration
import com.tilal6991.channels.redux.util.TransactingIndexedList

data class GlobalState(val clients: TransactingIndexedList<Client>,
                       val selectedClients: TransactingIndexedList<ChannelsConfiguration>)

fun GlobalState.mutate(clients: TransactingIndexedList<Client> = this.clients,
                       selectedClients: TransactingIndexedList<ChannelsConfiguration> = this.selectedClients): GlobalState {
    if (clients !== this.clients && selectedClients !== this.selectedClients) {
        return GlobalState(clients = clients, selectedClients = selectedClients)
    } else if (clients !== this.clients) {
        return GlobalState(clients = clients, selectedClients = this.selectedClients)
    } else if (selectedClients !== this.selectedClients) {
        return GlobalState(clients = this.clients, selectedClients = selectedClients)
    }
    return this
}