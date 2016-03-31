package com.tilal6991.channels.viewmodel

import android.databinding.ObservableField
import com.tilal6991.channels.collections.ObservableSortedArrayMap
import com.tilal6991.channels.util.failAssert
import com.tilal6991.channels.viewmodel.helper.UserMessageParser
import com.tilal6991.relay.EventListener
import com.tilal6991.relay.RegistrationDao
import com.tilal6991.relay.internal.protocol.PrefixSplitter
import timber.log.Timber

class ChannelManagerVM(
        initialNick: String,
        private val dao: RegistrationDao,
        private val channels: ObservableSortedArrayMap<String, ChannelVM>) : EventListener, UserMessageParser.Listener {

    private val selfNick: ObservableField<String> = ObservableField(initialNick)

    override fun onJoin(prefix: String, channel: String, optParams: Map<String, String>) {
        val nick = PrefixSplitter.nick(prefix)
        val self = nick == selfNick.get()
        val c: ChannelVM
        if (self) {
            val channelVM = channels[channel]
            if (channelVM == null) {
                c = ChannelVM(channel)
                channels.put(channel, c)
            } else {
                c = channelVM
            }
        } else {
            c = getChannelOrFail(channel) ?: return
        }
        c.onJoin(nick, self)
    }

    override fun onNames(channelName: String, nickList: List<String>, modeList: List<List<Char>>) {
        val channel = getChannelOrFail(channelName) ?: return
        for (i in nickList.indices) {
            channel.onName(nickList[i], modeList[i])
        }
    }

    override fun onPrivmsg(prefix: String, target: String, message: String, optParams: Map<String, String>) {
        if (!dao.isChannel(target)) {
            return
        }

        val nick = PrefixSplitter.nick(prefix)
        getChannelOrFail(target)?.onMessage(nick, message) ?: return
    }

    override fun onChannelMessage(channelVM: ChannelVM, message: String) {
        channelVM.onMessage(selfNick.get(), message)
    }

    override fun onNick(prefix: String, newNick: String) {
        val oldNick = PrefixSplitter.nick(prefix)
        for (i in 0..channels.size - 1) {
            val channel = channels.getValueAt(i)
            channel!!.onNickChange(oldNick, newNick)
        }

        if (oldNick == selfNick.get()) {
            selfNick.set(newNick)
        }
    }

    override fun onPart(prefix: String, channel: String) {
        val nick = PrefixSplitter.nick(prefix)
        val self = nick == selfNick.get()

        val c = channels[channel]
        if (c == null) {
            if (!self) Timber.asTree().failAssert()
            return
        }
        c.onPart(nick, self)
    }

    private fun getChannelOrFail(channelName: String): ChannelVM? {
        val channelVM = channels[channelName]
        if (channelVM == null) {
            Timber.asTree().failAssert()
        }
        return channelVM
    }
}