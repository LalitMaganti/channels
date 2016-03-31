package com.tilal6991.channels.viewmodel

import android.databinding.Bindable
import com.tilal6991.channels.util.join
import com.tilal6991.relay.EventListener
import com.tilal6991.relay.protocol.ReplyCodes
import java.util.*

class ServerVM(override val name: String) : ClientChildVM(), EventListener {

    override fun onWelcome(target: String, text: String) {
        add(text)
    }

    override fun onOtherCode(code: Int, arguments: List<String>) {
        if (displayedCodes.contains(code)) {
            add(" ".join(arguments))
        }
    }

    override fun onNotice(prefix: String, target: String, message: String, optParams: Map<String, String>) {
        add(message)
    }

    fun onSocketConnect() {
        active = true
        add("Connection was successful.")
    }

    fun onConnectFailed() {
        active = false
        add("Failed to connect to the server.")
    }

    fun onDisconnecting() {
        active = false
        add("Disconnecting from the server.")
    }

    fun onDisconnected() {
        active = false
        add("Disconnected from the server.")
    }

    fun onConnecting() {
        active = false
        add("Connecting to the server.")
    }

    fun onReconnecting() {
        active = false
        add("Trying to reconnect in 5 seconds.")
    }

    companion object {
        private val displayedCodes: Set<Int> = arrayOf(
                ReplyCodes.RPL_YOURHOST, ReplyCodes.RPL_CREATED, ReplyCodes.RPL_MYINFO,
                ReplyCodes.RPL_LUSERCLIENT, ReplyCodes.RPL_LUSEROP, ReplyCodes.RPL_LUSERUNKNOWN,
                ReplyCodes.RPL_LUSERCHANNELS, ReplyCodes.RPL_LUSERME, ReplyCodes.RPL_LOCALUSERS,
                ReplyCodes.RPL_GLOBALUSERS, ReplyCodes.RPL_STATSCONN, ReplyCodes.RPL_MOTDSTART,
                ReplyCodes.RPL_MOTD, ReplyCodes.RPL_ENDOFMOTD
        ).toCollection(HashSet())
    }
}