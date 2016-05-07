package com.tilal6991.channels.redux

import com.brianegan.bansa.Action
import com.brianegan.bansa.Reducer
import com.tilal6991.channels.configuration.ChannelsConfiguration
import com.tilal6991.channels.redux.reducer.clientReducer
import com.tilal6991.channels.redux.state.Client
import com.tilal6991.channels.redux.state.GlobalState
import com.tilal6991.channels.redux.state.mutate
import com.tilal6991.channels.redux.util.*

val initialState = GlobalState(
        TransactingIndexedList.empty(),
        TransactingIndexedList.empty()
)

val reducer = Reducer<GlobalState> { g, a ->
    reduce(g, a)
}

fun reduce(g: GlobalState, a: Action): GlobalState = when (a) {
    is Actions.NewConfigurations -> g.mutate(
            clients = mergeClientLists(g, a.configurations)
    )
    is Actions.SelectClient -> selectClient(g, a.configuration)
    is RelayAction.EventAction -> relayReducer(g, a)
    is Actions.ChangeSelectedChild ->
        g.mutateSelected { it.mutate(selectedType = a.type, selectedIndex = a.position) }
    else -> g.mutate(g.clients.transform { clientReducer(it, a) })
}

fun relayReducer(g: GlobalState, a: RelayAction.EventAction): GlobalState {
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