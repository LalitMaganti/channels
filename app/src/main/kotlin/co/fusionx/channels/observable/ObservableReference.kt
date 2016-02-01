package co.fusionx.channels.observable

import java.util.*

public class ObservableReference<T : Any>(private var reference: T?) {
    private val observers: MutableList<Observer<T>> = LinkedList()

    public fun get(): T? = reference
    public fun set(obj: T?) {
        observers.forEach { it.onPreSet(obj) }
        reference = obj
        observers.forEach { it.onPostSet(obj) }
    }

    public fun addObserver(observer: Observer<T>) {
        observers.add(observer)
    }

    public fun removeObserver(observer: Observer<T>) {
        observers.remove(observer)
    }

    public interface Observer<T : Any> {
        public fun onPreSet(new: T?) = Unit
        public fun onPostSet(new: T?) = Unit
    }
}