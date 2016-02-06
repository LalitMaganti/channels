package co.fusionx.channels.viewmodel.persistent

import co.fusionx.relay.protocol.ReplyCodes
import co.fusionx.relay.util.join

class ServerVM(override val name: String) : ClientChildVM() {

    fun onSocketConnect() {
        add("Connection was successful.")
    }

    fun onOtherCode(code: Int, arguments: List<String>) {
        if (displayedCodes.contains(code)) {
            add(" ".join(arguments))
        }
    }

    fun onWelcome(target: String, text: String) {
        add(text)
    }

    companion object {
        private val displayedCodes: Set<Int> = setOf(
                ReplyCodes.RPL_YOURHOST, ReplyCodes.RPL_CREATED, ReplyCodes.RPL_MYINFO,
                ReplyCodes.RPL_LUSERCLIENT, ReplyCodes.RPL_LUSEROP, ReplyCodes.RPL_LUSERUNKNOWN,
                ReplyCodes.RPL_LUSERCHANNELS, ReplyCodes.RPL_LUSERME, ReplyCodes.RPL_LOCALUSERS,
                ReplyCodes.RPL_GLOBALUSERS, ReplyCodes.RPL_STATSCONN, ReplyCodes.RPL_MOTDSTART,
                ReplyCodes.RPL_MOTD, ReplyCodes.RPL_ENDOFMOTD
        )
    }
}