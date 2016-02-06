package co.fusionx.channels.util

import timber.log.Timber
import java.util.*

val charSequenceComparator by lazy {
    Comparator<CharSequence> { lhs, rhs -> lhs.compareTo(rhs) }
}

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

fun <T> MutableCollection<T>.addAll(vararg data: T) = addAll(data)

fun Timber.Tree.failAssert() = e(IllegalArgumentException(), "This is a bug.")