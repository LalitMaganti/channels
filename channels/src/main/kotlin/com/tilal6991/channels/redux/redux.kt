package com.tilal6991.channels.redux

import com.tilal6991.channels.configuration.ChannelsConfiguration
import com.tilal6991.channels.redux.reducer.channelsReducer
import com.tilal6991.channels.redux.reducer.serverReducer
import com.tilal6991.channels.redux.state.Client
import com.tilal6991.channels.redux.state.GlobalState
import com.tilal6991.channels.redux.state.mutate
import com.tilal6991.channels.redux.util.*

val initialState = GlobalState(
        TransactingIndexedList(),
        TransactingIndexedList()
)

fun clientReducer(c: Client, a: Action): Client {
    return c.mutate(
            server = serverReducer(c.server, a),
            status = statusReducer(c.status, a),
            nick = nickReducer(c, a),
            channels = channelsReducer(c, c.channels, a))
}

fun statusReducer(status: Int, a: Action): Int {
    return when (a) {
        is Action.RelayEvent -> when (a.event) {
            is Events.OnWelcome -> Client.STATUS_CONNECTED
            is Events.OnSocketConnect -> Client.STATUS_REGISTERING
            is Events.OnDisconnect, is Events.OnConnectFailed -> Client.STATUS_DISCONNECTED
            else -> status
        }
        else -> status
    }
}

fun nickReducer(c: Client, a: Action): String = when (a) {
    is Action.RelayEvent -> nickRelayEvent(c, a.event)
    else -> c.nick
}

private fun nickRelayEvent(c: Client, event: Events.Event): String = when (event) {
    is Events.OnWelcome -> event.target
    is Events.OnNick -> if (event.prefix.nickFromPrefix() == c.nick) event.newNick else c.nick
    else -> c.nick
}

val reducer: (GlobalState, Action) -> GlobalState = { g, a ->
    reduce(g, a)
}

fun reduce(g: GlobalState, a: Action): GlobalState = when (a) {
    is Action.NewConfigurations -> g.mutate(
            clients = mergeClientLists(g, a.configurations)
    )
    is Action.SelectClient -> selectClient(g, a.configuration)
    is Action.RelayEvent -> relayReducer(g, a)
    is Action.ChangeSelectedChild ->
        g.mutateSelected { it.mutate(selectedType = a.type, selectedIndex = a.position) }
    else -> g.mutate(g.clients.transform { clientReducer(it, a) })
}

fun relayReducer(g: GlobalState, a: Action.RelayEvent): GlobalState {
    return g.mutate(clients = g.clients.clientMutate(a.configuration) { clientReducer(it, a) })
}

fun selectClient(g: GlobalState, configuration: ChannelsConfiguration): GlobalState {
    val list = g.clients.clientMutate(configuration) {
        it.mutate(selectedType = Client.SELECTED_SERVER, selectedIndex = 0)
    }
    return g.mutate(list, g.selectedClients.pullToFront(configuration))
}

fun mergeClientLists(state: GlobalState,
                     configurations: List<ChannelsConfiguration>): TransactingIndexedList<Client> {
    val newConfigs = configurations.toMutableList()
    newConfigs.sort()

    if (state.clients.isEmpty) {
        val builder = TransactingIndexedList.builder<Client>()
        for (it in newConfigs) {
            builder.add(Client(it))
        }
        return builder.build()
    }

    val oldClients = state.clients

    val builder = TransactingIndexedList.builder<Client>()
    var oldIndex = 0
    var newIndex = 0
    while (true) {
        if (oldIndex >= oldClients.size()) {
            for (i in newIndex..newConfigs.size - 1) {
                builder.add(Client(newConfigs[i]))
            }
            break
        } else if (newIndex >= newConfigs.size) {
            for (i in oldIndex..oldClients.size() - 1) {
                builder.add(oldClients[i])
            }
            break
        }

        val old = oldClients.get(oldIndex)
        val new = newConfigs[newIndex]
        val comparison = old.configuration.compareTo(new)
        if (comparison == 0) {
            oldIndex++
            newIndex++
            builder.add(old)
        } else if (comparison < 0) {
            oldIndex++
            builder.add(old)
        } else {
            newIndex++
            builder.add(Client(new))
        }
    }

    return builder.build()
}