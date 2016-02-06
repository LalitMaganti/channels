package co.fusionx.channels.model.helper

import co.fusionx.relay.EventListener

class ChannelEventListener : EventListener {

    override fun onNames(channelName: String, nickList: List<String>) {
        super.onNames(channelName, nickList)
    }

    override fun onNickChange(oldNick: String, newNick: String) {
        super.onNickChange(oldNick, newNick)
    }

    override fun onPing(server: String) {
        super.onPing(server)
    }

    override fun onJoin(prefix: String, channel: String) {
        super.onJoin(prefix, channel)
    }

    override fun onPrivmsg(prefix: String, target: String, message: String) {
        super.onPrivmsg(prefix, target, message)
    }
}