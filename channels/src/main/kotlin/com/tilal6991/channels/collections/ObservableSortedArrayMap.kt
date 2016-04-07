package com.tilal6991.channels.collections

import android.databinding.ListChangeRegistry
import android.databinding.ObservableList
import java.util.*

@Suppress("CAST_NEVER_SUCCEEDS") class ObservableSortedArrayMap<K, V>(
        private val keyComparator: Comparator<K>,
        private val comparator: HyperComparator<V>) : SortedArrayMap<K, V>(keyComparator),
        ObservableIndexedMap<K, V> {

    override val valuesList by lazy { ValuesObservableList() }

    private var registry: IndexedMapChangeRegistry<ObservableSortedArrayMap<K, V>, K, V>? = null

    override fun clear() {
        super.clear()

        registry?.notifyChange(this)
    }

    override fun put(key: K, value: V): V? {
        val index = indexOfRaw(key)
        if (index >= 0) {
            val old = getValueAt(index)
            // TODO(tilal6991) - this might need a resort. Investigate how to fix.
            if (comparator.areItemsTheSame(old, value)) {
                if (!comparator.areContentsTheSame(old, value)) {
                    registry?.notifyItemChanged(this, index, key, old, value)
                }
            } else {
                registry?.notifyItemChanged(this, index, key, old, value)
            }
            return old
        }

        val insertionIndex = -index - 1
        putAtIndex(insertionIndex, key, value)

        registry?.notifyItemInserted(this, insertionIndex, key, value)

        return null
    }

    override fun remove(key: K): V? {
        val index = indexOfRaw(key)
        val value = removeAtIndex(index) ?: return null
        registry?.notifyItemRemoved(this, index, key, value)
        return value
    }

    @Suppress("UNCHECKED_CAST")
    override fun addOnIndexedMapChangedCallback(
            callback: ObservableIndexedMap.OnIndexedMapChangedCallback<out ObservableIndexedMap<K, V>, K, V>) {
        if (registry == null) {
            registry = IndexedMapChangeRegistry()
        }
        registry!!.add(callback as ObservableIndexedMap.OnIndexedMapChangedCallback<ObservableSortedArrayMap<K, V>, K, V>)
    }

    @Suppress("UNCHECKED_CAST")
    override fun removeOnIndexedMapChangedCallback(
            callback: ObservableIndexedMap.OnIndexedMapChangedCallback<out ObservableIndexedMap<K, V>, K, V>) {
        if (registry == null) {
            registry = IndexedMapChangeRegistry()
        }
        registry!!.remove(callback as ObservableIndexedMap.OnIndexedMapChangedCallback<ObservableSortedArrayMap<K, V>, K, V>)
    }

    inner class ValuesObservableList : ObservableList<V>, AbstractList<V>(),
            ObservableIndexedMap.OnIndexedMapChangedCallback<ObservableSortedArrayMap<K, V>, K, V> {
        private val registry = ListChangeRegistry()

        override val size: Int
            get() = this@ObservableSortedArrayMap.size

        init {
            addOnIndexedMapChangedCallback(this)
        }

        override fun get(index: Int): V? {
            return getValueAt(index)
        }

        override fun onChanged(sender: ObservableSortedArrayMap<K, V>) {
            registry.notifyChanged(this)
        }

        override fun onItemChanged(sender: ObservableSortedArrayMap<K, V>, position: Int, key: K, oldValue: V, newValue: V) {
            registry.notifyChanged(this, position, 1)
        }

        override fun onItemInserted(sender: ObservableSortedArrayMap<K, V>, position: Int, key: K, value: V) {
            registry.notifyInserted(this, position, 1)
        }

        override fun onItemMoved(sender: ObservableSortedArrayMap<K, V>, fromPosition: Int, toPosition: Int, key: K, value: V) {
            registry.notifyMoved(this, fromPosition, toPosition, 1)
        }

        override fun onItemRemoved(sender: ObservableSortedArrayMap<K, V>, position: Int, key: K, value: V) {
            registry.notifyRemoved(this, position, 1)
        }

        override fun addOnListChangedCallback(callback: ObservableList.OnListChangedCallback<out ObservableList<V>>?) {
            registry.add(callback)
        }

        override fun removeOnListChangedCallback(callback: ObservableList.OnListChangedCallback<out ObservableList<V>>?) {
            registry.remove(callback)
        }
    }

    interface HyperComparator<T> {
        fun areItemsTheSame(item1: T, item2: T): Boolean
        fun areContentsTheSame(oldItem: T, newItem: T): Boolean
    }
}