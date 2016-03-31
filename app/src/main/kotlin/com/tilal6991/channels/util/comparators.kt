package com.tilal6991.channels.util

import com.tilal6991.channels.collections.ObservableSortedArrayMap
import com.tilal6991.channels.collections.ObservableSortedList
import com.tilal6991.channels.configuration.ChannelsConfiguration
import com.tilal6991.channels.viewmodel.ChannelVM
import java.util.*

class ChannelComparator private constructor() :
        ObservableSortedList.HyperComparator<ChannelVM>, ObservableSortedArrayMap.HyperComparator<ChannelVM> {
    override fun compare(p0: ChannelVM, p1: ChannelVM): Int {
        return p0.name.compareTo(p1.name)
    }

    override fun areItemsTheSame(item1: ChannelVM, item2: ChannelVM): Boolean {
        return item1.name == item2.name
    }

    override fun areContentsTheSame(oldItem: ChannelVM, newItem: ChannelVM): Boolean {
        return oldItem.name == newItem.name
    }

    companion object {
        val instance by lazy { ChannelComparator() }
    }
}

class UserComparator private constructor() : ObservableSortedArrayMap.HyperComparator<ChannelVM.UserVM>,
        ObservableSortedList.HyperComparator<ChannelVM.UserVM> {
    override fun areContentsTheSame(oldItem: ChannelVM.UserVM, newItem: ChannelVM.UserVM): Boolean {
        return oldItem.displayString == newItem.displayString
    }

    override fun areItemsTheSame(item1: ChannelVM.UserVM, item2: ChannelVM.UserVM): Boolean {
        return item1.displayString == item2.displayString
    }

    override fun compare(o1: ChannelVM.UserVM, o2: ChannelVM.UserVM): Int {
        return o1.displayString.compareTo(o2.displayString)
    }

    companion object {
        val instance by lazy { UserComparator() }
    }
}

class StringComparator private constructor() : Comparator<String> {
    override fun compare(lhs: String, rhs: String): Int {
        return lhs.compareTo(rhs)
    }

    companion object {
        val instance by lazy { StringComparator() }
    }
}

class UserListComparator private constructor() : ObservableSortedArrayMap.HyperComparator<ObservableSortedList<ChannelVM.UserVM>> {

    override fun areItemsTheSame(item1: ObservableSortedList<ChannelVM.UserVM>, item2: ObservableSortedList<ChannelVM.UserVM>): Boolean {
        return item1 === item2
    }

    override fun areContentsTheSame(oldItem: ObservableSortedList<ChannelVM.UserVM>, newItem: ObservableSortedList<ChannelVM.UserVM>): Boolean {
        return oldItem === newItem
    }

    companion object {
        val instance by lazy { UserListComparator() }
    }
}

class ConfigurationComparator private constructor() : ObservableSortedList.HyperComparator<ChannelsConfiguration> {
    override fun areItemsTheSame(item1: ChannelsConfiguration, item2: ChannelsConfiguration): Boolean {
        return item1.id == item2.id
    }

    override fun areContentsTheSame(oldItem: ChannelsConfiguration, newItem: ChannelsConfiguration): Boolean {
        return oldItem.name == newItem.name && oldItem.server == newItem.server && oldItem.user == newItem.user
    }

    override fun compare(o1: ChannelsConfiguration, o2: ChannelsConfiguration): Int {
        return o1.name.compareTo(o2.name)
    }

    companion object {
        val instance by lazy { ConfigurationComparator() }
    }
}

class CharComparator private constructor() : ObservableSortedArrayMap.HyperComparator<Char>, ObservableSortedList.HyperComparator<Char> {

    override fun areItemsTheSame(item1: Char, item2: Char): Boolean {
        return item1 == item2
    }

    override fun areContentsTheSame(oldItem: Char, newItem: Char): Boolean {
        return oldItem == newItem
    }

    override fun compare(lhs: Char, rhs: Char): Int {
        return lhs.compareTo(rhs)
    }

    companion object {
        val instance by lazy { CharComparator() }
    }
}