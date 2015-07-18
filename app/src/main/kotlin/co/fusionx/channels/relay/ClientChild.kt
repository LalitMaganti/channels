package co.fusionx.channels.relay

import co.fusionx.channels.observable.ObservableList

abstract class ClientChild {
    public abstract val name: CharSequence
    public val message: CharSequence
        get() = if (buffer.isEmpty()) "No message to show." else buffer.last()
    public abstract val buffer: ObservableList<CharSequence>

    protected fun add(data: CharSequence) {
        buffer.add(data)
    }
}