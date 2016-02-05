package co.fusionx.channels.viewmodel.helper

import co.fusionx.channels.databinding.ObservableSortedList
import co.fusionx.channels.util.compareTo
import co.fusionx.channels.viewmodel.persistent.ClientVM

class ClientComparator private constructor() : ObservableSortedList.HyperComparator<ClientVM> {
    override fun areItemsTheSame(item1: ClientVM, item2: ClientVM): Boolean {
        return item1.name == item2.name
    }

    override fun areContentsTheSame(oldItem: ClientVM, newItem: ClientVM): Boolean {
        return oldItem.name == newItem.name
    }

    override fun compare(o1: ClientVM, o2: ClientVM): Int {
        return o1.name.compareTo(o2.name)
    }

    companion object {
        val instance by lazy { ClientComparator() }
    }
}