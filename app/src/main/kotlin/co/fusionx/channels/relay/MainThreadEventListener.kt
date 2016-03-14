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

    override fun onNick(oldNick: String, newNick: String) = postForEach {
        it.onNick(oldNick, newNick)
    }

    override fun onPing(server: String) = postForEach {
        it.onPing(server)
    }

    override fun onJoin(prefix: String, channel: String) = postForEach {
        it.onJoin(prefix, channel)
    }

    override fun onPrivmsg(prefix: String, target: String, message: String) = postForEach {
        it.onPrivmsg(prefix, target, message)
    }

    override fun onNotice(prefix: String, target: String, message: String) = postForEach {
        it.onNotice(prefix, target, message)
    }

    override fun onIsupport(supportTokens: List<String>, message: String) = postForEach {
        it.onIsupport(supportTokens, message)
    }

    override fun onCapLs(caps: List<String>) = postForEach {
        it.onCapLs(caps)
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