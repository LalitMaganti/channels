package co.fusionx.channels.model

import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import co.fusionx.channels.BR

abstract class ClientChild {

    abstract val name: CharSequence

    val buffer: ObservableList<CharSequence> = ObservableArrayList()

    fun add(message: String) {
        buffer.add(message)
    }
}