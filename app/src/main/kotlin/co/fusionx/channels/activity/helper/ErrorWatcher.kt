package co.fusionx.channels.activity.helper

import android.support.design.widget.TextInputLayout
import android.text.Editable
import android.text.TextWatcher

open class ErrorWatcher(private val layout: TextInputLayout,
                        private val errorFn: (CharSequence) -> Boolean,
                        private val errorMsg: Int) : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (errorFn(s)) {
            layout.error = layout.context.getString(errorMsg)
        } else {
            layout.isErrorEnabled = false
            layout.error = null
        }
    }
}