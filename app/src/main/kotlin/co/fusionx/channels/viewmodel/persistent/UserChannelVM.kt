package co.fusionx.channels.viewmodel.persistent

import co.fusionx.channels.collections.ObservableSortedArrayMap
import co.fusionx.channels.relay.ConnectionInformation
import co.fusionx.channels.util.failAssert
import co.fusionx.relay.EventListener
import co.fusionx.relay.util.PrefixExtractor
import co.fusionx.relay.util.isChannel
import timber.log.Timber

class UserChannelVM(
        private val initialNick: String,
        private val channels: ObservableSortedArrayMap<String, ChannelVM>,
        private val connectionInformation: ConnectionInformation) : EventListener {

    val selfNick: String = initialNick

    private val isupportValues: ConnectionInformation.ISUPPORTValues
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

    override fun onNames(channelName: String, namesList: List<String>) {
        val channel = getChannelOrFail(channelName) ?: return

        for (n in namesList) {
            val mode = isupportValues.channelModes.find { n[0] == it }
            val nick = if (mode == null) n else n.substring(1)
            channel.onName(n, mode)
        }
    }

    override fun onPrivmsg(prefix: String, target: String, message: String) {
        if (!target.isChannel()) {
            return
        }

        val nick = PrefixExtractor.nick(prefix)
        getChannelOrFail(target)?.onPrivmsg(nick, message) ?: return
    }

    override fun onNickChange(oldNick: String, newNick: String) {
        for (i in 0..channels.size - 1) {
            val channel = channels.getValueAt(i)
            channel!!.onNickChange(oldNick, newNick)
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