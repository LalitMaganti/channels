package com.tilal6991.channels.redux.util

inline fun <T> SortedIndexedList<T>.transform(fn: (T) -> T): SortedIndexedList<T> {
    var list = this
    for (i in 0..size() - 1) {
        val old = list.get(i)
        val new = fn(old)
        if (old !== new) {
            list = list.set(i, new)
        }
    }
    return list
}