package co.fusionx.channels.databinding

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.view.View
import co.fusionx.channels.BR

public class ViewClickListener : BaseObservable() {
    public var headerListener: View.OnClickListener? = null
        @Bindable get
        set(it) {
            field = it
            notifyPropertyChanged(BR.headerListener)
        }
}