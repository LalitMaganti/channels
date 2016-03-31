package com.tilal6991.channels.viewmodel

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.tilal6991.channels.collections.CharSequenceTreeMap
import com.tilal6991.channels.collections.ObservableSortedArrayMap
import com.tilal6991.channels.collections.ObservableSortedList
import com.tilal6991.channels.util.CharComparator
import com.tilal6991.channels.util.UserComparator
import com.tilal6991.channels.util.UserListComparator
import com.tilal6991.channels.util.failAssert
import timber.log.Timber

class ChannelVM(override val name: String) : ClientChildVM() {

    val treeMap: CharSequenceTreeMap<UserVM> = CharSequenceTreeMap()
    val userMap: ObservableSortedArrayMap<Char, ObservableSortedList<UserVM>> = ObservableSortedArrayMap(
            CharComparator.instance, UserListComparator.instance)

    fun onMessage(nick: String, message: String) {
        val user = getUserOrFail(nick) ?: return
        add("${user.displayString}: $message")
    }

    fun onJoin(nick: String, self: Boolean) {
        if (self) {
            active = true
            treeMap.clear()
            userMap.clear()
        }

        val user = UserVM(nick, null)
        addUser(nick, user)
        add("${user.displayString} joined the channel")
    }

    fun onName(nick: String, mode: List<Char>) {
        val user = treeMap[nick]
        if (user == null) {
            addUser(nick, UserVM(nick, mode.getOrNull(0)))
        } else {
            onUpdateUserMode(user, mode.getOrNull(0))
        }
    }

    fun onNickChange(oldNick: String, newNick: String) {
        val user = getUserOrFail(oldNick) ?: return
        changeUserNick(user, newNick)
        add("$oldNick is now known as $newNick")
    }

    fun onPart(nick: String, self: Boolean) {
        if (self) {
            active = false
        }
        removeUser(nick)
    }

    private fun addUser(nick: String, user: UserVM) {
        treeMap.put(nick, user)
        addUserToModeMap(user)
    }

    private fun addUserToModeMap(user: UserVM) {
        val mode = user.mode ?: ' '
        var list = userMap[mode]
        if (list == null) {
            list = ObservableSortedList(UserVM::class.java, UserComparator.instance)
            userMap[mode] = list
        }
        list.add(user)
    }

    private fun changeUserNick(user: UserVM, newNick: String) {
        val mode = user.mode ?: ' '
        val list = userMap[mode]
        if (list == null) {
            Timber.asTree().failAssert()
        } else {
            list.indexOf(user)
        }
        user.onNickUpdate(newNick)
    }

    private fun removeUserFromModeMap(user: UserVM) {
        val mode = user.mode ?: ' '
        val list = userMap[mode] ?: return Timber.asTree().failAssert()
        list.remove(user)
        if (list.isEmpty()) {
            userMap.remove(mode)
        }
    }

    private fun onUpdateUserMode(user: UserVM, newMode: Char?) {
        removeUserFromModeMap(user)
        user.onUpdateMode(newMode)
        addUserToModeMap(user)
    }

    private fun removeUser(nick: String) {
        val user = treeMap.remove(nick) ?: return Timber.asTree().failAssert()
        removeUserFromModeMap(user)
    }

    private fun getUserOrFail(nick: String): UserVM? {
        val userVM = treeMap[nick]
        if (userVM == null) {
            Timber.asTree().failAssert()
        }
        return userVM
    }

    inner class UserVM(initialNick: String, initialMode: Char?) : BaseObservable() {

        var displayString: String
            @Bindable get
            private set

        var mode: Char?
            @Bindable get
            private set

        init {
            displayString = if (initialMode == null) initialNick else initialMode + initialNick
            mode = initialMode
        }

        fun onNickUpdate(nick: String) {
            displayString = if (mode == null) nick else mode!! + nick
        }

        fun onUpdateMode(m: Char?) {
            val nick = if (mode == null) displayString else displayString.substring(1)
            displayString = if (m == null) displayString else m + nick
            mode = m
        }
    }
}