package com.tilal6991.channels.viewmodel

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import com.tilal6991.channels.BR

abstract class ClientChildVM : BaseObservable(), ClientVM.StatusListener {
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

    override fun onSocketConnect() = add("Connection was successful.")
    override fun onConnectFailed() = add("Failed to connect to the server.")
    override fun onDisconnecting() = add("Disconnecting from the server.")
    override fun onDisconnected() = add("Disconnected from the server.")
    override fun onConnecting() = add("Connecting to the server.")
    override fun onReconnecting() = add("Trying to reconnect in 5 seconds.")
}