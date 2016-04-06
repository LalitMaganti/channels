package com.tilal6991.channels.viewmodel

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import com.tilal6991.channels.BR

abstract class ClientChildVM : BaseObservable() {
    abstract val name: CharSequence
    var active: Boolean = false
        @Bindable get
        protected set(it) {
            field = it
            notifyPropertyChanged(BR.active)
        }

    val message: CharSequence
        @Bindable get() = buffer.lastOrNull() ?: "No message to show"
    val buffer: ObservableList<CharSequence> = ObservableArrayList()

    fun add(message: String) {
        buffer.add(message)
        notifyPropertyChanged(BR.message)
    }
}