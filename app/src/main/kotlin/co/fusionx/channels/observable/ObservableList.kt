package co.fusionx.channels.observable

public class ObservableList<T>(private val wrapped: MutableList<T>): MutableList<T> by wrapped {
    private val observers = arrayListOf<Observer>()

    public override fun add(element: T): Boolean {
        val index = wrapped.size
        val added = wrapped.add(element)
        if (added) {
            for (it in observers) it.onAdd(index)
        }
        return added
    }

    // Observer modifications.
    public fun addObserver(observer: Observer) {
        observers.add(observer)
    }
    public fun removeObserver(observer: Observer) {
        observers.remove(observer)
    }

    public interface Observer {
        public fun onAdd(position: Int) {
        }
    }
}