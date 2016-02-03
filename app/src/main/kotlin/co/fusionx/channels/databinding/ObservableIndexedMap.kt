package co.fusionx.channels.databinding

public interface ObservableIndexedMap<K, V> : MutableMap<K, V> {
    public fun getAtIndex(index: Int): V?
    public fun indexOf(key: K): Int

    fun addOnIndexedMapChangedCallback(
            callback: OnIndexedMapChangedCallback<out ObservableIndexedMap<K, V>, K, V>)

    fun removeOnIndexedMapChangedCallback(
            callback: OnIndexedMapChangedCallback<out ObservableIndexedMap<K, V>, K, V>)

    public interface OnIndexedMapChangedCallback<T : ObservableIndexedMap<K, V>, K, V> {
        fun onChanged(sender: T)
        fun onItemChanged(sender: T, position: Int, key: K, oldValue: V, newValue: V)
        fun onItemInserted(sender: T, position: Int, key: K, value: V)
        fun onItemMoved(sender: T, fromPosition: Int, toPosition: Int, key: K, value: V)
        fun onItemRemoved(sender: T, position: Int, key: K, value: V)
    }

    companion object {
        public const val NO_POSITION = -1
    }
}