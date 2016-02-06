package co.fusionx.channels.collections

import android.databinding.ListChangeRegistry
import android.databinding.ObservableList
import android.support.v7.util.SortedList
import java.util.*

open class ObservableSortedList<T>(
        private val klass: Class<T>,
        private val comparator: HyperComparator<T>) : AbstractList<T>(), RandomAccess, ObservableList<T> {
    override val size: Int
        get() = wrapped.size()

    private var registry: ListChangeRegistry? = null

    private val wrapped: SortedList<T>

    init {
        wrapped = SortedList(klass, CallbackWrapper())
    }

    fun beginBatchedUpdates() {
        wrapped.beginBatchedUpdates()
    }

    fun endBatchedUpdates() {
        wrapped.endBatchedUpdates()
    }

    fun addAndGetIndex(item: T): Int {
        return wrapped.add(item)
    }

    fun updateItemAt(index: Int, item: T) {
        wrapped.updateItemAt(index, item)
    }

    fun recalculatePositionOfItemAt(index: Int) {
        wrapped.recalculatePositionOfItemAt(index)
    }

    override fun clear() {
        wrapped.clear()
    }

    override fun removeAt(index: Int): T {
        return wrapped.removeItemAt(index)
    }

    override fun get(index: Int): T {
        return wrapped.get(index)
    }

    override fun remove(element: T): Boolean {
        return wrapped.remove(element)
    }

    override fun add(element: T): Boolean {
        return addAndGetIndex(element) != SortedList.INVALID_POSITION
    }

    override fun set(index: Int, element: T): T {
        val item = get(index)
        wrapped.updateItemAt(index, element)
        return item
    }

    override fun indexOf(element: T): Int {
        return wrapped.indexOf(element)
    }

    override fun contains(element: T): Boolean {
        return indexOf(element) != SortedList.INVALID_POSITION
    }

    override fun addOnListChangedCallback(callback: ObservableList.OnListChangedCallback<out ObservableList<T>>?) {
        if (registry == null) {
            registry = ListChangeRegistry()
        }
        registry!!.add(callback)
    }

    override fun removeOnListChangedCallback(callback: ObservableList.OnListChangedCallback<out ObservableList<T>>?) {
        if (registry == null) {
            registry = ListChangeRegistry()
        }
        registry!!.remove(callback)
    }

    private inner class CallbackWrapper : SortedList.Callback<T>() {
        override fun compare(o1: T, o2: T): Int {
            return comparator.compare(o1, o2)
        }

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            return comparator.areContentsTheSame(oldItem, newItem)
        }

        override fun areItemsTheSame(item1: T, item2: T): Boolean {
            return comparator.areItemsTheSame(item1, item2)
        }

        override fun onRemoved(position: Int, count: Int) {
            if (registry == null) return;
            registry!!.notifyRemoved(this@ObservableSortedList, position, count)
        }

        override fun onChanged(position: Int, count: Int) {
            if (registry == null) return;
            registry!!.notifyChanged(this@ObservableSortedList, position, count)
        }

        override fun onInserted(position: Int, count: Int) {
            if (registry == null) return;
            registry!!.notifyInserted(this@ObservableSortedList, position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            if (registry == null) return;
            registry!!.notifyMoved(this@ObservableSortedList, fromPosition, toPosition, 1)
        }
    }

    interface HyperComparator<T> : Comparator<T> {
        fun areItemsTheSame(item1: T, item2: T): Boolean
        fun areContentsTheSame(oldItem: T, newItem: T): Boolean
    }
}