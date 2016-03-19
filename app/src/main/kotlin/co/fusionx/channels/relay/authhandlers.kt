package co.fusionx.channels.relay

import android.util.Base64
import co.fusionx.relay.EventListener
import co.fusionx.relay.RelayClient
import co.fusionx.relay.protocol.ClientGenerator
import java.nio.charset.StandardCharsets

interface AuthHandler : EventListener {

    /**
     * Called by other listeners to decide who should end a CAP session.
     *
     * @param caps optional list of caps which are being advertised by the server.
     * @return whether the handler will send CAP END.
     */
    fun endsCap(caps: List<String>? = null): Boolean = false
}

val EMPTY_AUTH_HANDLER: AuthHandler by lazy { object : AuthHandler {} }

abstract class SASLHandler(protected val client: RelayClient) : AuthHandler {

    private var handling = false

    override fun onCapLs(caps: List<String>) {
        handling = caps.contains("sasl")
        if (handling) {
            client.send(ClientGenerator.cap("REQ", listOf("sasl")))
        }
    }

    override fun onCapAck(caps: List<String>) {
        if (!caps.contains("sasl")) return
        onCapAck()
    }

    override fun onCapNak(caps: List<String>) {
        if (!caps.contains("sasl")) return
        endHandling()
    }

    override fun onOtherCode(code: Int, arguments: List<String>) {
        when (code) {
        // TODO(tilal6991) utilise the codes specified by Relay
            902, 903, 904, 905, 906, 907 -> endHandling()
        }
    }

    override fun endsCap(caps: List<String>?): Boolean {
        return handling || (caps?.contains("sasl") ?: false)
    }

    protected fun endHandling() {
        handling = false
        client.send(ClientGenerator.cap("END"))
    }

    abstract fun onCapAck()
}

class PlainSASLHandler(client: RelayClient,
                       private val configuration: Configuration) : SASLHandler(client) {

    override fun onCapAck() {
        client.send(ClientGenerator.authenticate("PLAIN"))
    }

    override fun onAuthenticate(data: String) {
        if (data == "+") {
            val authentication = configuration.username + "\\0" +
                    configuration.username + "\\0" + configuration.password
            val authBytes = authentication.toByteArray(StandardCharsets.UTF_8)
            val encoded = Base64.encodeToString(authBytes, Base64.DEFAULT)
            client.send(ClientGenerator.authenticate(encoded))
        }
    }

    data class Configuration(val username: String, val password: String)
}