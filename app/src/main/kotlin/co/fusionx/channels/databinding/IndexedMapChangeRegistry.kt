package co.fusionx.channels.databinding

import android.databinding.CallbackRegistry

class IndexedMapChangeRegistry<T : ObservableIndexedMap<K, V>, K, V> :
        CallbackRegistry<ObservableIndexedMap.OnIndexedMapChangedCallback<T, K, V>, T, IndexedMapChangeRegistry.MapChanges<K, V>>(
                IndexedMapChangeRegistry.NOTIFIER_CALLBACK as CallbackRegistry.NotifierCallback<ObservableIndexedMap.OnIndexedMapChangedCallback<T, K, V>, T, MapChanges<K, V>>) {

    fun notifyChange(sender: T) {
        notifyCallbacks(sender, 0, MapChanges.acquire())
    }

    fun notifyItemChanged(sender: T, position: Int, key: K, oldValue: V, value: V) {
        notifyCallbacks(sender, 1, MapChanges.acquire(position = position, key = key, oldValue = oldValue, value = value))
    }

    fun notifyItemInserted(sender: T, position: Int, key: K, value: V) {
        notifyCallbacks(sender, 2, MapChanges.acquire(position = position, key = key, value = value))
    }

    fun notifyItemMoved(sender: T, oldPosition: Int, position: Int, key: K, value: V) {
        notifyCallbacks(sender, 3, MapChanges.acquire(oldPosition = oldPosition, position = position, key = key, value = value))
    }

    fun notifyItemRemoved(sender: T, position: Int, key: K, value: V) {
        notifyCallbacks(sender, 4, MapChanges.acquire(position = position, key = key, value = value))
    }

    companion object {
        private val NOTIFIER_CALLBACK = object :
                CallbackRegistry.NotifierCallback<ObservableIndexedMap.OnIndexedMapChangedCallback<ObservableIndexedMap<Any, Any>, Any, Any>, ObservableIndexedMap<Any, Any>, MapChanges<Any, Any>>() {
            override fun onNotifyCallback(callback: ObservableIndexedMap.OnIndexedMapChangedCallback<ObservableIndexedMap<Any, Any>, Any, Any>, sender: ObservableIndexedMap<Any, Any>, arg: Int, arg2: MapChanges<Any, Any>) {
                when (arg) {
                    0 -> callback.onChanged(sender)
                    1 -> callback.onItemChanged(sender, arg2.position, arg2.key!!, arg2.oldValue!!, arg2.value!!)
                    2 -> callback.onItemInserted(sender, arg2.position, arg2.key!!, arg2.value!!)
                    3 -> callback.onItemMoved(sender, arg2.oldPosition, arg2.position, arg2.key!!, arg2.value!!)
                    4 -> callback.onItemRemoved(sender, arg2.position, arg2.key!!, arg2.value!!)
                }
            }
        }
    }

    public class MapChanges<K, V> private constructor() {
        public var oldPosition = -1
        public var position = -1
        public var key: K? = null
        public var oldValue: V? = null
        public var value: V? = null

        public companion object {
            private val mapChanges: MapChanges<Any, Any> by lazy { MapChanges<Any, Any>() }
            public fun <K, V> acquire(oldPosition: Int = -1, position: Int = -1, key: K? = null, oldValue: V? = null, value: V? = null): MapChanges<K, V> {
                mapChanges.oldPosition = oldPosition
                mapChanges.position = position
                mapChanges.key = key
                mapChanges.oldValue = oldValue
                mapChanges.value = value
                return mapChanges as MapChanges<K, V>
            }
        }
    }
}
