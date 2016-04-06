package com.tilal6991.channels.ui.helper

import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import com.tilal6991.channels.base.relayVM
import com.tilal6991.channels.ui.Bindable

class MessageTextHandler(private val editText: EditText) : Bindable {

    private val listener = TextView.OnEditorActionListener { textView, actionId, event ->
        if (actionId == EditorInfo.IME_ACTION_SEND ||
                event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER) {
            sendMessage();
            return@OnEditorActionListener true
        }
        return@OnEditorActionListener false
    }

    private fun sendMessage() {
        val text = editText.text
        if (text.isEmpty()) {
            return
        }

        val relayVM = editText.context.relayVM
        val client = relayVM.selectedClients.latest!!
        client.sendUserMessage(text.toString(), client.selectedChild.get()!!)

        editText.setText("")
    }

    override fun bind() {
        editText.setOnEditorActionListener(listener)
    }

    override fun unbind() {
        editText.setOnEditorActionListener(null)
    }
}