package com.tilal6991.channels.redux.util

import com.github.andrewoma.dexx.collection.IndexedLists
import com.github.andrewoma.dexx.collection.Map
import com.github.andrewoma.dexx.collection.Pair

fun <K, V> Map<K, V>.getKeyAt(index: Int): K? {
    return keys().getAt(index) ?: return null
}

fun <K, V> Map<K, V>.getValueAt(index: Int): V? {
    val key = getKeyAt(index) ?: return null
    return get(key)
}

fun <K, V> Map<K, V>.getAt(index: Int): Pair<K, V>? {
    val key = getKeyAt(index) ?: return null
    val value = get(key) ?: return null
    return Pair(key, value)
}

fun <K : Any, T : Comparable<T>> TransactingMap<K, TransactingIndexedList<T>>.putAddSorted(
        key: K, value: T): TransactingMap<K, TransactingIndexedList<T>> {
    val list = get(key)
    return if (list == null) {
        put(key, TransactingIndexedList.wrapping(IndexedLists.of(value)))
    } else {
        put(key, list.addSorted(value))
    }
}