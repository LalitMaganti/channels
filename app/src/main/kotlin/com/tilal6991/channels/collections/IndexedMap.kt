package com.tilal6991.channels.collections

interface IndexedMap<K, V> : MutableMap<K, V> {
    fun getKeyAt(index: Int): K?
    fun getValueAt(index: Int): V?
    fun indexOf(key: K): Int

    companion object {
        const val NO_POSITION = -1
    }
}