package com.tilal6991.channels.redux.reducer

import com.tilal6991.channels.redux.Action
import com.tilal6991.channels.redux.Events
import com.tilal6991.channels.redux.state.Client
import com.tilal6991.channels.redux.state.ConnectionInfo
import com.tilal6991.channels.redux.state.mutate
import com.tilal6991.channels.redux.util.nickFromPrefix

fun clientReducer(c: Client, a: Action): Client {
    return c.mutate(
            connectionInfo = connectionInfoReducer(c.connectionInfo, a),
            server = serverReducer(c.server, a),
            status = statusReducer(c.status, a),
            nick = nickReducer(c, a),
            channels = channelsReducer(c, c.channels, a))
}

fun connectionInfoReducer(ci: ConnectionInfo, a: Action): ConnectionInfo = when (a) {
    is Action.RelayEvent -> when (a.event) {
        is Events.OnIsupport -> {
            val index = a.event.keys.indexOf("PREFIX")
            if (index >= 0) {
                val prefix = a.event.values[index]
                ci.copy(prefix.substring(prefix.indexOf(')') + 1))
            } else {
                ci
            }
        }
        else -> ci
    }
    else -> ci
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
    is Action.RelayEvent -> {
        val event = a.event
        when (event) {
            is Events.OnWelcome -> event.target
            is Events.OnNick -> if (event.prefix.nickFromPrefix() == c.nick) event.newNick else c.nick
            else -> c.nick
        }
    }
    else -> c.nick
}