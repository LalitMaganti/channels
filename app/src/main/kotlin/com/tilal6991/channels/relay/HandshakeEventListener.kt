package com.tilal6991.channels.relay

import com.tilal6991.channels.configuration.ChannelsConfiguration
import com.tilal6991.relay.EventListener
import com.tilal6991.relay.RelayClient
import com.tilal6991.relay.protocol.Capabilities
import com.tilal6991.relay.protocol.ClientGenerator

class HandshakeEventListener(
        private val client: RelayClient,
        private val configuration: ChannelsConfiguration,
        private val authHandler: AuthHandler) : EventListener {

    override fun onSocketConnect() {
        client.send(ClientGenerator.cap("LS"))
        val password = configuration.server.password
        if (password != null) {
            client.send(ClientGenerator.pass(password))
        }
        client.send(ClientGenerator.nick(configuration.user.nicks.getOrElse(0) { "ChannelsUser" }))

        val realName = configuration.user.realName
        client.send(ClientGenerator.user(configuration.server.username, realName))
    }

    // TODO(tilal6991) - only do this when connected.
    override fun onCapLs(caps: List<String>, values: List<String?>, finalLine: Boolean) {
        val req = caps.intersect(capArray)
        if (req.isEmpty()) {
            if (!authHandler.endsCap(caps)) {
                client.send(ClientGenerator.cap("END"))
            }
            return
        }
        client.send(ClientGenerator.cap("REQ", req))
    }

    // TODO(tilal6991) - only do this when connected.
    override fun onCapAck(caps: List<String>) {
        if (!capArray.containsAll(caps)) return
        if (authHandler.endsCap()) return
        client.send(ClientGenerator.cap("END"))
    }

    // TODO(tilal6991) - only do this when connected.
    override fun onCapNak(caps: List<String>) {
        if (!capArray.containsAll(caps)) return
        if (authHandler.endsCap()) return
        client.send(ClientGenerator.cap("END"))
    }

    companion object {
        private val capArray = listOf(Capabilities.MULTI_PREFIX)
    }
}