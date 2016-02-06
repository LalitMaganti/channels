package co.fusionx.channels.viewmodel.helper

import co.fusionx.channels.collections.ObservableSortedList
import co.fusionx.channels.viewmodel.persistent.UserVM

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