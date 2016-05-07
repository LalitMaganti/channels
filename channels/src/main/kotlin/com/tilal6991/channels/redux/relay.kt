package com.tilal6991.channels.redux

import android.content.Context
import android.support.v4.util.ArrayMap
import com.brianegan.bansa.Action
import com.brianegan.bansa.Middleware
import com.brianegan.bansa.NextDispatcher
import com.brianegan.bansa.Store
import com.tilal6991.channels.configuration.ChannelsConfiguration
import com.tilal6991.channels.configuration.UserConfiguration
import com.tilal6991.channels.redux.state.GlobalState
import com.tilal6991.channels.redux.util.UserMessageParser
import com.tilal6991.channels.relay.*
import com.tilal6991.listen.EventObjectListener
import com.tilal6991.messageloop.AndroidHandlerMessageLoop
import com.tilal6991.relay.EventListener
import com.tilal6991.relay.MetaListener
import com.tilal6991.relay.RelayClient
import de.duenndns.ssl.MemorizingTrustManager

val clients = ArrayMap<ChannelsConfiguration, RelayClient>()

fun relayMiddleware(context: Context): Middleware<GlobalState> = RelayMiddleware(context)

sealed class RelayAction : Action {
    class EventAction(val configuration: ChannelsConfiguration,
                      val event: Events.Event) : RelayAction()

    class MessageAction(val configuration: ChannelsConfiguration,
                        val message: String) : RelayAction()
}

class RelayMiddleware<S>(private val context: Context) : Middleware<S> {
    override fun dispatch(store: Store<S>, action: Action, next: NextDispatcher) {
        when (action) {
            is Actions.SelectClient -> selectClient(action, store)
            is RelayAction.MessageAction -> sendMessage(action)
        }
        next.dispatch(action)
    }

    private fun selectClient(action: Actions.SelectClient, store: Store<S>) {
        if (clients[action.configuration] != null) {
            return
        }

        val client = createRelayClient(context, store, action.configuration)
        clients.put(action.configuration, client)
        client.init().connect()
    }

    private fun sendMessage(action: RelayAction.MessageAction) {
        val client = clients[action.configuration] ?: return
        val line = UserMessageParser.parseServerMessage(action.message) ?: return
        client.send(line)
    }
}

fun <S> createRelayClient(context: Context,
                          store: Store<S>,
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

class EventClassListener<S>(private val store: Store<S>,
                            private val configuration: ChannelsConfiguration) : EventObjectDispatcher() {

    override fun onEvent(event: Events.Event) {
        store.dispatch(RelayAction.EventAction(configuration, event))
    }
}