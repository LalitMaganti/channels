package co.fusionx.channels.viewmodel.transitory

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.ObservableField
import android.view.View
import co.fusionx.channels.BR

public class NavigationHeaderVM : BaseObservable() {
    public val title: ObservableField<CharSequence> = ObservableField("")
    public val subtitle: ObservableField<CharSequence> = ObservableField("")
    public var headerListener: View.OnClickListener? = null
        @Bindable get
        set(it) {
            field = it
            notifyPropertyChanged(BR.headerListener)
        }

    public fun updateText(title: CharSequence, subtitle: CharSequence) {
        this.title.set(title)
        this.subtitle.set(subtitle)
    }

    public fun updateListener(listener: View.OnClickListener?) {
        headerListener = listener
    }
}