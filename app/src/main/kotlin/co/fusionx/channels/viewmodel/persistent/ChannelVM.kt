package co.fusionx.channels.viewmodel.persistent

import co.fusionx.channels.collections.ObservableSortedList
import co.fusionx.channels.viewmodel.helper.UserComparator

class ChannelVM(override val name: String) : ClientChildVM() {

    val users: ObservableSortedList<UserVM> = ObservableSortedList(
            UserVM::class.java, UserComparator.instance)

    fun onPrivmsg(userVM: UserVM, message: String) {
        add("${userVM.nick}: $message")
    }

    fun onJoin(userVM: UserVM) {
        users.add(userVM)

        add("${userVM.nick} joined the channel")
    }

    fun onNames(nickList: List<UserVM>) {
        users.addAll(nickList)
    }

    fun onNickChange(value: Int, oldNick: String, newNick: String) {
        users.recalculatePositionOfItemAt(value)

        add("$oldNick is now known as $newNick")
    }
}