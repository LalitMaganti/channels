package com.tilal6991.channels.viewmodel

import android.util.ArraySet
import java.util.*

class SelectedClientsVM {
    var latest: ClientVM? = null
        private set
    var penultimate: ClientVM? = null
        private set
    var antepenultimate: ClientVM? = null
        private set

    private val callbacks: MutableCollection<OnClientsChangedCallback> = ArrayList()

    fun select(client: ClientVM) {
        if (client == latest) {
            return
        } else if (client == penultimate) {
            return selectPenultimate()
        } else if (client == antepenultimate) {
            return selectAntePenultimate()
        }

        antepenultimate = penultimate
        penultimate = latest
        latest = client

        callbacks.forEach { it.onNewClientAdded() }
    }

    fun selectPenultimate() {
        var oldLatest = latest
        latest = penultimate
        penultimate = oldLatest

        callbacks.forEach { it.onLatestPenultimateSwap() }
    }

    fun selectAntePenultimate() {
        var oldLatest = latest
        latest = antepenultimate
        antepenultimate = oldLatest

        callbacks.forEach { it.onLatestAntePenultimateSwap() }
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

        fun onLatestClientChanged()
    }
}