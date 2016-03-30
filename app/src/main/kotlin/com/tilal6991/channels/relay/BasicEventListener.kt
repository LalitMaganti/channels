package com.tilal6991.channels.relay

import com.tilal6991.relay.EventListener
import com.tilal6991.relay.RelayClient
import com.tilal6991.relay.protocol.ClientGenerator

class BasicEventListener(
        private val client: RelayClient) : EventListener {

    override fun onPing(hostName: String?) {
        client.send(ClientGenerator.pong(hostName))
    }
}