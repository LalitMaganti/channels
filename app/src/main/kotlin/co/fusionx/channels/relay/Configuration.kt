package co.fusionx.channels.relay

import co.fusionx.relay.RelayClient

class Configuration(val name: String,
                    val connection: RelayClient.Configuration,
                    val handshake: HandshakeEventListener.Configuration) {

    override fun equals(other: Any?): Boolean {
        if (other !is Configuration) return false
        return other.name == name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}