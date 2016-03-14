package co.fusionx.channels.viewmodel.persistent

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.support.v7.util.SortedList
import co.fusionx.channels.collections.ObservableSortedList

class ChannelVM(override val name: String) : ClientChildVM() {

    val userMap: ObservableSortedList<UserVM> = ObservableSortedList(
            UserVM::class.java, UserComparator.instance)

    fun onPrivmsg(nick: String, message: String) {
        val user = userMap[userMap.binarySearch { it.nick.compareTo(nick) }]
        add("${user.nick}: $message")
    }

    fun onJoin(nick: String) {
        val userVM = UserVM(nick)
        userMap.add(userVM)

        add("${userVM.nick} joined the channel")
    }

    fun onName(nick: String, mode: List<Char>) {
        val userVM = UserVM(nick)
        userMap.add(userVM)
    }

    fun onNickChange(oldNick: String, newNick: String) {
        val index = userMap.binarySearch { oldNick.compareTo(it.nick) }
        if (index == SortedList.INVALID_POSITION) {
            return
        }
        val user = userMap[index]
        user.nick = newNick

        add("$oldNick is now known as $newNick")
    }

    inner class UserVM(initialNick: String) : BaseObservable() {
        var nick: String = initialNick
            @Bindable get
    }

    class UserComparator private constructor() : ObservableSortedList.HyperComparator<UserVM> {
        override fun areContentsTheSame(oldItem: UserVM, newItem: UserVM): Boolean {
            return oldItem.nick == newItem.nick
        }

        override fun areItemsTheSame(item1: UserVM, item2: UserVM): Boolean {
            return item1.nick == item2.nick
        }

        override fun compare(o1: UserVM, o2: UserVM): Int {
            return o1.nick.compareTo(o2.nick)
        }

        companion object {
            val instance by lazy { UserComparator() }
        }
    }
}