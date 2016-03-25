package co.fusionx.channels.relay

import co.fusionx.channels.configuration.UserConfiguration
import co.fusionx.relay.EventListener
import co.fusionx.relay.RelayClient
import co.fusionx.relay.protocol.ClientGenerator

class HandshakeEventListener(
        private val client: RelayClient,
        private val configuration: UserConfiguration,
        private val authHandler: AuthHandler) : EventListener {

    override fun onSocketConnect() {
        client.send(ClientGenerator.cap("LS"))
        val password = configuration.password
        if (password != null) {
            client.send(ClientGenerator.pass(password))
        }
        client.send(ClientGenerator.nick(configuration.nicks.getOrElse(0) { "ChannelsUser" }))

        val realName = configuration.realName ?: "ChannelsUser"
        client.send(ClientGenerator.user(configuration.username ?: "ChannelsUser", realName))
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
        private val capArray = listOf("multi-prefix")
    }
}