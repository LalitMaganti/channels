package com.tilal6991.channels.collections

import com.tilal6991.channels.util.failAssert
import timber.log.Timber
import java.util.*

class CharSequenceTreeMap<V : Any> : IndexedMap<CharSequence, V> {

    override val size: Int
        get() = root.count
    override val entries: MutableSet<MutableMap.MutableEntry<CharSequence, V>>
        get() = throw UnsupportedOperationException()
    override val keys: MutableSet<CharSequence>
        get() = throw UnsupportedOperationException()
    override val values: MutableCollection<V>
        get() = throw UnsupportedOperationException()

    private val root = Node<V>()

    override fun get(key: CharSequence): V? {
        return get(key, root, 0)
    }

    override fun put(key: CharSequence, value: V): V? {
        put(key, value, root, 0)
        return null
    }

    override fun clear() {
        root.clear()
    }

    override fun getKeyAt(index: Int): CharSequence? {
        if (index < 0) {
            Timber.asTree().e(NegativeArraySizeException(), "Index cannot be negative.")
        }
        return getKeyAt(index, root)
    }

    override fun getValueAt(index: Int): V? {
        if (index < 0) {
            Timber.asTree().e(NegativeArraySizeException(), "Index cannot be negative.")
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

    private fun put(key: CharSequence, value: V, node: Node<V>, offset: Int) {
        if (offset == key.length) {
            if (node.terminalKey == null) {
                node.setTerminal(key, value, true)
            } else {
                if (node.trueTerminal) {
                    // We should not be inserting the same nick twice.
                    Timber.asTree().failAssert()
                } else {
                    val oldKey = node.terminalKey
                    val oldValue = node.terminalValue
                    node.setTerminal(key, value, true)
                    put(oldKey!!, oldValue!!, node, offset)
                }
            }
            return
        }

        val terminalKey = node.terminalKey
        if (terminalKey != null && !node.trueTerminal) {
            val terminalValue = node.terminalValue
            node.resetTerminals()
            put(terminalKey, terminalValue!!, node, offset)
        }

        val char = key[offset]
        val child = node.get(char)
        if (child == null) {
            // It's very important that we only do the setTerminal case where we create another
            // node as otherwise we get into all sorts of trouble when we do the old terminal
            // insertion.
            val newNode = Node<V>()
            newNode.setTerminal(key, value, false)
            node.put(char, newNode)
        } else {
            node.incrementCount()
            put(key, value, child, offset + 1)
        }
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
                return value
            } else if (offset == key.length) {
                return null
            }
        }

        val char = key[offset]
        val child = node.get(char)
        val value = remove(key, child, offset + 1) ?: return null
        if (child!!.count == 0) {
            node.remove(char)
            node.decrementCount()
        }
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

    private fun getKeyAt(index: Int, node: Node<V>): CharSequence? {
        if (index >= node.count) {
            return null
        }

        var mapIndex = index
        if (node.terminalKey != null) {
            if (index == 0) {
                return node.terminalKey
            }
            mapIndex--
        }

        var runningCount = 0
        val mapView = node.mapView
        for (i in 0..mapView.size - 1) {
            val child = mapView.getValueAt(i)!!
            if (mapIndex < runningCount + child.count) {
                return getKeyAt(mapIndex - runningCount)
            }
            runningCount += child.count
        }

        // This means the running count did not match the actual count which is a bug.
        Timber.asTree().failAssert()
        return null
    }

    private fun getValueAt(index: Int, node: Node<V>): V? {
        if (index >= node.count) {
            return null
        }

        var mapIndex = index
        if (node.terminalKey != null) {
            if (index == 0) {
                return node.terminalValue
            }
            mapIndex--
        }

        var runningCount = 0
        val mapView = node.mapView
        for (i in 0..mapView.size - 1) {
            val child = mapView.getValueAt(i)!!
            if (mapIndex < runningCount + child.count) {
                return getValueAt(mapIndex - runningCount)
            }
            runningCount += child.count
        }

        // This means the running count did not match the actual count which is a bug.
        Timber.asTree().failAssert()
        return null
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

        private val map: SortedArrayMap<Char, Node<T>> = SortedArrayMap(
                Comparator<Char> { p0, p1 -> p0 - p1 }, 3)

        fun get(c: Char): Node<T>? {
            return map[c]
        }

        fun put(char: Char, newNode: Node<T>) {
            map.put(char, newNode)
        }

        fun remove(char: Char) {
            map.remove(char)
        }

        fun setTerminal(key: CharSequence, value: T, trueTerminal: Boolean) {
            this.terminalKey = key
            this.terminalValue = value
            this.trueTerminal = trueTerminal

            incrementCount()
        }

        fun resetTerminals() {
            this.terminalKey = null
            this.terminalValue = null
            this.trueTerminal = false

            decrementCount()
        }

        fun clear() {
            map.clear()
            resetTerminals()
            count = 0
        }

        fun incrementCount() {
            count++
        }

        fun decrementCount() {
            count++
        }
    }

    private fun CharSequence.equalFromOffset(terminalKey: CharSequence, offset: Int): Boolean {
        if (terminalKey.length != length) {
            return false
        } else if (offset == length) {
            return true
        }

        for (i in offset..length) {
            if (this[i] != terminalKey[i]) {
                return false
            }
        }
        return true
    }

}
