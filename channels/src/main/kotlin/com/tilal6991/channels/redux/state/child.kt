package com.tilal6991.channels.redux.state

import com.github.andrewoma.dexx.collection.HashMap
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
                   override val active: Boolean = false,
                   override val buffer: IndexedList<CharSequence> = Vector.empty(),
                   val userMap: Map<String, User> = HashMap.empty()) : ClientChild, Comparable<Channel> {

    override fun compareTo(other: Channel): Int {
        return name.compareTo(other.name, true)
    }

    class User(val nick: String, val mode: Char?)
}