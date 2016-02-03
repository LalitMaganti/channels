package co.fusionx.channels.databinding

import android.support.v7.util.SortedList

class ObservableSortedList<T>(
        klass: Class<T>?,
        public val registry: SortedListCallbackRegistry<T>) : SortedList<T>(klass, registry) {

    public fun addObserver(callback: SortedListCallbackRegistry.Callback) {
        registry.addCallback(callback)
    }

    fun removeObserver(callback: SortedListCallbackRegistry.Callback) {
        registry.removeCallback(callback)
    }
}