package com.tilal6991.channels.util

import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.text.TextUtils
import com.tilal6991.channels.collections.IndexedMap
import timber.log.Timber

fun CharSequence.compareTo(other: CharSequence): Int {
    val count = Math.min(length, other.length)
    for (i in 0..count - 1) {
        val a = this[i]
        val b = other[i]
        if (a < b) {
            return -1
        } else if (a > b) {
            return 1
        }
    }
    return length - other.length
}

inline fun <K, V> IndexedMap<K, V>.binarySearchKey(comparison: (K) -> Int): Int {
    var low = 0
    var high = size - 1

    while (low <= high) {
        val mid = (low + high).ushr(1)
        val midVal = getKeyAt(mid)!!
        val cmp = comparison(midVal)

        if (cmp < 0) {
            low = mid + 1
        } else if (cmp > 0) {
            high = mid - 1
        } else {
            return mid
        }
    }
    return -(low + 1)
}

fun <T> MutableCollection<T>.addAll(vararg data: T) = addAll(data)

fun Timber.Tree.failAssert() = e(IllegalArgumentException(), "This is a bug.")

fun ObservableField<out CharSequence?>.isNotEmpty(): Boolean {
    return !isEmpty()
}

fun ObservableField<out CharSequence?>.isEmpty(): Boolean {
    return TextUtils.isEmpty(get())
}

fun CharSequence.isValidPort(): Boolean {
    if (isEmpty()) {
        return false
    }

    try {
        val int = Integer.parseInt(toString())
        return int.isValidPort()
    } catch (ex: NumberFormatException) {
        return false
    }
}

fun ObservableInt.isValidPort(): Boolean {
    return get().isValidPort()
}

fun Int.isValidPort(): Boolean {
    return this > 0 && this <= 65536
}