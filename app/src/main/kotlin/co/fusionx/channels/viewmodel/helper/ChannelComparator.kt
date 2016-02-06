package co.fusionx.channels.viewmodel.helper

import co.fusionx.channels.collections.ObservableSortedArrayMap
import co.fusionx.channels.collections.ObservableSortedList
import co.fusionx.channels.viewmodel.persistent.ChannelVM

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