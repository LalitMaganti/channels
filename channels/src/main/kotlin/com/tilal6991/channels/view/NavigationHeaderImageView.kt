package com.tilal6991.channels.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView

class NavigationHeaderImageView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null) : ImageView(context, attrs) {

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
        if (l == null) {
            isClickable = false
        }
    }
}