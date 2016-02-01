package co.fusionx.channels.databinding

import android.support.v7.util.SortedList
import java.util.*

class SortedListDispatcher<T>(private val comparator: Comparator<T>) : SortedList.Callback<T>() {

    private val callbacks: MutableList<Callback> = ArrayList()

    override fun onChanged(position: Int, count: Int) {
        for (c in callbacks) c.onChanged(position, count)
    }

    override fun onInserted(position: Int, count: Int) {
        for (c in callbacks) c.onInserted(position, count)
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        for (c in callbacks) c.onMoved(fromPosition, toPosition)
    }

    override fun onRemoved(position: Int, count: Int) {
        for (c in callbacks) c.onRemoved(position, count)
    }

    fun addCallback(callback: Callback) {
        callbacks.add(callback)
    }

    fun removeCallback(callback: Callback) {
        callbacks.remove(callback)
    }

    override fun areItemsTheSame(item1: T, item2: T): Boolean {
        return comparator.areItemsTheSame(item1, item2)
    }

    override fun compare(o1: T, o2: T): Int {
        return comparator.compare(o1, o2)
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return comparator.areContentsTheSame(oldItem, newItem)
    }

    public interface Callback {
        fun onChanged(position: Int, count: Int)
        fun onInserted(position: Int, count: Int)
        fun onMoved(fromPosition: Int, toPosition: Int)
        fun onRemoved(position: Int, count: Int)
    }

    public interface Comparator<T> {
        fun areItemsTheSame(item1: T, item2: T): Boolean
        fun compare(o1: T, o2: T): Int
        fun areContentsTheSame(oldItem: T, newItem: T): Boolean
    }
}
