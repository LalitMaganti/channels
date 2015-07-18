package co.fusionx.channels.observable

import android.database.Observable
import java.util.*

public class ObservableReference<T>(private var reference: T) {
    private val observers: MutableList<Observer<T>> = LinkedList()

    public fun get(): T = reference
    public fun set(obj: T) {
        reference = obj
    }

    public fun addObserver(observer: Observer<T>) {
        observers.add(observer)
    }
    public fun removeObserver(observer: Observer<T>) {
        observers.remove(observer)
    }

    public interface Observer<T> {
        public fun onSet(new: T)
    }
}