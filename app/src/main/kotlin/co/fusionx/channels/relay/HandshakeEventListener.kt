package co.fusionx.channels.relay

import co.fusionx.relay.EventListener
import co.fusionx.relay.RelayClient
import co.fusionx.relay.protocol.ClientGenerator

class HandshakeEventListener(
        private val client: RelayClient,
        private val configuration: Configuration,
        private val authHandler: AuthHandler) : EventListener {

    override fun onSocketConnect() {
        client.send(ClientGenerator.cap("LS"))
        if (configuration.password != null) {
            client.send(ClientGenerator.pass(configuration.password))
        }
        client.send(ClientGenerator.nick(configuration.nicks[0]))

        val realName = configuration.realName ?: "ChannelsUser"
        client.send(ClientGenerator.user(configuration.username, realName))
    }

    // TODO(tilal6991) - only do this when connected.
    override fun onCapLs(caps: List<String>) {
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

    override fun onWelcome(target: String, text: String) {

    }

    data class Configuration(
            val username: String,
            val password: String?,
            val nicks: List<String>,
            val realName: String?)

    companion object {
        private val capArray = listOf("multi-prefix")
    }
}