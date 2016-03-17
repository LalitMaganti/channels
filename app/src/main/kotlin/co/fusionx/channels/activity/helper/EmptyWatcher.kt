package co.fusionx.channels.activity.helper

import android.support.design.widget.TextInputLayout
import co.fusionx.channels.R

class EmptyWatcher(private val layout: TextInputLayout) : ErrorWatcher(layout, { it.isEmpty() }, R.string.empty_error)