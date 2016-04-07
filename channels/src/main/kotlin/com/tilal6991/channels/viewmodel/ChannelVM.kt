package com.tilal6991.channels.viewmodel

import android.databinding.BaseObservable
import android.databinding.Bindable
import com.tilal6991.channels.collections.CharSequenceTreeMap
import com.tilal6991.channels.collections.ObservableSortedArrayMap
import com.tilal6991.channels.collections.ObservableSortedList
import com.tilal6991.channels.util.UserComparator
import com.tilal6991.channels.util.UserListComparator
import com.tilal6991.channels.util.UserPrefixComparator
import com.tilal6991.channels.util.failAssert
import timber.log.Timber

class ChannelVM(override val name: String,
                private val comparator: UserPrefixComparator) : ClientChildVM() {

    val userMap = ObservableSortedArrayMap(comparator, UserListComparator.instance)
    private val treeMap = CharSequenceTreeMap<UserVM>()

    fun onMessage(nick: String, message: String) {
        val user = getUser(nick)
        add("${user?.handle ?: nick}: $message")
    }

    fun onJoin(nick: String, self: Boolean) {
        if (self) {
            active = true

            treeMap.clear()
            userMap.clear()
        }

        val user = UserVM(nick, null)
        addUser(nick, user)
        add("${user.handle} joined the channel")
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
        // If user is null then they simply mush not have been
        // in this channel.
        val user = getUser(oldNick) ?: return

        changeUserNick(user, newNick)
        add("$oldNick is now known as $newNick")
    }

    fun onPart(nick: String, self: Boolean) {
        if (self) {
            active = false
        }
        // If the user is not preset then something has gone
        // wrong and we need to fail.
        removeUser(nick) ?: return Timber.asTree().failAssert()

        add("$nick has parted from the channel")
    }

    fun onQuit(nick: String, self: Boolean, message: String?) {
        if (self) {
            active = false
        }

        // User being null means they are not in this channel and we
        // don't need to do anything.
        removeUser(nick) ?: return
        val suffix: String
        if (message == null) {
            suffix = ""
        } else {
            suffix = " ($message)"
        }
        add("$nick has quit the server$suffix")
    }

    private fun addUser(nick: String, user: UserVM) {
        val oldUser = treeMap.put(nick, user)
        if (oldUser != null) {
            // We should not be inserting the same nick twice.
            Timber.asTree().e(IllegalArgumentException(), "Inserted same nick twice")
        }
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

    private fun removeUser(nick: String): UserVM? {
        val user = treeMap.remove(nick) ?: return null
        removeUserFromModeMap(user)
        return user
    }

    private fun getUser(nick: String): UserVM? {
        return treeMap[nick]
    }

    inner class UserVM(initialNick: String, initialMode: Char?) : BaseObservable() {

        var handle: String
            @Bindable get
            private set

        var mode: Char?
            @Bindable get
            private set

        init {
            handle = if (initialMode == null) initialNick else initialMode + initialNick
            mode = initialMode
        }

        fun onNickUpdate(nick: String) {
            handle = if (mode == null) nick else mode!! + nick
        }

        fun onUpdateMode(m: Char?) {
            val nick = if (mode == null) handle else handle.substring(1)
            handle = if (m == null) handle else m + nick
            mode = m
        }
    }
}