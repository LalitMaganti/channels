package com.tilal6991.channels.viewmodel

import android.databinding.ObservableField
import android.databinding.ObservableList
import android.support.v4.util.LruCache
import com.tilal6991.channels.collections.ObservableIndexedMap
import com.tilal6991.channels.util.UserPrefixComparator
import com.tilal6991.channels.util.failAssert
import com.tilal6991.channels.viewmodel.helper.UserMessageParser
import com.tilal6991.relay.EventListener
import com.tilal6991.relay.RegistrationDao
import com.tilal6991.relay.internal.protocol.PrefixSplitter
import timber.log.Timber

class ChannelManagerVM(
        initialNick: String,
        private val dao: RegistrationDao,
        private val channelMap: ObservableIndexedMap<String, ChannelVM>) : EventListener,
        UserMessageParser.Listener, ClientVM.StatusListener {

    val channels: ObservableList<ChannelVM>
        get() = channelMap.valuesList

    private val comparator = UserPrefixComparator.create(dao)
    private val selfNick: ObservableField<String> = ObservableField(initialNick)

    override fun onJoin(prefix: String, channel: String, optParams: Map<String, String>) {
        val nick = PrefixSplitter.nick(prefix)
        val self = nick == selfNick.get()
        val c: ChannelVM
        if (self) {
            val channelVM = channelMap[channel]
            if (channelVM == null) {
                c = ChannelVM(channel, comparator)
                channelMap.put(channel, c)
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
        for (i in 0..channelMap.size - 1) {
            val channel = channelMap.getValueAt(i)
            channel.onNickChange(oldNick, newNick)
        }

        if (oldNick == selfNick.get()) {
            selfNick.set(newNick)
        }
    }

    override fun onPart(prefix: String, channel: String) {
        val nick = PrefixSplitter.nick(prefix)
        val self = nick == selfNick.get()

        val c = channelMap[channel]
        if (c == null && !self) {
            return Timber.asTree().failAssert()
        }
        c?.onPart(nick, self)
    }

    override fun onQuit(prefix: String, message: String?) {
        for (i in 0..channelMap.size - 1) {
            val c = channelMap.getValueAt(i)
            val nick = PrefixSplitter.nick(prefix)
            val self = nick == selfNick.get()
            c.onQuit(nick, self, message)
        }
    }

    private fun getChannelOrFail(channelName: String): ChannelVM? {
        val channelVM = channelMap[channelName]
        if (channelVM == null) {
            Timber.asTree().failAssert()
        }
        return channelVM
    }

    // Status listener.
    override fun onSocketConnect() = channels.forEach { it.onSocketConnect() }
    override fun onConnectFailed() = channels.forEach { it.onConnectFailed() }
    override fun onDisconnecting() = channels.forEach { it.onDisconnecting() }
    override fun onDisconnected() = channels.forEach { it.onDisconnected() }
    override fun onConnecting() = channels.forEach { it.onConnecting() }
    override fun onReconnecting() = channels.forEach { it.onReconnecting() }
}