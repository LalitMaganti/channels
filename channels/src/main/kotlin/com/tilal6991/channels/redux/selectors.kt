package com.tilal6991.channels.redux

import com.tilal6991.channels.redux.state.Client
import com.tilal6991.channels.redux.state.ClientChild
import com.tilal6991.channels.redux.state.GlobalState
import com.tilal6991.channels.redux.util.binarySearch
import com.tilal6991.channels.redux.util.getOrNull
import com.tilal6991.reselect.Reselect.createSelector
import com.tilal6991.reselect.computation.Computation1
import com.tilal6991.reselect.selector.Selector
import rx.Observable

val selectedClientSelector = createSelector(
        { state: GlobalState, p: Unit -> state.selectedClients },
        { state, p -> state.clients },
        { selected, clients ->
            val configuration = selected.getOrNull(0) ?: return@createSelector null
            val index = clients.binarySearch(configuration) { it.configuration }
            if (index < 0) null else clients[index]
        })

val selectedChildSelector = createSelector(
        selectedClientSelector,
        Computation1 { it: Client? ->
            if (it == null) return@Computation1 null
            when (it.selectedType) {
                Client.SELECTED_SERVER -> it.server
                Client.SELECTED_CHANNEL -> it.channels[it.selectedIndex]
                else -> null
            }
        })

fun <T, R> Observable<T>.select(selector: Selector<T, Unit, R>): Observable<R> {
    return map { selector(it, Unit) }
}

fun <T, P, R> Observable<T>.select(selector: Selector<T, P, R>,
                                   props: P): Observable<R> {
    return map { selector(it, props) }
}