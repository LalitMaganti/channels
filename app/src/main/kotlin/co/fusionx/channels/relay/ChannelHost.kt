package co.fusionx.channels.relay

import android.support.v7.util.SortedList
import co.fusionx.relay.util.PrefixExtractor
import java.util.*

public class ChannelHost(private val name: CharSequence) : ClientChild() {

    private val dispatcher = DispatchingCallback()
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

    public fun addUserCallback(userCallback: UserCallback) {
        dispatcher.addUserCallback(userCallback)
    }

    public fun removeUserCallback(userCallback: UserCallback) {
        dispatcher.removeUserCallback(userCallback)
    }

    override fun getName() = name

    public abstract class UserCallback : SortedList.Callback<CharSequence>() {
        override final fun areContentsTheSame(oldItem: CharSequence, newItem: CharSequence): Boolean {
            return oldItem == newItem
        }

        override final fun areItemsTheSame(item1: CharSequence, item2: CharSequence): Boolean {
            return item1 == item2
        }

        override final fun compare(o1: CharSequence, o2: CharSequence): Int {
            for (i in 0..o1.length - 1) {
                val a = o1[i]
                val b = o2[i]
                if (a < b) {
                    return -1
                } else if (a > b) {
                    return 1
                }
            }
            return o1.length - o2.length
        }
    }

    private class DispatchingCallback : UserCallback() {
        private val callbacks: MutableList<UserCallback> = ArrayList()

        override fun onChanged(position: Int, count: Int) {
            for (c in callbacks) c.onChanged(position, count)
        }

        override fun onInserted(position: Int, count: Int) {
            for (c in callbacks) c.onInserted(position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            for (c in callbacks) c.onMoved(fromPosition, toPosition)
        }

        override fun onRemoved(position: Int, count: Int) {
            for (c in callbacks) c.onRemoved(position, count)
        }

        fun addUserCallback(userCallback: UserCallback) {
            callbacks.add(userCallback)
        }

        fun removeUserCallback(userCallback: UserCallback) {
            callbacks.remove(userCallback)
        }
    }
}