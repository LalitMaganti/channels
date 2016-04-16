package com.tilal6991.channels.redux.reducer

import com.github.andrewoma.dexx.collection.IndexedList
import com.github.andrewoma.dexx.collection.IndexedLists
import com.github.andrewoma.dexx.collection.Maps
import com.github.andrewoma.dexx.collection.TreeMap
import com.tilal6991.channels.redux.Action
import com.tilal6991.channels.redux.Events
import com.tilal6991.channels.redux.state.Channel
import com.tilal6991.channels.redux.state.Client
import com.tilal6991.channels.redux.state.mutate
import com.tilal6991.channels.redux.util.*
import com.tilal6991.channels.util.failAssert
import com.tilal6991.relay.MoreStringUtils
import com.tilal6991.relay.MoreStringUtils.nickFromPrefix
import timber.log.Timber
import java.util.*

fun channelsReducer(client: Client,
                    channels: TransactingIndexedList<Channel>,
                    a: Action): TransactingIndexedList<Channel> = when (a) {
    is Action.RelayEvent -> channelRelayReducer(client, channels, a.event)
    else -> channels
}

fun channelRelayReducer(client: Client,
                        channels: TransactingIndexedList<Channel>,
                        event: Events.Event): TransactingIndexedList<Channel> = when (event) {
    is Events.OnJoin -> channels.findNullable(event.channel) { joinReducer(client, it, event) }
    is Events.OnPart -> channels.find(event.channel) { partReducer(client, it, event) }
    is Events.OnNames -> channels.find(event.channelName) { namesReducer(it, event) }
    else -> channels.transform { channelReducer(it, event) }
}

fun namesReducer(channel: Channel, event: Events.OnNames): Channel {
    var userMap = channel.userMap
    var modeMap = channel.modeMap

    for (i in event.nickList.indices) {
        val nick = event.nickList[i]
        val mode = event.modeList[i]
        var user = userMap.get(nick)
        val modeChar = mode.getOrNull(0) ?: Channel.User.NULL_MODE_CHAR

        if (user == null) {
            user = Channel.User(nick, mode.getOrNull(0))
            userMap = userMap.put(nick, user)
        } else if (user.mode != mode.getOrNull(0)) {
            // This is actually a bug but let's take the opportunity to correct the effect of it.
            val list = modeMap.get(user.mode ?: Channel.User.NULL_MODE_CHAR)

            // If list is null, this is an even worse bug but means we have nothing to do.
            list?.removeAt(list.binarySearch(user.nick) { it.nick })
        }

        modeMap = modeMap.putAddSorted(modeChar, user)
    }

    return channel.mutate(
            userMap = userMap,
            modeMap = modeMap)
}

fun partReducer(client: Client, channel: Channel, event: Events.OnPart): Channel {
    return channel.findUser(event.prefix) {
        channel.mutate(
                active = if (nickFromPrefix(event.prefix) == client.nick) false else channel.active,
                userMap = channel.userMap.remove(it.nick),
                buffer = channel.buffer.append("${it.nick} has parted from the channel"))
    }
}

fun userComparator(ordering: IndexedList<Char>): Comparator<Char> {
    return Comparator { l, r ->
        if (l == r) 0 else ordering.indexOf(l).compareTo(ordering.indexOf(r))
    }
}

fun joinReducer(client: Client, channel: Channel?, event: Events.OnJoin): Channel {
    val nick = event.prefix.nickFromPrefix()
    val message = "$nick joined the channel"
    val user = Channel.User(nick, null)
    val comparator = userComparator(client.connectionInfo.prefixes)
    if (channel == null) {
        if (client.nick != nick) {
            Timber.v("Failed finding: ${event.channel} ${client.nick} $nick")
            for (i in client.channels) {
                println("In list: ${i.name} with compare ${i.name.compareTo(event.channel)}")
            }
            Timber.asTree().failAssert()
        }

        return Channel(
                name = event.channel,
                active = if (client.nick == nick) true else false,
                buffer = TransactingIndexedList.wrapping<CharSequence>(IndexedLists.of(message)),
                userMap = Maps.of(nick, user),
                modeMap = TransactingMap.wrapping(TreeMap(comparator, null)))
    } else if (client.nick == nick) {
        return channel.mutate(
                active = true,
                buffer = channel.buffer.append(message),
                userMap = channel.userMap.put(nick, user),
                modeMap = TransactingMap.wrapping(TreeMap(comparator, null)))
    }

    return channel.mutate(
            buffer = channel.buffer.append(message),
            userMap = channel.userMap.put(nick, user),
            modeMap = channel.modeMap.putAddSorted(user.mode ?: Channel.User.NULL_MODE_CHAR, user))
}

val ignoreCaseComparator = Comparator<String> { lhs, rhs -> lhs.compareTo(rhs, ignoreCase = true) }
fun TransactingIndexedList<Channel>.find(name: String, fn: (Channel) -> Channel): TransactingIndexedList<Channel> {
    return binaryMutate(name, { it.name }, { if (it == null) null else fn(it) }, ignoreCaseComparator)
}

fun TransactingIndexedList<Channel>.findNullable(name: String, fn: (Channel?) -> Channel): TransactingIndexedList<Channel> {
    return binaryMutate(name, { it.name }, fn, ignoreCaseComparator)
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
