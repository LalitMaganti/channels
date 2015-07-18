package co.fusionx.channels.adapter

public interface CollectionAdapter<T> {
    val items: MutableList<T>

    public fun getItemCount(): Int = headerCount() + items.size + footerCount()

    fun add(item: T) {
        val headerCount = headerCount()
        val itemCount = items.size
        items.add(item)
        notifyItemInserted(headerCount + itemCount)
    }

    fun addAll(newItems: List<T>) {
        val count = getItemCount()
        items.addAll(newItems)
        notifyItemRangeInserted(count, newItems.size)
    }

    fun clear() {
        val count = items.size
        items.clear()
        notifyItemRangeRemoved(headerCount(), count)
    }

    fun replaceAll(newItems: List<T>) {
        clear()
        addAll(newItems)
    }

    fun get(position: Int): T = items[position]

    fun update(item: T, predicate: (T) -> Boolean) {
        val i = items.indexOfFirst(predicate)
        items[i] = item
        notifyItemChanged(headerCount() + i)
    }

    fun remove(item: T) {
        val i = items.indexOf(item)
        items.removeAt(i)
        notifyItemRemoved(headerCount() + i)
    }

    fun remove(predicate: (T) -> Boolean) {
        val i = items.indexOfFirst(predicate)
        items.removeAt(i)
        notifyItemRemoved(headerCount() + i)
    }

    fun notifyItemRangeInserted(positionStart: Int, size: Int)
    fun notifyItemRangeRemoved(positionStart: Int, count: Int)
    fun notifyItemInserted(position: Int)
    fun notifyItemChanged(position: Int)
    fun notifyItemRemoved(position: Int)

    fun headerCount() = 0
    fun footerCount() = 0
}