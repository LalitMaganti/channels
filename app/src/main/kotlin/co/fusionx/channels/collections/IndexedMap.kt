package co.fusionx.channels.collections

interface IndexedMap<K, V> : MutableMap<K, V> {
    fun getAtIndex(index: Int): V?
    fun indexOf(key: K): Int

    companion object {
        const val NO_POSITION = -1
    }
}