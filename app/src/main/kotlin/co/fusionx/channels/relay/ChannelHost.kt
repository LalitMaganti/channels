package co.fusionx.channels.relay

import android.support.v7.util.SortedList
import co.fusionx.channels.databinding.SortedListDispatcher
import co.fusionx.channels.util.compareTo
import co.fusionx.relay.util.PrefixExtractor

public class ChannelHost(override val name: CharSequence) : ClientChild() {

    private val dispatcher = SortedListDispatcher(userComparator.value)
    val users: SortedList<CharSequence> = SortedList(CharSequence::class.java, dispatcher)

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

    public fun addCallback(callback: SortedListDispatcher.Callback) {
        dispatcher.addCallback(callback)
    }

    public fun removeCallback(userCallback: SortedListDispatcher.Callback) {
        dispatcher.removeCallback(userCallback)
    }

    companion object {
        private val userComparator = lazy { UserComparator() }
    }

    private class UserComparator : SortedListDispatcher.Comparator<CharSequence> {
        override fun areContentsTheSame(oldItem: CharSequence, newItem: CharSequence): Boolean {
            return oldItem == newItem
        }

        override fun areItemsTheSame(item1: CharSequence, item2: CharSequence): Boolean {
            return item1 == item2
        }

        override fun compare(o1: CharSequence, o2: CharSequence): Int {
            return o1.compareTo(o2)
        }
    }
}