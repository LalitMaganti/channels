package co.fusionx.channels.relay

import co.fusionx.channels.relay.configuration.Configuration
import co.fusionx.channels.relay.configuration.ServerHandshakeConfiguration
import co.fusionx.relay.EventListener
import co.fusionx.relay.RelayClient
import co.fusionx.relay.protocol.ClientGenerator

class BasicEventListener(
        private val client: RelayClient,
        private val configuration: Configuration) : EventListener {

    private val handshakeConfig: ServerHandshakeConfiguration
        get() = configuration.serverHandshakeConfiguration

    override fun onPing(server: String) {
        client.send(ClientGenerator.pong(server))
    }

    // TODO(tilal6991) fix this to work correctly.
    override fun onSocketConnect() {
        client.send(ClientGenerator.nick(handshakeConfig.nicks[0]))
        client.send(ClientGenerator.user(handshakeConfig.username,
                handshakeConfig.realName ?: "none"))
    }

    override fun onWelcome(target: String, text: String) {
        client.send(ClientGenerator.join("#channels"))
    }
}