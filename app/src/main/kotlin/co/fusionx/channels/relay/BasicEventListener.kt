package co.fusionx.channels.relay

import co.fusionx.relay.EventListener
import co.fusionx.relay.RelayClient
import co.fusionx.relay.protocol.ClientGenerator

class BasicEventListener(
        private val client: RelayClient) : EventListener {

    override fun onPing(server: String) {
        client.send(ClientGenerator.pong(server))
    }
}