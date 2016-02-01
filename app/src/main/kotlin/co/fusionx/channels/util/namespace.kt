package co.fusionx.channels.util

public fun CharSequence.compareTo(other: CharSequence): Int {
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