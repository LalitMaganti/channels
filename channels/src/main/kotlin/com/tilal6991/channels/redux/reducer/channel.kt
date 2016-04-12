package com.tilal6991.channels.redux.reducer

import com.github.andrewoma.dexx.collection.IndexedLists
import com.github.andrewoma.dexx.collection.Maps
import com.tilal6991.channels.redux.Action
import com.tilal6991.channels.redux.Events
import com.tilal6991.channels.redux.state.Channel
import com.tilal6991.channels.redux.state.Client
import com.tilal6991.channels.redux.state.mutate
import com.tilal6991.channels.redux.util.SortedIndexedList
import com.tilal6991.channels.redux.util.binaryMutate
import com.tilal6991.channels.redux.util.nickFromPrefix
import com.tilal6991.channels.redux.util.transform
import com.tilal6991.channels.util.failAssert
import timber.log.Timber

fun channelsReducer(client: Client,
                    channels: SortedIndexedList<Channel>,
                    a: Action): SortedIndexedList<Channel> = when (a) {
    is Action.RelayEvent -> channelRelayReducer(client, channels, a.event)
    else -> channels
}

fun channelRelayReducer(client: Client,
                        channels: SortedIndexedList<Channel>,
                        event: Events.Event): SortedIndexedList<Channel> = when (event) {
    is Events.OnJoin ->
        channels.binaryMutate(event.channel, { it.name }) { joinReducer(client, it, event) }
    else -> channels.transform { channelReducer(client, it, event) }
}

fun joinReducer(client: Client, channel: Channel?, event: Events.OnJoin): Channel {
    val nick = event.prefix.nickFromPrefix()
    val message = "$nick joined the channel"
    if (channel == null) {
        if (client.nick != nick) {
            Timber.asTree().failAssert()
        }

        return Channel(
                event.channel,
                if (client.nick == nick) true else false,
                IndexedLists.of<CharSequence>(message),
                Maps.of(nick, Channel.User(nick, null)))
    }

    return channel.mutate(
            buffer = channel.buffer.append(message),
            userMap = channel.userMap.put(nick, Channel.User(nick, null)))
}

fun channelReducer(client: Client,
                   channel: Channel,
                   event: Events.Event): Channel = when (event) {
    else -> channel
}