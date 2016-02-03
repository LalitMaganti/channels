package co.fusionx.channels.util

import java.util.*

public val charSequenceComparator by lazy {
    Comparator<CharSequence?> { lhs, rhs -> lhs.compareTo(rhs) }
}

public fun CharSequence?.compareTo(other: CharSequence?): Int {
    if (this == null && other == null) {
        return 0
    } else if (this == null) {
        return 1
    } else if (other == null) {
        return -1
    }

    for (i in 0..length - 1) {
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

public fun <T> MutableCollection<T>.addAll(vararg data: T) = addAll(data)