package com.tilal6991.channels.redux.util

import com.github.andrewoma.dexx.collection.IndexedList
import com.github.andrewoma.dexx.collection.IndexedLists
import com.github.andrewoma.dexx.collection.List
import com.github.andrewoma.dexx.collection.Traversable
import com.github.andrewoma.dexx.collection.internal.base.AbstractIndexedList
import java.util.*

class SortedIndexedList<T>(private val list: IndexedList<T>,
                           private val comparator: Comparator<T>? = null) : AbstractIndexedList<T>(), IndexedList<T> {

    override fun size(): Int {
        return list.size()
    }

    override fun indexOf(elem: T): Int {
        val index = binarySearch(0, size(), elem)
        if (index < 0) {
            return -1
        }
        for (i in index - 1 downTo 0) {
            if (get(i) != elem) {
                return i + 1
            }
        }
        return 0
    }

    override fun lastIndexOf(elem: T): Int {
        val index = binarySearch(0, size(), elem)
        if (index < 0) {
            return -1
        }
        val lastIndex = size() - 1
        for (i in index + 1..lastIndex) {
            if (get(i) != elem) {
                return i - 1
            }
        }
        return lastIndex
    }

    override fun set(i: Int, elem: T): SortedIndexedList<T> {
        return SortedIndexedList(list.set(i, elem), comparator)
    }

    override fun drop(number: Int): SortedIndexedList<T> {
        return SortedIndexedList(list.drop(number), comparator)
    }

    override fun take(number: Int): SortedIndexedList<T> {
        return SortedIndexedList(list.take(number), comparator)
    }

    override fun append(elem: T): SortedIndexedList<T> {
        val index = binarySearch(0, size(), elem)
        val elementsBefore = if (index >= 0) index + 1 else index
        return SortedIndexedList(IndexedLists.builder<T>()
                .addAll(list.take(elementsBefore) as Traversable<T>)
                .add(elem)
                .addAll(list.drop(elementsBefore) as Traversable<T>)
                .build(), comparator)
    }

    override fun range(from: Int, fromInclusive: Boolean, to: Int, toInclusive: Boolean): SortedIndexedList<T> {
        return SortedIndexedList(
                list.range(from, fromInclusive, to, toInclusive),
                comparator
        )
    }

    override fun prepend(elem: T): SortedIndexedList<T> {
        return append(elem)
    }

    override fun get(i: Int): T {
        return list.get(i)
    }

    override fun first(): T? {
        return list.first()
    }

    override fun last(): T? {
        return list.last()
    }

    override fun tail(): List<T> {
        return list.tail()
    }

    override fun iterator(): MutableIterator<T> {
        return list.iterator()
    }

    inline fun <U : Comparable<U>> binarySearch(elem: U, selector: (T) -> U): Int {
        var low = 0
        var high = size() - 1

        while (low <= high) {
            val mid = (low + high).ushr(1) // safe from overflows
            val midVal = get(mid)
            val cmp = selector(midVal).compareTo(elem)

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

    private fun binarySearch(fromIndex: Int, toIndex: Int, elem: T): Int {
        var low = fromIndex
        var high = toIndex - 1

        while (low <= high) {
            val mid = (low + high).ushr(1) // safe from overflows
            val midVal = get(mid)
            val cmp: Int
            if (comparator != null) {
                cmp = comparator.compare(midVal, elem)
            } else {
                cmp = (midVal as Comparable<T>).compareTo(elem)
            }

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
}