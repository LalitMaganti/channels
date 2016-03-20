package co.fusionx.channels.viewmodel.helper

import co.fusionx.channels.collections.ObservableSortedList
import co.fusionx.channels.configuration.ChannelsConfiguration

class ConfigurationComparator private constructor() : ObservableSortedList.HyperComparator<ChannelsConfiguration> {
    override fun areItemsTheSame(item1: ChannelsConfiguration, item2: ChannelsConfiguration): Boolean {
        return item1.name == item2.name
    }

    override fun areContentsTheSame(oldItem: ChannelsConfiguration, newItem: ChannelsConfiguration): Boolean {
        return oldItem.name == newItem.name
    }

    override fun compare(o1: ChannelsConfiguration, o2: ChannelsConfiguration): Int {
        return o1.name.compareTo(o2.name)
    }

    companion object {
        val instance by lazy { ConfigurationComparator() }
    }
}