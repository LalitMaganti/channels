package com.tilal6991.channels.redux.reducer

import com.tilal6991.channels.redux.Action
import com.tilal6991.channels.redux.Events
import com.tilal6991.channels.redux.state.Server
import com.tilal6991.channels.redux.util.append
import com.tilal6991.channels.redux.util.displayCode

fun serverReducer(s: Server, a: Action): Server = when (a) {
    is Action.RelayEvent -> server(s, a.event)
    else -> s
}

fun server(s: Server, event: Events.Event): Server = when (event) {
    is Events.OnWelcome -> s.append(event.text)
    is Events.OnNotice -> s.append(event.message)
    is Events.OnOtherCode ->
        if (event.code.displayCode()) s.append(event.arguments.lastOrNull()) else s
    else -> s
}