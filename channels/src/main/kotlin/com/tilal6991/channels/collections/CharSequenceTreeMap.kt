package com.tilal6991.channels.collections

import android.support.v4.util.Pools
import com.tilal6991.channels.util.CharComparator

class CharSequenceTreeMap<V : Any> : IndexedMap<CharSequence, V> {

    override val size: Int
        get() = root.count
    override val entries: MutableSet<MutableMap.MutableEntry<CharSequence, V>>
        get() = throw UnsupportedOperationException()
    override val keys: MutableSet<CharSequence>
        get() = throw UnsupportedOperationException()
    override val values: MutableCollection<V>
        get() = throw UnsupportedOperationException()

    private val pool = GLOBAL_POOL as Pools.SimplePool<Node<V>>
    private val root = Node<V>()

    override fun get(key: CharSequence): V? {
        return get(key, root, 0)
    }

    override fun put(key: CharSequence, value: V): V? {
        return put(key, value, root, 0)
    }

    override fun clear() {
        root.clear(pool)
    }

    override fun getKeyAt(index: Int): CharSequence {
        if (index < 0) {
            throw IndexOutOfBoundsException("Index cannot be negative.")
        }
        return getKeyAt(index, root)
    }

    override fun getValueAt(index: Int): V {
        if (index < 0) {
            throw IndexOutOfBoundsException("Index cannot be negative.")
        }
        return getValueAt(index, root)
    }

    override fun indexOf(key: CharSequence): Int {
        return indexOf(key, root, 0)
    }

    override fun remove(key: CharSequence): V? {
        return remove(key, root, 0)
    }

    override fun containsKey(key: CharSequence): Boolean {
        return get(key) != null
    }

    override fun containsValue(value: V): Boolean {
        throw UnsupportedOperationException()
    }

    override fun isEmpty(): Boolean {
        return root.count == 0
    }

    override fun putAll(from: Map<out CharSequence, V>) {
        for (i in from) {
            put(i.key, i.value)
        }
    }

    private fun get(key: CharSequence, node: Node<V>?, offset: Int): V? {
        if (node == null || offset == key.length && node.terminalKey == null) {
            return null
        }

        val terminalKey = node.terminalKey
        if (terminalKey != null) {
            // TODO(tilal6991) - is this faster than substring + equals?
            if (key.equalFromOffset(terminalKey, offset)) {
                return node.terminalValue!!
            } else if (offset == key.length) {
                return null
            }
        }

        val child = node.get(key[offset])
        return get(key, child, offset + 1)
    }

    private fun put(key: CharSequence, value: V, node: Node<V>, offset: Int): V? {
        if (offset == key.length) {
            val oldKey = node.terminalKey
            val oldValue = node.terminalValue
            val oldTrueTerminal = node.trueTerminal
            node.setTerminal(key, value, true)

            if (oldKey == null || oldValue == null) {
                node.incrementCount()
                return null
            } else if (oldTrueTerminal) {
                return oldValue
            }

            node.incrementCount()
            put(oldKey, oldValue, node, offset)
            return null
        }

        val terminalKey = node.terminalKey
        if (terminalKey != null && !node.trueTerminal) {
            val terminalValue = node.terminalValue
            node.resetTerminals()
            node.decrementCount()
            put(terminalKey, terminalValue!!, node, offset)
        }

        val char = key[offset]
        val child = node.get(char)
        if (child == null) {
            // It's very important that we only do the setTerminal case where we create another
            // node as otherwise we get into all sorts of trouble when we do the old terminal
            // insertion.
            val newNode = pool.acquire()
            newNode.setTerminal(key, value, offset + 1 == key.length)
            newNode.incrementCount()

            node.put(char, newNode)
            node.incrementCount()
            return null
        }

        val old = put(key, value, child, offset + 1)
        if (old == null) {
            node.incrementCount()
        }
        return old
    }

    private fun remove(key: CharSequence, node: Node<V>?, offset: Int): V? {
        if (node == null || offset == key.length && node.terminalKey == null) {
            return null
        }

        val terminalKey = node.terminalKey
        if (terminalKey != null) {
            // TODO(tilal6991) - is this faster than substring + equals?
            if (key.equalFromOffset(terminalKey, offset)) {
                val value = node.terminalValue!!
                node.resetTerminals()
                node.decrementCount()
                return value
            } else if (offset == key.length) {
                return null
            }
        }

        val char = key[offset]
        val child = node.get(char)
        val value = remove(key, child, offset + 1) ?: return null
        if (child!!.count == 0) {
            pool.release(node.remove(char))
        }
        node.decrementCount()
        return value
    }

