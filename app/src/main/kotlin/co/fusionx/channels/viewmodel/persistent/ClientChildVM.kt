package co.fusionx.channels.viewmodel.persistent

import android.databinding.BaseObservable
import android.databinding.Bindable
import android.databinding.ObservableList
import co.fusionx.channels.BR
import co.fusionx.channels.model.ClientChild

public abstract class ClientChildVM(private val child: ClientChild) : BaseObservable() {
    public val name: CharSequence
        get() = child.name
    public val buffer: ObservableList<CharSequence>
        get() = child.buffer
    val message: CharSequence
        @Bindable get() = child.buffer.lastOrNull() ?: "No message to show"

    init {
        child.buffer.addOnListChangedCallback(BufferWatcher())
    }

    inner class BufferWatcher : ObservableList.OnListChangedCallback<ObservableList<CharSequence>>() {
        override fun onItemRangeChanged(sender: ObservableList<CharSequence>?, positionStart: Int, itemCount: Int) {
            notifyPropertyChanged(BR.message)
        }

        override fun onChanged(sender: ObservableList<CharSequence>?) {
            notifyPropertyChanged(BR.message)
        }

        override fun onItemRangeInserted(sender: ObservableList<CharSequence>?, positionStart: Int, itemCount: Int) {
            notifyPropertyChanged(BR.message)
        }

        override fun onItemRangeMoved(sender: ObservableList<CharSequence>?, fromPosition: Int, toPosition: Int, itemCount: Int) {
            notifyPropertyChanged(BR.message)
        }

        override fun onItemRangeRemoved(sender: ObservableList<CharSequence>?, positionStart: Int, itemCount: Int) {
            notifyPropertyChanged(BR.message)
        }
    }
}