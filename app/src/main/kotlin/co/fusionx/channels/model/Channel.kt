package co.fusionx.channels.model

import co.fusionx.channels.collections.ObservableSortedList
import co.fusionx.channels.util.compareTo
import co.fusionx.relay.util.PrefixExtractor

class Channel(override val name: CharSequence) : ClientChild() {

    val users: ObservableSortedList<CharSequence> = ObservableSortedList(
            CharSequence::class.java, UserComparator.instance)

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

    private class UserComparator private constructor() : ObservableSortedList.HyperComparator<CharSequence> {
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
            val instance by lazy { UserComparator() }
        }
    }
}