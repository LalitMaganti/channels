package com.tilal6991.channels.redux.reducer

import com.brianegan.bansa.Action
import com.github.andrewoma.dexx.collection.IndexedLists
import com.github.andrewoma.dexx.collection.Maps
import com.tilal6991.channels.redux.Actions
import com.tilal6991.channels.redux.Events
import com.tilal6991.channels.redux.state.Channel
import com.tilal6991.channels.redux.state.Client
import com.tilal6991.channels.redux.state.ModeSection
import com.tilal6991.channels.redux.state.mutate
import com.tilal6991.channels.redux.util.*
import com.tilal6991.channels.util.failAssert
import com.tilal6991.relay.MoreStringUtils.nickFromPrefix
import timber.log.Timber
import java.util.*

fun channelsReducer(client: Client,
                    channels: TransactingIndexedList<Channel>,
                    a: Action): TransactingIndexedList<Channel> = when (a) {
    is Actions.RelayEvent -> channelRelayReducer(client, channels, a.event)
    else -> channels
}

fun channelRelayReducer(client: Client,
                        channels: TransactingIndexedList<Channel>,
                        event: Events.Event): TransactingIndexedList<Channel> = when (event) {
    is Events.OnJoin -> channels.findNullable(event.channel) { joinReducer(client, it, event) }
    is Events.OnPart -> channels.find(event.channel) { partReducer(client, it, event) }
    is Events.OnNames -> channels.find(event.channelName) { namesReducer(client, it, event) }
    is Events.OnPrivmsg -> channels.findNullable(event.target) { privmsgReducer(it, event) }
    else -> channels.transform { channelReducer(it, event) }
}

fun privmsgReducer(channel: Channel?, event: Events.OnPrivmsg): Channel? {
    // TODO(tilal6991) - more advanced parsing here based on CHANTYPES ISUPPORT token.
    return channel?.append("${event.prefix.nickFromPrefix()}: ${event.message}")
}

fun namesReducer(client: Client, channel: Channel, event: Events.OnNames): Channel {
    var userMap = channel.userMap
    var modeMap = channel.modeMap

    // TODO(tilal6991) - make this more intelligent by using builders and the like.
    for (i in event.nickList.indices) {
        val nick = event.nickList[i]
        val mode = event.modeList[i]

        val newMode = mode.getOrNull(0)
        val oldUser = userMap.get(nick)

        if (oldUser == null) {
            val newUser = Channel.User(nick, newMode)
            userMap = userMap.put(nick, newUser)
            modeMap = modeMap.addToUserList(client, newUser)
        } else if (oldUser.mode != newMode) {
            // This is actually a bug but let's take the opportunity to correct the effect of it.
            val oldMode = oldUser.mode ?: Channel.User.NULL_MODE_CHAR
            val index = modeMap.indexOfFirst { oldMode == it.char }
            if (index >= 0) {
                val users = modeMap.get(index).users
                val listIndex = users.binarySearch(oldUser.nick) { it.nick }

                if (listIndex >= 0) {
                    if (users.size() == 1) {
                        modeMap = modeMap.removeAt(index)
                    } else {
                        modeMap = modeMap.set(index, ModeSection(oldMode, users.removeAt(listIndex)))
                    }
                }
            }
            modeMap = modeMap.addToUserList(client, Channel.User(nick, newMode))
        }
    }
    return channel.mutate(userMap = userMap, modeMap = modeMap)
}

fun partReducer(client: Client, channel: Channel, event: Events.OnPart): Channel {
    return channel.findUser(event.prefix) {
        channel.mutate(
                active = if (nickFromPrefix(event.prefix) == client.nick) false else channel.active,
                userMap = channel.userMap.remove(it.nick),
                buffer = channel.buffer.append("${it.nick} has parted from the channel"))
    }
}

fun userComparator(ordering: String): Comparator<ModeSection> {
    return Comparator { l, r ->
        if (l.char == r.char) {
            0
        } else if (l.char == Channel.User.NULL_MODE_CHAR) {
            1
        } else if (r.char == Channel.User.NULL_MODE_CHAR) {
            -1
        } else {
            ordering.indexOf(l.char).compareTo(ordering.indexOf(r.char))
        }
    }
}

fun joinReducer(client: Client, channel: Channel?, event: Events.OnJoin): Channel {
    val nick = event.prefix.nickFromPrefix()
    val message = "$nick joined the channel"
    val user = Channel.User(nick, null)
    if (channel == null) {
        if (client.nick != nick) {
            Timber.v("Failed finding: ${event.channel} ${client.nick} $nick")
            for (i in client.channels) {
                Timber.v("In list: ${i.name} with compare ${i.name.compareTo(event.channel)}")
            }
            Timber.asTree().failAssert()
        }

        return Channel(
                name = event.channel,
                active = if (client.nick == nick) true else false,
                buffer = TransactingIndexedList.wrapping<CharSequence>(IndexedLists.of(message)),
                userMap = Maps.of(nick, user),
                modeMap = TransactingIndexedList.of(
                        ModeSection(Channel.User.NULL_MODE_CHAR, TransactingIndexedList.of(user))))
    } else if (client.nick == nick) {
        return channel.mutate(
                active = true,
                buffer = channel.buffer.append(message),
                userMap = Maps.of(nick, user),
                modeMap = TransactingIndexedList.of(
                        ModeSection(Channel.User.NULL_MODE_CHAR, TransactingIndexedList.of(user))))
    }

    return channel.mutate(
            buffer = channel.buffer.append(message),
            userMap = channel.userMap.put(nick, user),
            modeMap = channel.modeMap.addToUserList(client, user))
}

val ignoreCaseComparator = Comparator<String> { lhs, rhs -> lhs.compareTo(rhs, ignoreCase = true) }
fun TransactingIndexedList<Channel>.find(name: String, fn: (Channel) -> Channel): TransactingIndexedList<Channel> {
    return binaryMutate(name, { it.name }, { if (it == null) null else fn(it) }, ignoreCaseComparator)
}

fun TransactingIndexedList<Channel>.findNullable(name: String, fn: (Channel?) -> Channel?): TransactingIndexedList<Channel> {
    return binaryMutate(name, { it.name }, fn, ignoreCaseComparator)
}

fun TransactingIndexedList<ModeSection>.addToUserList(client: Client, user: Channel.User): TransactingIndexedList<ModeSection> {
    val modeChar = user.mode ?: Channel.User.NULL_MODE_CHAR
    val index = indexOfFirst { modeChar == it.char }
    if (index >= 0) {
        val modePair = get(index)
        return set(index, ModeSection(modeChar, modePair.users.addSorted(user)))
    } else {
        return addSorted(ModeSection(modeChar, TransactingIndexedList.of(user)),
                userComparator(client.connectionInfo.prefixes))
    }
}

fun channelReducer(channel: Channel,
                   event: Events.Event): Channel = when (event) {
    is Events.OnQuit -> {
        channel.findUser(event.prefix) {
            channel.mutate(
                    userMap = channel.userMap.remove(it.nick),
                    buffer = channel.buffer.append("${it.nick} has quit the server${getReason(event.message)}"))
        }
    }
    else -> channel
}

fun getReason(message: String?): String {
    return if (message == null) "" else " ($message)"
}

fun Channel.findUser(prefix: String, fn: (Channel.User) -> Channel): Channel {
    val nick = nickFromPrefix(prefix)
    val user = userMap.get(nick) ?: return this
    return fn(user)

}
