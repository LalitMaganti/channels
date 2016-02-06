package co.fusionx.channels.relay

class ISUPPORTValues(private val v: Map<String, String>) : Map<String, String> by v {
    val PREFIX: String
        get() = this["PREFIX"] ?: "(ov)@+"
}