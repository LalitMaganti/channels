package com.tilal6991.channels.viewmodel

import java.util.*

class SelectedClientsVM {

    val latest: ClientVM?
        get() = clientList.firstOrNull()
    val penultimate: ClientVM?
        get() = if (clientList.size < 2) null else clientList[1]
    val antepenultimate: ClientVM?
        get() = if (clientList.size < 3) null else clientList[2]

    val clientList: LinkedList<ClientVM> = LinkedList()

    private val callbacks: MutableCollection<OnClientsChangedCallback> = ArrayList()

    fun select(client: ClientVM) {
        val i = 0
        val it = clientList.iterator()
        while (it.hasNext()) {
            val current = it.next()
            if (current != client) {
                continue
            }

            if (i == 0) {
                return
            } else if (i == 1) {
                return selectPenultimate()
            } else if (i == 2) {
                return selectAntePenultimate()
            } else {
                it.remove()
                break
            }
        }

        clientList.addFirst(client)
        callbacks.forEach { it.onNewClientAdded() }
    }

    fun selectPenultimate() {
        if (clientList.size < 2) {
            return
        }

        var oldLatest = clientList.removeFirst()
        var oldSecond = clientList.removeFirst()
        clientList.addFirst(oldLatest)
        clientList.addFirst(oldSecond)

        callbacks.forEach { it.onLatestPenultimateSwap() }
    }

    fun selectAntePenultimate() {
        if (clientList.size < 3) {
            return
        }

        var latest = clientList.removeFirst()
        var second = clientList.removeFirst()
        var third = clientList.removeFirst()
        clientList.addFirst(second)
        clientList.addFirst(latest)
        clientList.addFirst(third)

        callbacks.forEach { it.onLatestAntePenultimateSwap() }
    }

    fun closeSelected() {
        if (clientList.isEmpty()) {
            return
        }
        clientList.removeFirst()
        callbacks.forEach { it.onLatestClosed() }
    }

    fun addOnClientsChangedCallback(callback: OnClientsChangedCallback) {
        callbacks.add(callback)
    }

    fun removeOnClientsChangedCallback(callback: OnClientsChangedCallback) {
        callbacks.remove(callback)
    }

    interface OnClientsChangedCallback {
        fun onNewClientAdded()
        fun onLatestPenultimateSwap()
        fun onLatestAntePenultimateSwap()
        fun onLatestClosed()
    }

    interface OnLatestClientChangedCallback : OnClientsChangedCallback {
        override fun onNewClientAdded() {
            onLatestClientChanged()
        }

        override fun onLatestPenultimateSwap() {
            onLatestClientChanged()
        }

        override fun onLatestAntePenultimateSwap() {
            onLatestClientChanged()
        }

        override fun onLatestClosed() {
            onLatestClientChanged()
        }

        fun onLatestClientChanged()
    }
}