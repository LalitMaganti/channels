package co.fusionx.channels.viewmodel.persistent

import co.fusionx.channels.collections.ObservableSortedArrayMap
import co.fusionx.relay.ConnectionInformationListener
import co.fusionx.channels.util.failAssert
import co.fusionx.channels.viewmodel.helper.UserMessageParser
import co.fusionx.relay.EventListener
import co.fusionx.relay.util.PrefixExtractor
import co.fusionx.relay.util.isChannel
import timber.log.Timber
import java.util.*

class UserChannelDao(
        initialNick: String,
        private val channels: ObservableSortedArrayMap<String, ChannelVM>,
        private val connectionInformation: ConnectionInformationListener) :
        EventListener, UserMessageParser.ParserListener {

    private var selfNick: String = initialNick

    private val isupportValues: ConnectionInformationListener.ISUPPORTValues
        get() = connectionInformation.isupportValues

    override fun onJoin(prefix: String, channel: String) {
        val nick = PrefixExtractor.nick(prefix)
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

        val nick = PrefixExtractor.nick(prefix)
        getChannelOrFail(target)?.onPrivmsg(nick, message) ?: return
    }

    override fun onChannelMessage(channelVM: ChannelVM, message: String) {
        channelVM.onPrivmsg(selfNick, message)
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