package co.fusionx.channels.relay

import android.os.Handler
import android.os.Looper
import co.fusionx.relay.EventListener
import java.util.*

class MainThreadEventListener : EventListener {

    private val children: MutableCollection<EventListener> = ArrayList()
    private val handler = Handler(Looper.getMainLooper())

    override fun onSocketConnect() = postForEach {
        it.onSocketConnect()
    }

    override fun onOtherCode(code: Int, arguments: List<String>) = postForEach {
        it.onOtherCode(code, arguments)
    }

    override fun onWelcome(target: String, text: String) = postForEach {
        it.onWelcome(target, text)
    }

    override fun onNames(
            channelName: String, nickList: List<String>, modeList: List<List<Char>>) = postForEach {
        it.onNames(channelName, nickList, modeList)
    }

    override fun onNick(prefix: String, newNick: String) = postForEach {
        it.onNick(prefix, newNick)
    }

    override fun onPing(hostName: String?) = postForEach {
        it.onPing(hostName)
    }

    override fun onJoin(prefix: String, channel: String, optParams: Map<String, String>) = postForEach {
        it.onJoin(prefix, channel, optParams)
    }

    override fun onPrivmsg(prefix: String, target: String, message: String, optParams: Map<String, String>) = postForEach {
        it.onPrivmsg(prefix, target, message, optParams)
    }

    override fun onNotice(prefix: String, target: String, message: String, optParams: Map<String, String>) = postForEach {
        it.onNotice(prefix, target, message, optParams)
    }

    override fun onIsupport(keys: List<String>, values: List<String?>, message: String) = postForEach {
        it.onIsupport(keys, values, message)
    }

    override fun onCapLs(caps: List<String>, values: List<String?>, finalLine: Boolean) = postForEach {
        it.onCapLs(caps, values, finalLine)
    }

    override fun onCapAck(caps: List<String>) = postForEach {
        it.onCapAck(caps)
    }

    override fun onCapNak(caps: List<String>) = postForEach {
        it.onCapNak(caps)
    }

    override fun onAuthenticate(data: String) = postForEach {
        it.onAuthenticate(data)
    }

    private inline fun postForEach(crossinline fn: (EventListener) -> Unit) {
        handler.post { children.forEach(fn) }
    }

    fun addEventListener(eventListener: EventListener) {
        children.add(eventListener)
    }
}