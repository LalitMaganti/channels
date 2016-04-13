package com.tilal6991.channels.redux

import android.content.Context
import android.support.v4.util.ArrayMap
import com.tilal6991.channels.configuration.ChannelsConfiguration
import com.tilal6991.channels.configuration.UserConfiguration
import com.tilal6991.channels.redux.bansa.Store
import com.tilal6991.channels.redux.state.GlobalState
import com.tilal6991.channels.relay.*
import com.tilal6991.listen.EventObjectListener
import com.tilal6991.messageloop.AndroidHandlerMessageLoop
import com.tilal6991.relay.EventListener
import com.tilal6991.relay.MetaListener
import com.tilal6991.relay.RelayClient
import de.duenndns.ssl.MemorizingTrustManager

val clients = ArrayMap<ChannelsConfiguration, RelayClient>()

fun relayMiddleware(context: Context): (Store<GlobalState, Action>) -> ((Action) -> Unit) -> (Action) -> Unit {
    return { store -> { next -> { action -> calculate(context, store, next, action) } } }
}

fun calculate(context: Context,
              store: Store<GlobalState, Action>,
              next: (Action) -> Unit, action: Action) {
    when (action) {
        is Action.SelectClient -> {
            if (clients[action.configuration] != null) {
                return next(action)
            }

            val client = createRelayClient(context, store, action.configuration)
            clients.put(action.configuration, client)
            client.init().connect()
        }
    }
    next(action)
}

fun createRelayClient(context: Context,
                      store: Store<GlobalState, Action>,
                      configuration: ChannelsConfiguration): RelayClient {
    val relayConfig = RelayClient.Configuration.create {
        hostname = configuration.server.hostname
        port = configuration.server.port

        ssl = configuration.server.ssl
        sslTrustManager = MemorizingTrustManager(context)
    }
    val core = RelayClient.create(relayConfig, { AndroidHandlerMessageLoop.create(it) })

    val authHandler: AuthHandler
    val user = configuration.user
    if (user.authType == UserConfiguration.SASL_AUTH_TYPE) {
        authHandler = PlainSASLHandler(core, user.authUsername!!, user.authPassword!!)
    } else if (user.authType == UserConfiguration.NICKSERV_AUTH_TYPE) {
        authHandler = NickServHandler(core, user.authPassword!!)
    } else {
        authHandler = EMPTY_AUTH_HANDLER
    }

    val basicEventListener = BasicEventListener(core)
    val handshakeListener = HandshakeEventListener(core, configuration, authHandler)
    val mainThreadListener = MainThreadEventListener()

    core.addEventListener(basicEventListener)
    core.addEventListener(handshakeListener)
    core.addEventListener(mainThreadListener)

    core.addMetaListener(handshakeListener)
    core.addMetaListener(mainThreadListener)

    val listener = EventClassListener(store, configuration)
    mainThreadListener.addMetaListener(listener)
    mainThreadListener.addEventListener(listener)

    return core
}

@EventObjectListener(className = "EventObjectDispatcher",
        eventsClassName = "Events",
        eventInterfaceClassName = "EventObjectListener")
interface Listener : EventListener, MetaListener

class EventClassListener(private val store: Store<GlobalState, Action>,
                         private val configuration: ChannelsConfiguration) : EventObjectDispatcher() {

    override fun onEvent(event: Events.Event) {
        store.dispatch(Action.RelayEvent(configuration, event))
    }
}