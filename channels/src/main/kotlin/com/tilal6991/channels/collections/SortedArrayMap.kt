package com.tilal6991.channels.collections

import java.util.*

@Suppress("CAST_NEVER_SUCCEEDS")
open class SortedArrayMap<K, V> @JvmOverloads constructor(
        private val keyComparator: Comparator<K>,
        private val baseSize: Int = SortedArrayMap.BASE_SIZE) : AbstractMap<K, V>(), IndexedMap<K, V> {

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() {
            val set = HashSet<MutableMap.MutableEntry<K, V>>()
            for (i in 0..keyList.size - 1) {
                set.add(Entry(keyList[i]!!, valueList[i]!!))
            }
            return set
        }

    private val keyList: ArrayList<K>
    private val valueList: ArrayList<V>

    override val size: Int
        get() = keyList.size

    init {
        keyList = ArrayList(baseSize)
        valueList = ArrayList(baseSize)
    }

    override fun clear() {
        keys.clear()
        values.clear()
    }

    override fun put(key: K, value: V): V? {
        val index = indexOfRaw(key)
        if (index >= 0) {
            val old = valueList[index]!!
            valueList[index] = value
            return old
        }
        val insertionIndex = -index - 1
        putAtIndex(insertionIndex, key, value)
        return null
    }

    override fun putAll(from: Map<out K, V>) {
        val additional = Math.min(size, from.size) * 2
        keyList.ensureCapacity(size + from.size + additional)
        valueList.ensureCapacity(size + from.size + additional)

        for ((k, v) in from) {
            put(k, v)
        }
    }

    override fun remove(key: K): V? {
        val index = indexOfRaw(key)
        return removeAt(index)
    }

    override fun removeAt(index: Int): V? {
        if (index < 0) {
            return null
        }
        keyList.removeAt(index)
        return valueList.removeAt(index)
    }

    override fun containsKey(key: K): Boolean {
        return indexOfRaw(key) >= 0
    }

    override fun containsValue(value: V): Boolean {
        return valueList.contains(value)
    }

    override fun get(key: K): V? {
        val index = indexOfRaw(key)
        if (index < 0) {
            return null
        }
        return valueList[index]
    }

    override fun isEmpty(): Boolean {
        return keyList.isEmpty()
    }

    override fun getKeyAt(index: Int): K {
        return keyList[index]!!
    }

    override fun getValueAt(index: Int): V {
        return valueList[index]!!
    }

    override fun indexOf(key: K): Int {
        val index = indexOfRaw(key)
        return if (index < 0) IndexedMap.NO_POSITION else index
    }

    protected fun putAtIndex(index: Int, key: K, value: V) {
        keyList.add(index, key)
        valueList.add(index, value)
    }

    protected fun indexOfRaw(key: K): Int {
        return Collections.binarySearch(keyList, key, keyComparator as Comparator<in K?>)
    }

    class Entry<K, V>(override val key: K, override var value: V) : MutableMap.MutableEntry<K, V> {
        override fun setValue(newValue: V): V {
            val oldValue = value
            value = oldValue
            return oldValue
        }
    }

    companion object {
        const val BASE_SIZE = 10
    }
}