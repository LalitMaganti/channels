package com.tilal6991.channels.redux.state

import com.github.andrewoma.dexx.collection.Map
import com.tilal6991.channels.redux.util.TransactingIndexedList
import com.tilal6991.channels.redux.util.TransactingMap

interface ClientChild {
    val name: CharSequence
    val active: Boolean
    val buffer: TransactingIndexedList<CharSequence>
}

data class Server(override val name: String,
                  override val buffer: TransactingIndexedList<CharSequence> = TransactingIndexedList
                          .empty<CharSequence>()) : ClientChild {

    override val active: Boolean
        get() = true
}

data class Channel(override val name: String,
                   override val active: Boolean,
                   override val buffer: TransactingIndexedList<CharSequence>,
                   val userMap: Map<String, User>,
                   val modeMap: TransactingMap<Char, TransactingIndexedList<User>>) : ClientChild, Comparable<Channel> {

    override fun compareTo(other: Channel): Int {
        return name.compareTo(other.name, true)
    }

    data class User(val nick: String, val mode: Char?): Comparable<User> {
        override fun compareTo(other: User): Int {
            return nick.compareTo(other.nick)
        }

        companion object {
            const val NULL_MODE_CHAR = ' '
        }
    }
}

fun Channel.mutate(name: String = this.name,
                   active: Boolean = this.active,
                   buffer: TransactingIndexedList<CharSequence> = this.buffer,
                   userMap: Map<String, Channel.User> = this.userMap,
                   modeMap: TransactingMap<Char, TransactingIndexedList<Channel.User>> = this.modeMap): Channel {
    if (name === this.name && active === this.active && buffer === this.buffer
            && userMap === this.userMap && modeMap === modeMap) {
        return this
    }
    return Channel(name, active, buffer, userMap, modeMap)
}