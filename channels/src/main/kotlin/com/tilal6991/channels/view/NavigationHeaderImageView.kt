package com.tilal6991.channels.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView

class NavigationHeaderImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0) : ImageView(context, attrs, defStyle) {

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        if (l == null) {
            isClickable = false
        }
    }
}