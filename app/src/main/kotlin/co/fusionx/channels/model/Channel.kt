package co.fusionx.channels.model

import android.support.v7.util.SortedList
import co.fusionx.channels.databinding.SortedListCallbackRegistry
import co.fusionx.channels.util.compareTo
import co.fusionx.relay.util.PrefixExtractor

public class Channel(override val name: CharSequence) : ClientChild() {

    private val registry = SortedListCallbackRegistry(UserComparator.instance)
    val users: SortedList<CharSequence> = SortedList(CharSequence::class.java, registry)

    fun onPrivmsg(prefix: String, message: String) {
        add("${PrefixExtractor.nick(prefix)}: $message")
    }

    fun onJoin(prefix: String) {
        val nick = PrefixExtractor.nick(prefix)
        users.add(nick)

        add("$nick joined the channel")
    }

    fun onNames(nickList: List<String>) {
        users.addAll(nickList)
    }

    public fun addUsersCallback(callback: SortedListCallbackRegistry.Callback) {
        registry.addCallback(callback)
    }

    public fun removeUsersCallback(callback: SortedListCallbackRegistry.Callback) {
        registry.removeCallback(callback)
    }

    private class UserComparator private constructor() : SortedListCallbackRegistry.Comparator<CharSequence> {
        override fun areContentsTheSame(oldItem: CharSequence, newItem: CharSequence): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(item1: CharSequence, item2: CharSequence): Boolean {
            return item1 == item2
        }

        override fun compare(o1: CharSequence, o2: CharSequence): Int {
            return o1.compareTo(o2)
        }

        companion object {
            public val instance by lazy { UserComparator() }
        }
    }
}