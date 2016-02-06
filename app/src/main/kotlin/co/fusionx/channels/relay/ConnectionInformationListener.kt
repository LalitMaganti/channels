package co.fusionx.channels.relay

import android.util.ArrayMap
import co.fusionx.relay.EventListener

class ConnectionInformationListener : EventListener {

    private val isupportValueMap: MutableMap<String, String> = ArrayMap()

    override fun onIsupport(supportTokens: List<String>, message: String) {
        for (i in supportTokens) {
            val equalIndex = i.indexOf('=')
            if (equalIndex != -1) {
                isupportValueMap.put(i.substring(0, equalIndex), i.substring(equalIndex + 1))
            }
        }
    }
}