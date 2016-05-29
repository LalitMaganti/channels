package com.tilal6991.channels.redux.util

import android.support.v7.widget.RecyclerView

class TransactingAdapterHelper(private val callback: RecyclerView.AdapterDataObserver) {

    private var init = false
    private lateinit var currentList: TransactingIndexedList<*>

    fun onNewList(list: TransactingIndexedList<*>) {
        if (!init) {
            init = true
            currentList = list
            return callback.onChanged()
        }

        val oldList = currentList
        currentList = list

        val diff = list.transactionCount() - oldList.transactionCount()
        if (diff > oldList.maxSize()) {
            return callback.onChanged()
        } else if (diff == 0) {
            return
        } else if (diff < 0) {
            inverseConsume(oldList, list.transactionCount())
        } else {
            consume(list, oldList.transactionCount())
        }
    }

    private fun inverseConsume(oldList: TransactingIndexedList<*>, transactionCount: Int) {
        for (i in oldList.size() - 1..transactionCount) {
            val transaction = oldList.transactions.get(i)
            when (transaction.type) {
                TransactingIndexedList.ADD -> callback.onItemRangeRemoved(transaction.startIndex, transaction.count)
                TransactingIndexedList.REMOVE -> callback.onItemRangeInserted(transaction.startIndex, transaction.count)
                TransactingIndexedList.MOVE -> callback.onItemRangeMoved(transaction.toIndex, transaction.startIndex, transaction.count)
                TransactingIndexedList.CHANGE -> callback.onItemRangeChanged(transaction.startIndex, transaction.count)
            }
        }
    }

    private fun consume(list: TransactingIndexedList<*>, startIndex: Int) {
        for (i in startIndex..list.transactionCount() - 1) {
            val transaction = list.transactions.get(i)
            when (transaction.type) {
                TransactingIndexedList.ADD -> callback.onItemRangeInserted(transaction.startIndex, transaction.count)
                TransactingIndexedList.REMOVE -> callback.onItemRangeRemoved(transaction.startIndex, transaction.count)
                TransactingIndexedList.MOVE -> callback.onItemRangeMoved(transaction.startIndex, transaction.toIndex, transaction.count)
                TransactingIndexedList.CHANGE -> callback.onItemRangeChanged(transaction.startIndex, transaction.count)
            }
        }
    }
}