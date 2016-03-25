package co.fusionx.channels.viewmodel.persistent

import co.fusionx.channels.collections.ObservableSortedArrayMap
import co.fusionx.relay.RegistrationDao
import co.fusionx.channels.util.failAssert
import co.fusionx.channels.viewmodel.helper.UserMessageParser
import co.fusionx.relay.EventListener
import co.fusionx.relay.protocol.PrefixSplitter
import co.fusionx.relay.util.isChannel
import timber.log.Timber
import java.util.*

class ChannelManagerVM(
        initialNick: String,
        private val channels: ObservableSortedArrayMap<String, ChannelVM>) :
        EventListener, UserMessageParser.Listener {

    private var selfNick: String = initialNick

    override fun onJoin(prefix: String, channel: String) {
        val nick = PrefixSplitter.nick(prefix)
        val c: ChannelVM
        if (nick == selfNick) {
            c = ChannelVM(channel)
            channels.put(channel, c)
        } else {
            c = getChannelOrFail(channel) ?: return
        }
        c.onJoin(nick)
    }

    override fun onNames(channelName: String, nickList: List<String>, modeList: List<List<Char>>) {
        val channel = getChannelOrFail(channelName) ?: return
        for (i in nickList.indices) {
            channel.onName(nickList[i], modeList[i])
        }
    }

    override fun onPrivmsg(prefix: String, target: String, message: String) {
        if (!target.isChannel()) {
            return
        }

        val nick = PrefixSplitter.nick(prefix)
        getChannelOrFail(target)?.onMessage(nick, message) ?: return
    }

    override fun onChannelMessage(channelVM: ChannelVM, message: String) {
        channelVM.onMessage(selfNick, message)
    }

    override fun onNick(oldNick: String, newNick: String) {
        for (i in 0..channels.size - 1) {
            val channel = channels.getValueAt(i)
            channel!!.onNickChange(oldNick, newNick)
        }

        if (oldNick == selfNick) {
            selfNick = newNick
        }
    }

    private fun getChannelOrFail(channelName: String): ChannelVM? {
        val channelVM = channels[channelName]
        if (channelVM == null) {
            Timber.asTree().failAssert()
        }
        return channelVM
    }
}