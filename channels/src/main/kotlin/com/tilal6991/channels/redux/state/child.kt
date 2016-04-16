package com.tilal6991.channels.redux.state

import com.github.andrewoma.dexx.collection.IndexedList
import com.github.andrewoma.dexx.collection.Map
import com.github.andrewoma.dexx.collection.Pair
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
                   val modeMap: TransactingIndexedList<ModeSection>) : ClientChild, Comparable<Channel> {

    override fun compareTo(other: Channel): Int {
        return name.compareTo(other.name, true)
    }

    data class User(val nick: String, val mode: Char?) : Comparable<User> {
        override fun compareTo(other: User): Int {
            return nick.compareTo(other.nick)
        }

        companion object {
            const val NULL_MODE_CHAR = ' '
        }
    }
}

data class ModeSection(val char: Char, val users: TransactingIndexedList<Channel.User>)

fun Channel.mutate(name: String = this.name,
                   active: Boolean = this.active,
                   buffer: TransactingIndexedList<CharSequence> = this.buffer,
                   userMap: Map<String, Channel.User> = this.userMap,
                   modeMap: TransactingIndexedList<ModeSection> = this.modeMap): Channel {
    if (name === this.name && active === this.active && buffer === this.buffer
            && userMap === this.userMap && modeMap === this.modeMap) {
        return this
    }
    return Channel(name, active, buffer, userMap, modeMap)
}