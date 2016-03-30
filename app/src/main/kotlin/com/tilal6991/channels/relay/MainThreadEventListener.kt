package com.tilal6991.channels.relay

import android.os.Handler
import android.os.Looper
import com.tilal6991.relay.EventListener
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
    
    override fun onConnectFailed() = postForEach {
        it.onConnectFailed()
    }

    override fun onDisconnect(triggered: Boolean) = postForEach {
        it.onDisconnect(triggered)
    }

    override fun onInvite(prefix: String, target: String, channel: String) = postForEach {
        it.onInvite(prefix, target, channel)
    }

    override fun onCapNew(caps: List<String>) = postForEach {
        it.onCapNew(caps)
    }

    override fun onCapDel(caps: List<String>) = postForEach {
        it.onCapDel(caps)
    }

    override fun onAccountLogin(prefix: String, account: String) = postForEach {
        it.onAccountLogin(prefix, account)
    }

    override fun onAccountLogout(prefix: String) = postForEach {
        it.onAccountLogout(prefix)
    }

    override fun onAway(prefix: String, message: String) = postForEach {
        it.onAway(prefix, message)
    }

    override fun onAwayEnd(prefix: String) = postForEach {
        it.onAwayEnd(prefix)
    }

    override fun onBatch(referenceTag: String, type: String, batchArgs: List<String>) = postForEach {
        it.onBatch(referenceTag, type, batchArgs)
    }

    override fun onChghost(prefix: String?, newUser: String, newHost: String) = postForEach {
        it.onChghost(prefix, newUser, newHost)
    }

    private inline fun postForEach(crossinline fn: (EventListener) -> Unit) {
        handler.post { children.forEach(fn) }
    }

    fun addEventListener(eventListener: EventListener) {
        children.add(eventListener)
    }
}