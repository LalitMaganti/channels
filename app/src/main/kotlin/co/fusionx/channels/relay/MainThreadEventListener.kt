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

    override fun onNames(channelName: String, nickList: List<String>) = postForEach {
        it.onNames(channelName, nickList)
    }

    override fun onNickChange(oldNick: String, newNick: String) = postForEach {
        it.onNickChange(oldNick, newNick)
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

    private inline fun postForEach(crossinline fn: (EventListener) -> Unit) {
        handler.post { children.forEach(fn) }
    }

    fun addEventListener(eventListener: EventListener) {
        children.add(eventListener)
    }
}