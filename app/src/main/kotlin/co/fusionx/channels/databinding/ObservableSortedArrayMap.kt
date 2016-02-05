package co.fusionx.channels.databinding

import android.databinding.ListChangeRegistry
import android.databinding.ObservableList
import java.lang.ref.WeakReference
import java.util.*

@Suppress("CAST_NEVER_SUCCEEDS")
public class ObservableSortedArrayMap<K, V>(
        private val keyComparator: Comparator<K?>,
        private val valueHyperComparator: HyperComparator<V>) : ObservableIndexedMap<K, V> {
    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = throw UnsupportedOperationException()
    override val keys: MutableSet<K>
        get() = throw UnsupportedOperationException()
    override val values: MutableCollection<V>
        get() = throw UnsupportedOperationException()

    override val size: Int
        get() = contentSize

    private lateinit var keyArray: Array<K?>
    private lateinit var valueArray: Array<V?>

    private var contentSize: Int

    private @Transient var registry: IndexedMapChangeRegistry<ObservableSortedArrayMap<K, V>, K, V>? = null

    private var valuesObservableList: ValuesObservableList? = null

    init {
        contentSize = 0
        keyArray = arrayOfNulls<Any>(BASE_SIZE) as Array<K?>
        valueArray = arrayOfNulls<Any>(BASE_SIZE) as Array<V?>
    }

    override fun clear() {
        contentSize = 0
        keyArray = arrayOfNulls<Any>(BASE_SIZE) as Array<K?>
        valueArray = arrayOfNulls<Any>(BASE_SIZE) as Array<V?>

        registry?.notifyChange(this)
    }

    override fun put(key: K, value: V): V? {
        val index = indexOf(key)
        if (index >= 0) {
            val old = valueArray[index]!!
            valueArray[index] = value

            // TODO(tilal6991) - this might need a resort. Investigate how to fix.
            if (valueHyperComparator.areItemsTheSame(old, value)) {
                if (!valueHyperComparator.areContentsTheSame(old, value)) {
                    registry?.notifyItemChanged(this, index, key, old, value)
                }
            } else {
                registry?.notifyItemChanged(this, index, key, old, value)
            }
            return old
        }

        var oldK: Array<K?> = keyArray
        var oldV: Array<V?> = valueArray
        val insertionIndex = -index - 1
        if (contentSize == oldK.size) {
            keyArray = arrayOfNulls<Any>(oldK.size * 2) as Array<K?>
            valueArray = arrayOfNulls<Any>(oldV.size * 2) as Array<V?>

            if (insertionIndex > 0) {
                System.arraycopy(oldK, 0, keyArray, 0, insertionIndex)
                System.arraycopy(oldV, 0, valueArray, 0, insertionIndex)
            }
        }
        if (insertionIndex < contentSize) {
            System.arraycopy(oldK, insertionIndex, keyArray, insertionIndex + 1, contentSize - insertionIndex)
            System.arraycopy(oldV, insertionIndex, valueArray, insertionIndex + 1, contentSize - insertionIndex)
        }

        contentSize++
        keyArray[insertionIndex] = key
        valueArray[insertionIndex] = value
        registry?.notifyItemInserted(this, insertionIndex, key, value)

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
        val index = indexOf(key)
        if (index < 0) {
            return null
        }
        val value = valueArray[index] ?: return null

        if (keyArray.size > BASE_SIZE * 2 && contentSize < keyArray.size / 3) {
            val n = if (contentSize > BASE_SIZE * 2) contentSize + (contentSize shr 1) else BASE_SIZE * 2

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
        registry?.notifyItemRemoved(this, index, key, value)

        return value
    }

    override fun containsKey(key: K): Boolean {
        return indexOf(key) >= 0
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
        val index = indexOf(key)
        if (index < 0) {
            return null
        }
        return valueArray[index]
    }

    override fun isEmpty(): Boolean {
        return contentSize == 0
    }

    override fun getAtIndex(index: Int): V? {
        return if (index < size) valueArray[index] else null
    }

    override fun indexOf(key: K): Int {
        val index = Arrays.binarySearch(keyArray, key, keyComparator)
        return if (index < 0) ObservableIndexedMap.NO_POSITION else index
    }

    override fun valuesAsObservableList(): ObservableList<V> {
        if (valuesObservableList == null) {
            valuesObservableList = ValuesObservableList()
        }
        return valuesObservableList!!
    }

    override fun addOnIndexedMapChangedCallback(
            callback: ObservableIndexedMap.OnIndexedMapChangedCallback<out ObservableIndexedMap<K, V>, K, V>) {
        if (registry == null) {
            registry = IndexedMapChangeRegistry()
        }
        registry!!.add(callback as ObservableIndexedMap.OnIndexedMapChangedCallback<ObservableSortedArrayMap<K, V>, K, V>)
    }

    override fun removeOnIndexedMapChangedCallback(
            callback: ObservableIndexedMap.OnIndexedMapChangedCallback<out ObservableIndexedMap<K, V>, K, V>) {
        if (registry == null) {
            registry = IndexedMapChangeRegistry()
        }
        registry!!.remove(callback as ObservableIndexedMap.OnIndexedMapChangedCallback<ObservableSortedArrayMap<K, V>, K, V>)
    }

    public inner class ValuesObservableList : ObservableList<V>, AbstractList<V>(), ObservableIndexedMap.OnIndexedMapChangedCallback<ObservableSortedArrayMap<K, V>, K, V> {
        private val registry = ListChangeRegistry()

        override val size: Int
            get() = this@ObservableSortedArrayMap.size

        init {
            addOnIndexedMapChangedCallback(this)
        }

        override fun get(index: Int): V? {
            return getAtIndex(index)
        }

        override fun onChanged(sender: ObservableSortedArrayMap<K, V>) {
            registry.notifyChanged(this)
        }

        override fun onItemChanged(sender: ObservableSortedArrayMap<K, V>, position: Int, key: K, oldValue: V, newValue: V) {
            registry.notifyChanged(this, position, 1)
        }

        override fun onItemInserted(sender: ObservableSortedArrayMap<K, V>, position: Int, key: K, value: V) {
            registry.notifyInserted(this, position, 1)
        }

        override fun onItemMoved(sender: ObservableSortedArrayMap<K, V>, fromPosition: Int, toPosition: Int, key: K, value: V) {
            registry.notifyMoved(this, fromPosition, toPosition, 1)
        }

        override fun onItemRemoved(sender: ObservableSortedArrayMap<K, V>, position: Int, key: K, value: V) {
            registry.notifyRemoved(this, position, 1)
        }

        override fun addOnListChangedCallback(callback: ObservableList.OnListChangedCallback<out ObservableList<V>>?) {
            registry.add(callback)
        }

        override fun removeOnListChangedCallback(callback: ObservableList.OnListChangedCallback<out ObservableList<V>>?) {
            registry.remove(callback)
        }
    }

    companion object {
        public const val BASE_SIZE = 10
    }

    public interface HyperComparator<T> {
        fun areItemsTheSame(item1: T, item2: T): Boolean
        fun areContentsTheSame(oldItem: T, newItem: T): Boolean
    }
}