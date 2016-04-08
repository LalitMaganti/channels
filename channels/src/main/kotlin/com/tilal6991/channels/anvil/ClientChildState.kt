package com.tilal6991.channels.anvil

import android.databinding.ObservableArrayList
import android.databinding.ObservableList
import com.brianegan.bansa.State

abstract class ClientChildState : State {

    abstract val name: CharSequence

    var active: Boolean = false

    val buffer: ObservableList<CharSequence> = ObservableArrayList()

    val message: CharSequence
        get() = buffer.lastOrNull() ?: "No message to show"
}