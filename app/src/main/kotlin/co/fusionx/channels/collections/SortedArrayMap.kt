package co.fusionx.channels.collections

import timber.log.Timber
import java.util.*

open class SortedArrayMap<K, V> @JvmOverloads constructor(
        private val keyComparator: Comparator<K>,
        private val baseSize: Int = SortedArrayMap.BASE_SIZE) : AbstractMap<K, V>(), IndexedMap<K, V> {

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() {
            val set = HashSet<MutableMap.MutableEntry<K, V>>()
            for (i in 0..contentSize - 1) {
                set.add(Entry(keyArray[i]!!, valueArray[i]!!))
            }
            return set
        }

    private var keyArray: Array<K?>
    private var valueArray: Array<V?>
    private var contentSize: Int

    override val size: Int
        get() = contentSize

    init {
        contentSize = 0
        keyArray = arrayOfNulls<Any>(baseSize) as Array<K?>
        valueArray = arrayOfNulls<Any>(baseSize) as Array<V?>
    }

    override fun clear() {
        contentSize = 0
        keyArray = arrayOfNulls<Any>(baseSize) as Array<K?>
        valueArray = arrayOfNulls<Any>(baseSize) as Array<V?>
    }

    override fun put(key: K, value: V): V? {
        val index = indexOfRaw(key)
        if (index >= 0) {
            val old = valueArray[index]!!
            valueArray[index] = value
            return old
        }
        val insertionIndex = -index - 1
        putAtIndex(insertionIndex, key, value)
        return null
    }

    override fun putAll(from: Map<out K, V>) {
        var oldK: Array<K?> = keyArray
        var oldV: Array<V?> = valueArray

        if (contentSize + from.size > oldK.size) {
            keyArray = arrayOfNulls<Any>(from.size + oldK.size * 2) as Array<K?>
            valueArray = arrayOfNulls<Any>(from.size + oldV.size * 2) as Array<V?>

            System.arraycopy(oldK, 0, keyArray, 0, oldK.size)
            System.arraycopy(oldV, 0, valueArray, 0, oldV.size)
        }

        for ((k, v) in from) {
            put(k, v)
        }
    }

    override fun remove(key: K): V? {
        val index = indexOfRaw(key)
        return removeAtIndex(index)
    }

    override fun containsKey(key: K): Boolean {
        return indexOfRaw(key) >= 0
    }

    override fun containsValue(value: V): Boolean {
        for (v in valueArray) {
            if (value == v) {
                return true
            }
        }
        return false
    }

    override fun get(key: K): V? {
        val index = indexOfRaw(key)
        if (index < 0) {
            return null
        }
        return valueArray[index]
    }

    override fun isEmpty(): Boolean {
        return contentSize == 0
    }

    override fun getAtIndex(index: Int): V? {
        if (index < 0) {
            Timber.asTree().e(NegativeArraySizeException(), "Index cannot be negative.")
        }
        return if (index < contentSize) getAtIndexUnchecked(index) else null
    }

    override fun indexOf(key: K): Int {
        val index = indexOfRaw(key)
        return if (index < 0) ObservableIndexedMap.NO_POSITION else index
    }

    protected fun getAtIndexUnchecked(index: Int): V {
        return valueArray[index]!!
    }

    protected fun putAtIndex(index: Int, key: K, value: V) {
        var oldK: Array<K?> = keyArray
        var oldV: Array<V?> = valueArray
        if (contentSize == oldK.size) {
            keyArray = arrayOfNulls<Any>(oldK.size * 2) as Array<K?>
            valueArray = arrayOfNulls<Any>(oldV.size * 2) as Array<V?>

            if (index > 0) {
                System.arraycopy(oldK, 0, keyArray, 0, index)
                System.arraycopy(oldV, 0, valueArray, 0, index)
            }
        }
        if (index < contentSize) {
            System.arraycopy(oldK, index, keyArray, index + 1, contentSize - index)
            System.arraycopy(oldV, index, valueArray, index + 1, contentSize - index)
        }

        contentSize++
        keyArray[index] = key
        valueArray[index] = value
    }

    protected fun removeAtIndex(index: Int): V? {
        if (index < 0) {
            return null
        }
        val value = valueArray[index] ?: return null

        if (keyArray.size > baseSize * 2 && contentSize < keyArray.size / 3) {
            val n = if (contentSize > baseSize * 2) contentSize + (contentSize shr 1) else baseSize * 2

            var oldK: Array<K?> = keyArray
            var oldV: Array<V?> = valueArray

            keyArray = arrayOfNulls<Any>(n) as Array<K?>
            valueArray = arrayOfNulls<Any>(n) as Array<V?>

            if (index > 0) {
                System.arraycopy(oldK, 0, keyArray, 0, index)
                System.arraycopy(oldV, 0, valueArray, 0, index)
            }
            if (index < contentSize - 1) {
                System.arraycopy(oldK, index + 1, keyArray, index, contentSize - index - 1)
                System.arraycopy(oldV, index + 1, valueArray, index, contentSize - index - 1)
            }
        } else {
            if (index < contentSize - 1) {
                System.arraycopy(keyArray, index + 1, keyArray, index, contentSize - index - 1)
                System.arraycopy(valueArray, index + 1, valueArray, index, contentSize - index - 1)
            }
            keyArray[contentSize - 1] = null
            valueArray[contentSize - 1] = null
        }

        contentSize--
        return value
    }

    protected fun indexOfRaw(key: K): Int {
        return Arrays.binarySearch(keyArray, 0, contentSize, key, keyComparator as Comparator<in K?>)
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