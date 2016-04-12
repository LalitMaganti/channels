package com.tilal6991.channels.redux

import com.github.andrewoma.dexx.collection.IndexedLists
import com.github.andrewoma.dexx.collection.Vector
import com.tilal6991.channels.configuration.ChannelsConfiguration
import com.tilal6991.channels.redux.bansa.applyMiddleware
import com.tilal6991.channels.redux.bansa.createStore
import com.tilal6991.channels.redux.state.Client
import com.tilal6991.channels.redux.state.GlobalState
import com.tilal6991.channels.redux.state.Server
import com.tilal6991.channels.redux.state.mutate
import com.tilal6991.channels.redux.util.SortedIndexedList
import com.tilal6991.channels.redux.util.mutate
import com.tilal6991.channels.redux.util.pullToFront
import com.tilal6991.channels.redux.util.transform

val initialState = GlobalState(
        SortedIndexedList(Vector.empty()),
        Vector.empty()
)

fun serverReducer(s: Server, a: Action): Server {
    return when (a) {
        is Action.Welcome -> welcome(s, a)
        else -> s
    }
}

fun clientReducer(c: Client, a: Action): Client {
    return c.mutate(server = serverReducer(c.server, a), channels = c.channels)
}

val reducer: (GlobalState, Action) -> GlobalState = { g, a ->
    reduce(g, a)
}

fun reduce(g: GlobalState, a: Action): GlobalState {
    when (a) {
        is Action.NewConfigurations -> return g.mutate(
                clients = mergeClientLists(g, a.configurations)
        )
        is Action.SelectClient -> return selectClient(g, a.configuration)
    }
    return g.mutate(g.clients.transform { clientReducer(it, a) })
}

fun selectClient(g: GlobalState, configuration: ChannelsConfiguration): GlobalState {
    val index = g.clients.binarySearch(configuration) { it.configuration }
    val client = g.clients[index].mutate(
            selectedType = Client.SELECTED_SERVER,
            selectedIndex = 0
    )

    return g.mutate(g.clients.mutate(index, client), g.selectedClients.pullToFront(configuration))
}

fun mergeClientLists(state: GlobalState,
                     configurations: List<ChannelsConfiguration>): SortedIndexedList<Client> {
    val newConfigs = configurations.toMutableList()
    newConfigs.sort()

    if (state.clients.isEmpty) {
        val builder = IndexedLists.builder<Client>()
        for (it in newConfigs) {
            builder.add(Client(it))
        }
        return SortedIndexedList(builder.build(), null)
    }

    val oldClients = state.clients

    val builder = IndexedLists.builder<Client>()
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

    return SortedIndexedList(builder.build(), null)
}

val store = applyMiddleware(relayMiddleware)(createStore(initialState, reducer))

fun welcome(s: Server, a: Action.Welcome): Server {
    return s.copy(buffer = s.buffer.append(a.message))
}