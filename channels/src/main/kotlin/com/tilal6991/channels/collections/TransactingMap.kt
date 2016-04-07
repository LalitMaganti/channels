package com.tilal6991.channels.collections

import android.support.v4.util.Pools
import java.util.concurrent.ConcurrentLinkedQueue

class TransactingMap<K, V>(private val wrapped: MutableMap<K, V>) : MutableMap<K, V> {

    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = throw UnsupportedOperationException()
    override val keys: MutableSet<K>
        get() = throw UnsupportedOperationException()
    override val values: MutableCollection<V>
        get() = throw UnsupportedOperationException()

    private val queue: ConcurrentLinkedQueue<Transaction<K, V>>
    private val pool = TransactionPool(100) as Pools.SimplePool<Transaction<K, V>>

    init {
        queue = ConcurrentLinkedQueue()
    }

    override fun clear() {
        queue.add(pool.acquire().initialise(CLEAR))
    }

    override fun put(key: K, value: V): V? {
        queue.add(pool.acquire().initialise(PUT, key, value))
        return null
    }

    override fun putAll(from: Map<out K, V>) {
        for ((k, v) in from) {
            queue.add(pool.acquire().initialise(PUT, k, v))
        }
    }

    override fun remove(key: K): V? {
        queue.add(pool.acquire().initialise(REMOVE, key))
        return null
    }

    override val size: Int
        get() = throw UnsupportedOperationException()

    override fun containsKey(key: K): Boolean {
        throw UnsupportedOperationException()
    }

    override fun containsValue(value: V): Boolean {
        throw UnsupportedOperationException()
    }

    override fun get(key: K): V? {
        throw UnsupportedOperationException()
    }

    override fun isEmpty(): Boolean {
        throw UnsupportedOperationException()
    }

    private class Transaction<K, V> {

        var type: Int = -1
        var key: K? = null
        var value: V? = null

        fun initialise(type: Int, key: K? = null, value: V? = null): Transaction<K, V> {
            this.type = type
            this.key = key
            this.value = value
            return this
        }

        fun reset(): Transaction<K, V> {
            type = -1
            key = null
            value = null
            return this
        }
    }

    private class TransactionPool(private val maxPoolSize: Int) : Pools.SimplePool<Transaction<Any, Any>>(maxPoolSize) {
        override fun acquire(): Transaction<Any, Any> {
            return super.acquire() ?: TransactingMap.Transaction()
        }

        override fun release(instance: Transaction<Any, Any>?): Boolean {
            return instance == null || super.release(instance.reset())
        }
    }

    companion object {
        private const val CLEAR = 0
        private const val PUT = 1
        private const val REMOVE = 2
    }
}