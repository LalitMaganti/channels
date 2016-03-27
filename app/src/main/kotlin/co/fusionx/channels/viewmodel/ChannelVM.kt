package co.fusionx.channels.viewmodel

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.support.v7.util.SortedList
import co.fusionx.channels.collections.ObservableSortedArrayMap
import co.fusionx.channels.util.CharComparator
import co.fusionx.channels.util.UserComparator
import co.fusionx.channels.util.binarySearchKey

class ChannelVM(override val name: String) : ClientChildVM() {

    val userMap: ObservableSortedArrayMap<UserVM, Char> = ObservableSortedArrayMap(
            UserComparator.instance, CharComparator.instance)

    fun onMessage(nick: String, message: String) {
        val index = userMap.binarySearchKey { it.nick.compareTo(nick) }
        if (index < 0) {
            return
        }

        val user = userMap.getKeyAt(index)!!
        add("${user.nick}: $message")
    }

    fun onJoin(nick: String) {
        val userVM = UserVM(nick)
        userMap[userVM] = ' '

        add("${userVM.nick} joined the channel")
    }

    fun onName(nick: String, mode: List<Char>) {
        val userVM = UserVM(nick)
        userMap[userVM] = mode.getOrElse(0) { ' ' }
    }

    fun onNickChange(oldNick: String, newNick: String) {
        val index = userMap.binarySearchKey { oldNick.compareTo(it.nick) }
        if (index < 0) {
            return
        }
        val user = userMap.getKeyAt(index)!!
        user.nick = newNick

        add("$oldNick is now known as $newNick")
    }

    inner class UserVM(initialNick: String) : BaseObservable() {
        var nick: String = initialNick
            @Bindable get
    }
}