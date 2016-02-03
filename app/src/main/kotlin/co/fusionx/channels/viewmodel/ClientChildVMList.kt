package co.fusionx.channels.viewmodel

import android.databinding.ListChangeRegistry
import android.databinding.ObservableList
import co.fusionx.channels.databinding.ObservableIndexedMap

public class ClientChildVMList(
        private val server: ServerVM,
        private val channels: ObservableIndexedMap<CharSequence, ChannelVM>) : ObservableList<ClientChildVM> {
    override val size: Int
        get() = 1 + channels.size

    private val registry: ListChangeRegistry

    init {
        registry = ListChangeRegistry()
        channels.addOnIndexedMapChangedCallback(object : ObservableIndexedMap.OnIndexedMapChangedCallback<ObservableIndexedMap<CharSequence, ChannelVM>, CharSequence, ChannelVM> {
            override fun onChanged(sender: ObservableIndexedMap<CharSequence, ChannelVM>) {
                registry.notifyChanged(this@ClientChildVMList)
            }

            override fun onItemChanged(sender: ObservableIndexedMap<CharSequence, ChannelVM>, position: Int, key: CharSequence, oldValue: ChannelVM, newValue: ChannelVM) {
                registry.notifyChanged(this@ClientChildVMList, position + 1, 1)
            }

            override fun onItemInserted(sender: ObservableIndexedMap<CharSequence, ChannelVM>, position: Int, key: CharSequence, value: ChannelVM) {
                registry.notifyInserted(this@ClientChildVMList, position + 1, 1)
            }

            override fun onItemMoved(sender: ObservableIndexedMap<CharSequence, ChannelVM>, fromPosition: Int, toPosition: Int, key: CharSequence, value: ChannelVM) {
                registry.notifyMoved(this@ClientChildVMList, fromPosition + 1, toPosition + 1, 1)
            }

            override fun onItemRemoved(sender: ObservableIndexedMap<CharSequence, ChannelVM>, position: Int, key: CharSequence, value: ChannelVM) {
                registry.notifyRemoved(this@ClientChildVMList, position + 1, 1)
            }
        })
    }

    override fun contains(element: ClientChildVM): Boolean {
        if (element is ServerVM) {
            return server.name == element.name
        } else if (element is ClientChildVM) {
            return channels.contains(element.name)
        }
        return false
    }

    override fun containsAll(elements: Collection<ClientChildVM>): Boolean {
        for (e in elements) {
            if (!contains(e)) {
                return false
            }
        }
        return true
    }

    override fun get(index: Int): ClientChildVM? {
        if (index < 0) {
            return null
        } else if (index < 1) {
            return server
        }
        var remainingIndex = index - 1
        if (remainingIndex < channels.size) {
            return channels.getAtIndex(remainingIndex)
        }
        return null
    }

    override fun indexOf(element: ClientChildVM): Int {
        if (element is ServerVM) {
            return if (server.name == element.name) 0 else -1
        } else if (element is ClientChildVM) {
            return channels.indexOf(element.name)
        }
        return -1
    }

    override fun isEmpty(): Boolean {
        return false
    }

    override fun lastIndexOf(element: ClientChildVM): Int {
        return indexOf(element)
    }

    override fun addOnListChangedCallback(callback: ObservableList.OnListChangedCallback<out ObservableList<ClientChildVM>>?) {
        registry.add(callback)
    }

    override fun removeOnListChangedCallback(callback: ObservableList.OnListChangedCallback<out ObservableList<ClientChildVM>>?) {
        registry.remove(callback)
    }

    /* All other operations are unsupported */
    override fun add(element: ClientChildVM): Boolean {
        throw UnsupportedOperationException()
    }

    override fun add(index: Int, element: ClientChildVM) {
        throw UnsupportedOperationException()
    }

    override fun addAll(elements: Collection<ClientChildVM>): Boolean {
        throw UnsupportedOperationException()
    }

    override fun addAll(index: Int, elements: Collection<ClientChildVM>): Boolean {
        throw UnsupportedOperationException()
    }

    override fun clear() {
        throw UnsupportedOperationException()
    }

    override fun listIterator(): MutableListIterator<ClientChildVM> {
        throw UnsupportedOperationException()
    }

    override fun listIterator(index: Int): MutableListIterator<ClientChildVM> {
        throw UnsupportedOperationException()
    }

    override fun remove(element: ClientChildVM): Boolean {
        throw UnsupportedOperationException()
    }

    override fun removeAll(elements: Collection<ClientChildVM>): Boolean {
        throw UnsupportedOperationException()
    }

    override fun removeAt(index: Int): ClientChildVM {
        throw UnsupportedOperationException()
    }

    override fun retainAll(elements: Collection<ClientChildVM>): Boolean {
        throw UnsupportedOperationException()
    }

    override fun set(index: Int, element: ClientChildVM): ClientChildVM {
        throw UnsupportedOperationException()
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<ClientChildVM> {
        throw UnsupportedOperationException()
    }

    override fun iterator(): MutableIterator<ClientChildVM> {
        throw UnsupportedOperationException()
    }
}