package co.fusionx.channels.relay.configuration

import co.fusionx.relay.ConnectionConfiguration

class Configuration(val name: String,
                    val connectionConfiguration: ConnectionConfiguration,
                    val serverHandshakeConfiguration: ServerHandshakeConfiguration) {

    override fun equals(other: Any?): Boolean {
        if (other !is Configuration) return false
        return other.name == name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}