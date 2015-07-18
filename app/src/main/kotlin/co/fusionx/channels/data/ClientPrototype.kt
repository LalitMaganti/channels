package co.fusionx.channels.data

import co.fusionx.relay.ConnectionConfiguration

public data class ClientPrototype(
        public val title: String,
        public val connection: ConnectionConfiguration)