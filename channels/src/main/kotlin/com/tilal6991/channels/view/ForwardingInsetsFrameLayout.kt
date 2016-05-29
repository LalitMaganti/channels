package com.tilal6991.channels.view

import android.content.Context
import android.util.AttributeSet
import android.view.WindowInsets
import android.widget.FrameLayout

class ForwardingInsetsFrameLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0,
        defStyleRes: Int = 0) : FrameLayout(context, attrs, defStyle, defStyleRes) {

    override fun onApplyWindowInsets(insets: WindowInsets): WindowInsets {
        for (i in 0..childCount - 1) {
            getChildAt(i).dispatchApplyWindowInsets(insets)
        }
        return insets
    }
}