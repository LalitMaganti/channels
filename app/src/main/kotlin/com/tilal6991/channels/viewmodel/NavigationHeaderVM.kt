package com.tilal6991.channels.viewmodel

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.ObservableField
import android.view.View
import com.tilal6991.channels.BR

class NavigationHeaderVM : BaseObservable() {
    val title: ObservableField<CharSequence> = ObservableField("")
    val subtitle: ObservableField<CharSequence> = ObservableField("")
    var headerListener: View.OnClickListener? = null
        @Bindable get
        set(it) {
            field = it
            notifyPropertyChanged(BR.headerListener)
        }

    fun updateText(title: CharSequence, subtitle: CharSequence) {
        this.title.set(title)
        this.subtitle.set(subtitle)
    }

    fun updateListener(listener: View.OnClickListener?) {
        headerListener = listener
    }
}