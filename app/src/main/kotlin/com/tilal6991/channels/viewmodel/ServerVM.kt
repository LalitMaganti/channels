package com.tilal6991.channels.viewmodel

import com.tilal6991.channels.util.join
import com.tilal6991.relay.EventListener
import com.tilal6991.relay.protocol.ReplyCodes
import java.util.*

class ServerVM(override val name: String) : ClientChildVM(), EventListener {

    fun onConnecting() {
        add("Connecting to the server.")
    }

    override fun onSocketConnect() {
        add("Connection was successful.")
    }

    override fun onWelcome(target: String, text: String) {
        add(text)
    }

    override fun onConnectFailed() {
        add("Failed to connect to the server.")
    }

    override fun onDisconnect(triggered: Boolean) {
        add("Disconnected from the server.")
    }

    override fun onOtherCode(code: Int, arguments: List<String>) {
        if (displayedCodes.contains(code)) {
            add(" ".join(arguments))
        }
    }

    override fun onNotice(prefix: String, target: String, message: String, optParams: Map<String, String>) {
        add(message)
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