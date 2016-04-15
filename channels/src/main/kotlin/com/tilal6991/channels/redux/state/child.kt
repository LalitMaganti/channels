package com.tilal6991.channels.redux.state

import com.github.andrewoma.dexx.collection.Map
import com.tilal6991.channels.redux.util.TransactingIndexedList

interface ClientChild {

    val name: CharSequence
    val active: Boolean
    val buffer: TransactingIndexedList<CharSequence>
}

data class Server(override val name: String,
                  override val buffer: TransactingIndexedList<CharSequence> = TransactingIndexedList()) : ClientChild {

    override val active: Boolean
        get() = true
}

data class Channel(override val name: String,
                   override val active: Boolean,
                   override val buffer: TransactingIndexedList<CharSequence>,
                   val userMap: Map<String, User>) : ClientChild, Comparable<Channel> {

    override fun compareTo(other: Channel): Int {
        return name.compareTo(other.name, true)
    }

    class User(val nick: String, val mode: Char?)
}

fun Channel.mutate(name: String = this.name,
                   active: Boolean = this.active,
                   buffer: TransactingIndexedList<CharSequence> = this.buffer,
                   userMap: Map<String, Channel.User> = this.userMap): Channel {
    if (name === this.name && active === this.active && buffer === this.buffer && userMap === this.userMap) {
        return this
    }
    return Channel(name, active, buffer, userMap)
}