package co.fusionx.channels.relay

import android.util.ArrayMap
import co.fusionx.relay.EventListener

class ConnectionInformation : EventListener {

    private val isupportValueMap: MutableMap<String, String> = ArrayMap()

    val isupportValues = ISUPPORTValues(isupportValueMap)

    override fun onIsupport(supportTokens: List<String>, message: String) {
        for (i in supportTokens) {
            val equalIndex = i.indexOf('=')
            if (equalIndex != -1) {
                isupportValueMap.put(i.substring(0, equalIndex), i.substring(equalIndex + 1))
            }
        }
    }

    class ISUPPORTValues(private val v: Map<String, String>) : Map<String, String> by v {
        val PREFIX: String
            get() = this["PREFIX"] ?: "(ov)@+"

        val channelModes: CharArray
            get() {
                val index = PREFIX.indexOf(')')
                if (index == -1) {
                    return charArrayOf()
                }
                return PREFIX.substring(index + 1).toCharArray()
            }
    }
}