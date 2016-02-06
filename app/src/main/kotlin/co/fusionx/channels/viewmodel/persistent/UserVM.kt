package co.fusionx.channels.viewmodel.persistent

import android.databinding.BaseObservable
import android.databinding.Bindable
import co.fusionx.channels.BR
import co.fusionx.channels.collections.ObservableSortedList
import co.fusionx.channels.viewmodel.helper.ChannelComparator

class UserVM(nick: String) : BaseObservable() {

    var nick: String = nick
        @Bindable get() = field
        private set(it) {
            field = it
            notifyPropertyChanged(BR.nick)
        }

    val channels: ObservableSortedList<ChannelVM> = ObservableSortedList(
            ChannelVM::class.java, ChannelComparator.instance)

    fun onJoin(c: ChannelVM) {
        channels.add(c)
    }

    fun onNickChange(newNick: String) {
        nick = newNick
    }
}