package co.fusionx.channels.presenter.helper

import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.support.design.widget.TextInputLayout
import android.text.Editable
import android.text.TextWatcher
import android.widget.CompoundButton
import co.fusionx.channels.R

class CommitingNullableWatcher(private val field: ObservableField<CharSequence?>) : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        field.set(s)
    }
}

class CommitingWatcher(private val field: ObservableField<CharSequence>) : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        field.set(s)
    }
}

class CommitingIntWatcher(private val field: ObservableInt) : TextWatcher {
    override fun afterTextChanged(s: Editable?) {
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (s.isEmpty()) {
            return
        }
        field.set(parseInt(s))
    }

    private fun parseInt(s: CharSequence): Int {
        var parsed = 0
        var base = 1
        for (i in s.length - 1 downTo 0) {
            val c = s[i]
            if (c < '0' || c > '9') {
                throw IllegalArgumentException("Should never happen.")
            }

            var j = c - '0'
            parsed += j * base
            base *= 10
        }
        return parsed
    }
}

class CommitingBooleanWatcher(private val field: ObservableBoolean) : CompoundButton.OnCheckedChangeListener {
    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        field.set(isChecked)
    }
}

open class ErrorWatcher(private val layout: TextInputLayout,
                        private val errorMsg: Int,
                        private val errorFn: (CharSequence) -> Boolean) : TextWatcher {
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

class EmptyWatcher(private val layout: TextInputLayout) : ErrorWatcher(layout, R.string.empty_error, { it.isEmpty() })