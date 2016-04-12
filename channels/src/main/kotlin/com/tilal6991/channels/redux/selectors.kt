package com.tilal6991.channels.redux

import android.os.Handler
import android.os.Looper
import com.tilal6991.channels.redux.bansa.Subscription
import com.tilal6991.channels.redux.state.Client
import com.tilal6991.channels.redux.state.ClientChild
import com.tilal6991.channels.redux.state.GlobalState
import com.tilal6991.channels.redux.util.getOrNull
import trikita.anvil.Anvil
import java.util.*

var currentState = initialState
private val handler = Handler(Looper.getMainLooper())
private val mainThreadSubscribers = ArrayList<(GlobalState) -> Unit>()
private var s: Subscription? = null

fun subscribe(fn: (GlobalState) -> Unit): Runnable {
    if (s == null) {
        s = store.subscribe { state ->
            handler.post {
                currentState = state
                mainThreadSubscribers.forEach { it(currentState) }
                Anvil.render()
            }
        }
        currentState = store.state
        Anvil.render()
        fn(currentState)
    }

    mainThreadSubscribers.add(fn)
    return Runnable {
        mainThreadSubscribers.remove(fn)
    }
}

val selectedClient: Client?
    get() {
        val configuration = currentState.selectedClients.getOrNull(0) ?: return null
        val index = currentState.clients.binarySearch(configuration) { it.configuration }
        return if (index < 0) null else currentState.clients[index]
    }

val selectedChild: ClientChild?
    get() {
        val currentClient = selectedClient ?: return null
        return when (currentClient.selectedType) {
            Client.SELECTED_SERVER -> currentClient.server
            Client.SELECTED_CHANNEL -> currentClient.channels[currentClient.selectedIndex]
            else -> null
        }
    }

fun message(child: ClientChild?): CharSequence? {
    val buffer = child?.buffer ?: return null
    return buffer.getOrNull(buffer.size() - 1) ?: "No items to show"
}
