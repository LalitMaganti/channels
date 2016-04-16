package com.tilal6991.channels.redux.util

import com.github.andrewoma.dexx.collection.*
import com.github.andrewoma.dexx.collection.Map

class TransactingMap<K : Any, V> private constructor(
        private val actual: Map<K, V>,
        val transactions: IndexedList<Transaction<K, V>>,
        private val maxSize: Int,
        private val transactionCount: Int) : Map<K, V> by actual {

    fun transactionCount(): Int {
        return transactionCount
    }

    fun maxSize(): Int {
        return maxSize
    }

    override fun put(key: K, value: V): TransactingMap<K, V> {
        val old = actual.get(key)
        val new = actual.put(key, value)

        val newT = if (old == null) transactions else maxAppend(transactions, Transaction(ADD, key, value), maxSize)
        return TransactingMap(new, newT, maxSize, transactionCount + 1)
    }

    override fun remove(key: K): TransactingMap<K, V> {
        val value = actual.get(key) ?: return this
        val new = actual.remove(key)
        val newT = maxAppend(transactions, Transaction(REMOVE, key, value), maxSize)
        return TransactingMap(new, newT, maxSize, transactionCount + 1)
    }

    override fun toString(): String {
        return actual.joinToString(limit = 10)
    }

    class TransactingBuilder<K : Any, V> internal constructor() : Builder<Pair<K, V>, TransactingMap<K, V>> {
        private var actualBuilder = Maps.builder<K, V>()
        private var maxSize = TransactingMap.DEFAULT_MAX_SIZE

        override fun add(element: Pair<K, V>?): Builder<Pair<K, V>, TransactingMap<K, V>> {
            actualBuilder = actualBuilder.add(element)
            return this
        }

        override fun addAll(elements: Traversable<Pair<K, V>>): Builder<Pair<K, V>, TransactingMap<K, V>> {
            actualBuilder = actualBuilder.addAll(elements)
            return this
        }

        override fun addAll(elements: MutableIterable<Pair<K, V>>): Builder<Pair<K, V>, TransactingMap<K, V>> {
            actualBuilder = actualBuilder.addAll(elements)
            return this
        }

        override fun addAll(iterator: MutableIterator<Pair<K, V>>): Builder<Pair<K, V>, TransactingMap<K, V>> {
            actualBuilder = actualBuilder.addAll(iterator)
            return this
        }

        override fun addAll(e1: Pair<K, V>?, e2: Pair<K, V>?, vararg es: Pair<K, V>?): Builder<Pair<K, V>, TransactingMap<K, V>> {
            actualBuilder = actualBuilder.addAll(e1, e2, *es)
            return this
        }

        fun maxSize(size: Int) {
            maxSize = size
        }

        override fun build(): TransactingMap<K, V> {
            return TransactingMap(actualBuilder.build(), Vector.empty(), maxSize, 0)
        }
    }

    class Transaction<K : Any, V>(val type: Int, val key: K, val value: V)

    companion object {
        const val ADD = 1
        const val REMOVE = 2
        const val DEFAULT_MAX_SIZE = 20

        fun <K : Any, V> builder(): TransactingMap.TransactingBuilder<K, V> {
            return TransactingMap.TransactingBuilder()
        }

        fun <K : Any, V> wrapping(map: Map<K, V>): TransactingMap<K, V> {
            return TransactingMap(map, Vector.empty(), DEFAULT_MAX_SIZE, 0)
        }

        private fun <K : Any, V> maxAppend(list: IndexedList<Transaction<K, V>>,
                                           elem: Transaction<K, V>,
                                           maxSize: Int): IndexedList<Transaction<K, V>> {
            if (list.size() >= maxSize) {
                val i = list.size() - maxSize + 1
                return list.drop(i).append(elem)
            }
            return list.append(elem)
        }
    }
}