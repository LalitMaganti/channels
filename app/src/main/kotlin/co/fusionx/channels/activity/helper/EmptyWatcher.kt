package co.fusionx.channels.activity.helper

import android.support.design.widget.TextInputLayout
import co.fusionx.channels.R

class EmptyWatcher(private val layout: TextInputLayout) : ErrorWatcher(layout, R.string.empty_error, { it.isEmpty() })