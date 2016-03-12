package co.fusionx.channels.relay.configuration

data class ServerHandshakeConfiguration(
        val username: String,
        val password: String?,
        val nicks: List<String>,
        val realName: String?
)