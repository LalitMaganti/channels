package co.fusionx.channels.model.helper

import co.fusionx.relay.EventListener
import co.fusionx.relay.RelayClient
import co.fusionx.relay.protocol.ClientGenerator

class BasicEventListener(private val client: RelayClient) : EventListener {
    override fun onPing(server: String) {
        client.send(ClientGenerator.pong(server))
    }

    // TODO(tilal6991) fix this to work correctly.
    override fun onSocketConnect() {
        client.send(ClientGenerator.nick("tilal6993"))
        client.send(ClientGenerator.user("tilal6993", "Lalit"))
    }

    override fun onWelcome(target: String, text: String) {
        client.send(ClientGenerator.join("#channels"))
    }
}