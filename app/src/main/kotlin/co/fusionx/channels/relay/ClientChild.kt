package co.fusionx.channels.relay

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.ObservableArrayList
import android.databinding.ObservableList

import co.fusionx.channels.BR

abstract class ClientChild : BaseObservable() {

    abstract val name: CharSequence

    val buffer: ObservableList<CharSequence> = ObservableArrayList()
    val message: CharSequence
        @Bindable get() = buffer.lastOrNull() ?: "No message to show"

    fun add(message: String) {
        buffer.add(message)
        notifyPropertyChanged(BR.message)
    }
}