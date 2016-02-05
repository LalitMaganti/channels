package co.fusionx.channels.presenter.helper

import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import co.fusionx.channels.presenter.Bindable

public class MessageTextHandler(private val editText: EditText) : Bindable {

    private val listener = TextView.OnEditorActionListener { textView, actionId, keyEvent ->
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            sendMessage();
            return@OnEditorActionListener true
        }
        return@OnEditorActionListener false
    }

    private fun sendMessage() {
        editText.text
    }

    public override fun bind() {
        editText.setOnEditorActionListener(listener)
    }

    public override fun unbind() {
        editText.setOnEditorActionListener(null)
    }
}