package co.fusionx.channels.viewmodel.helper

import co.fusionx.channels.collections.ObservableSortedList
import co.fusionx.channels.configuration.Configuration

class ConfigurationComparator private constructor() : ObservableSortedList.HyperComparator<Configuration> {
    override fun areItemsTheSame(item1: Configuration, item2: Configuration): Boolean {
        return item1.connection.name == item2.connection.name
    }

    override fun areContentsTheSame(oldItem: Configuration, newItem: Configuration): Boolean {
        return oldItem.connection.name == newItem.connection.name
    }

    override fun compare(o1: Configuration, o2: Configuration): Int {
        val name = o1.connection.name
        val other = o2.connection.name
        return if (name == null || other == null) 0 else name.compareTo(other)
    }

    companion object {
        val instance by lazy { ConfigurationComparator() }
    }
}