    private fun indexOf(key: CharSequence, node: Node<V>?, offset: Int): Int {
        if (node == null || offset == key.length && node.terminalKey == null) {
            return IndexedMap.NO_POSITION
        }

        val terminalKey = node.terminalKey
        if (terminalKey != null) {
            // TODO(tilal6991) - is this faster than substring + equals?
            if (key.equalFromOffset(terminalKey, offset)) {
                return 0
            } else if (offset == key.length) {
                return IndexedMap.NO_POSITION
            }
        }

        val mapIndex = node.mapView.indexOf(key[offset])
        val child = node.mapView.getValueAt(mapIndex)
        val absIndex = mapIndex + if (terminalKey != null) 1 else 0
        return absIndex + indexOf(key, child, offset + 1)
    }

    private fun getKeyAt(index: Int, node: Node<V>): CharSequence {
        if (index >= node.count) {
            throw IndexOutOfBoundsException()
        }

        var mapIndex = index
        val key = node.terminalKey
        if (key != null) {
            if (index == 0) {
                return key
            }
            mapIndex--
        }

        var runningCount = 0
        val mapView = node.mapView
        for (i in 0..mapView.size - 1) {
            val child = mapView.getValueAt(i)
            if (mapIndex < runningCount + child.count) {
                return getKeyAt(mapIndex - runningCount)
            }
            runningCount += child.count
        }

        // This means the running count did not match the actual count which is a bug.
        throw IllegalStateException()
    }

    private fun getValueAt(index: Int, node: Node<V>): V {
        if (index >= node.count) {
            throw IndexOutOfBoundsException()
        }

        var mapIndex = index
        val value = node.terminalValue
        if (value != null) {
            if (index == 0) {
                return value
            }
            mapIndex--
        }

        var runningCount = 0
        val mapView = node.mapView
        for (i in 0..mapView.size - 1) {
            val child = mapView.getValueAt(i)
            if (mapIndex < runningCount + child.count) {
                return getValueAt(mapIndex - runningCount)
            }
            runningCount += child.count
        }

        // This means the running count did not match the actual count which is a bug.
        throw IllegalStateException()
    }

    private class Node<T : Any> {
        var terminalKey: CharSequence? = null
            private set
        var terminalValue: T? = null
            private set
        var trueTerminal: Boolean = false
            private set
        var count = 0
            private set
        val mapView: IndexedMap<Char, Node<T>>
            get() = map

        private val map: SortedArrayMap<Char, Node<T>> = SortedArrayMap(CharComparator.instance, 3)

        fun get(c: Char): Node<T>? {
            return map[c]
        }

        fun put(char: Char, newNode: Node<T>) {
            map.put(char, newNode)
        }

        fun remove(char: Char): Node<T>? {
            return map.remove(char)
        }

        fun setTerminal(key: CharSequence, value: T, trueTerminal: Boolean) {
            this.terminalKey = key
            this.terminalValue = value
            this.trueTerminal = trueTerminal
        }

        fun resetTerminals() {
            this.terminalKey = null
            this.terminalValue = null
            this.trueTerminal = false
        }

        fun clear(pool: Pools.SimplePool<Node<T>>) {
            for (i in 0..map.size - 1) {
                val node = map.getValueAt(i)
                node!!.clear(pool)
                if (!pool.release(node)) break
            }

            map.clear()
            resetTerminals()
            count = 0
        }

        fun incrementCount() {
            count++
        }

        fun decrementCount() {
            count--
        }
    }

    private fun CharSequence.equalFromOffset(terminalKey: CharSequence, offset: Int): Boolean {
        if (terminalKey.length != length) {
            return false
        } else if (offset == length) {
            return true
        }

        for (i in offset..length - 1) {
            if (this[i] != terminalKey[i]) {
                return false
            }
        }
        return true
    }

    private class NodePool(private val maxPoolSize: Int) : Pools.SimplePool<Node<Any>>(maxPoolSize) {
        override fun acquire(): Node<Any> {
            return super.acquire() ?: Node()
        }

        override fun release(instance: Node<Any>?): Boolean {
            return instance == null || super.release(instance)
        }
    }

    companion object {
        private val GLOBAL_POOL = NodePool(50)
    }
}
