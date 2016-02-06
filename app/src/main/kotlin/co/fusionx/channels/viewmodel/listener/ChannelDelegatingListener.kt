package co.fusionx.channels.viewmodel.listener

import co.fusionx.channels.collections.ObservableSortedArrayMap
import co.fusionx.channels.util.failAssert
import co.fusionx.channels.viewmodel.persistent.ChannelVM
import co.fusionx.channels.viewmodel.persistent.UserVM
import co.fusionx.relay.EventListener
import co.fusionx.relay.util.PrefixExtractor
import co.fusionx.relay.util.isChannel
import timber.log.Timber

class ChannelDelegatingListener(
        private val selfUser: UserVM,
        private val users: MutableMap<String, UserVM>,
        private val channels: ObservableSortedArrayMap<CharSequence, ChannelVM>) : EventListener {

    override fun onJoin(prefix: String, channel: String) {
        val nick = PrefixExtractor.nick(prefix)
        if (nick == selfUser.nick) {
            val c = ChannelVM(channel)
            channels.put(channel, c)
            c.onJoin(selfUser)
            selfUser.onJoin(c)
        } else {
            val c = getChannelOrFail(channel) ?: return
            val u = users.getOrPut(nick) { UserVM(nick) }
            c.onJoin(u)
            u.onJoin(c)
        }
    }

    override fun onNames(channelName: String, nickList: List<String>) {
        val userList = nickList.map { users.getOrPut(it) { UserVM(it) } }
        getChannelOrFail(channelName)?.onNames(userList) ?: return
    }

    override fun onPrivmsg(prefix: String, target: String, message: String) {
        if (!target.isChannel()) {
            return
        }

        val nick = PrefixExtractor.nick(prefix)
        val user = getUserOrFail(nick) ?: return
        getChannelOrFail(target)?.onPrivmsg(user, message) ?: return
    }

    override fun onNickChange(oldNick: String, newNick: String) {
        val u = getUserOrFail(oldNick) ?: return
        val indices = u.channels.map { it.users.indexOf(u) }
        u.onNickChange(newNick)

        for (i in indices.withIndex()) {
            val channelVM = u.channels[i.index]
            channelVM.onNickChange(i.value, oldNick, newNick)
        }
    }

    private fun getUserOrFail(nick: String): UserVM? {
        val userVM = users[nick]
        if (userVM == null) {
            Timber.asTree().failAssert()
        }
        return userVM
    }

    private fun getChannelOrFail(channelName: String): ChannelVM? {
        val channelVM = channels[channelName]
        if (channelVM == null) {
            Timber.asTree().failAssert()
        }
        return channelVM
    }
}