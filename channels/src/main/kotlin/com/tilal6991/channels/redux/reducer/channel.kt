package com.tilal6991.channels.redux.reducer

import com.github.andrewoma.dexx.collection.IndexedList
import com.github.andrewoma.dexx.collection.IndexedLists
import com.github.andrewoma.dexx.collection.Maps
import com.tilal6991.channels.redux.Action
import com.tilal6991.channels.redux.Events
import com.tilal6991.channels.redux.state.Channel
import com.tilal6991.channels.redux.state.Client
import com.tilal6991.channels.redux.state.mutate
import com.tilal6991.channels.redux.util.TransactingIndexedList
import com.tilal6991.channels.redux.util.binaryMutate
import com.tilal6991.channels.redux.util.nickFromPrefix
import com.tilal6991.channels.redux.util.transform
import com.tilal6991.channels.util.failAssert
import com.tilal6991.relay.MoreStringUtils
import timber.log.Timber

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
    is Events.OnPart -> channels.find(event.channel) { partReducer(it, event) }
    else -> channels.transform { channelReducer(it, event) }
}

fun partReducer(channel: Channel, event: Events.OnPart): Channel {
    return channel.findUser(event.prefix) {
        channel.mutate(
                userMap = channel.userMap.remove(it.nick),
                buffer = channel.buffer.append("${it.nick} has parted from the channel"))
    }
}

fun joinReducer(client: Client, channel: Channel?, event: Events.OnJoin): Channel {
    val nick = event.prefix.nickFromPrefix()
    val message = "$nick joined the channel"
    if (channel == null) {
        if (client.nick != nick) {
            Timber.v("Failed finding: ${event.channel} ${client.nick} $nick")
            Timber.asTree().failAssert()
        }

        return Channel(
                event.channel,
                if (client.nick == nick) true else false,
                TransactingIndexedList.builder<CharSequence>().add(message).build(),
                Maps.of(nick, Channel.User(nick, null)))
    }

    return channel.mutate(
            buffer = channel.buffer.append(message),
            userMap = channel.userMap.put(nick, Channel.User(nick, null)))
}

fun TransactingIndexedList<Channel>.find(name: String, fn: (Channel) -> Channel): TransactingIndexedList<Channel> {
    return binaryMutate(name, { it.name }, { if (it == null) null else fn(it) })
}

fun TransactingIndexedList<Channel>.findNullable(name: String, fn: (Channel?) -> Channel): TransactingIndexedList<Channel> {
    return binaryMutate(name, { it.name }, fn)
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
    val nick = MoreStringUtils.nickFromPrefix(prefix)
    val user = userMap.get(nick) ?: return this
    return fn(user)

}
