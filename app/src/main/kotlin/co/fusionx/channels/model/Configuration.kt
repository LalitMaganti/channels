package co.fusionx.channels.model

import co.fusionx.relay.ConnectionConfiguration

class Configuration(val name: String,
                    val connectionConfiguration: ConnectionConfiguration) {
    override fun equals(other: Any?): Boolean {
        if (other !is Configuration) return false
        return other.name == name
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}