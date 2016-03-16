package co.fusionx.channels.viewmodel.helper

import co.fusionx.channels.collections.ObservableSortedList
import co.fusionx.channels.relay.Configuration
import co.fusionx.channels.util.compareTo
import co.fusionx.channels.viewmodel.persistent.ClientVM

class ConfigurationComparator private constructor() : ObservableSortedList.HyperComparator<Configuration> {
    override fun areItemsTheSame(item1: Configuration, item2: Configuration): Boolean {
        return item1.name == item2.name
    }

    override fun areContentsTheSame(oldItem: Configuration, newItem: Configuration): Boolean {
        return oldItem.name == newItem.name
    }

    override fun compare(o1: Configuration, o2: Configuration): Int {
        return o1.name.compareTo(o2.name)
    }

    companion object {
        val instance by lazy { ConfigurationComparator() }
    }
}