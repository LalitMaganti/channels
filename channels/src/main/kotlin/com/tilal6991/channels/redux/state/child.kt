package com.tilal6991.channels.redux.state

import com.github.andrewoma.dexx.collection.IndexedList
import com.github.andrewoma.dexx.collection.Map
import com.github.andrewoma.dexx.collection.Vector

interface ClientChild {

    val name: CharSequence
    val active: Boolean
    val buffer: IndexedList<CharSequence>
}

data class Server(override val name: String,
                  override val buffer: IndexedList<CharSequence> = Vector.empty()) : ClientChild {

    override val active: Boolean
        get() = true
}

data class Channel(override val name: String,
                   override val active: Boolean,
                   override val buffer: IndexedList<CharSequence>,
                   val userMap: Map<String, User>) : ClientChild, Comparable<Channel> {

    override fun compareTo(other: Channel): Int {
        return name.compareTo(other.name, true)
    }

    class User(val nick: String, val mode: Char?)
}

fun Channel.mutate(name: String = this.name,
                   active: Boolean = this.active,
                   buffer: IndexedList<CharSequence> = this.buffer,
                   userMap: Map<String, Channel.User> = this.userMap): Channel {
    if (name === this.name && active === this.active && buffer === this.buffer && userMap === this.userMap) {
        return this
    }
    return Channel(name, active, buffer, userMap)
}