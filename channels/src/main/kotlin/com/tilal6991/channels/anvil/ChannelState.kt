package com.tilal6991.channels.anvil

import android.databinding.BaseObservable
import com.tilal6991.channels.collections.CharSequenceTreeMap
import com.tilal6991.channels.collections.ObservableSortedArrayMap
import com.tilal6991.channels.util.UserListComparator
import com.tilal6991.channels.util.UserPrefixComparator
import com.tilal6991.channels.viewmodel.ChannelVM

class ChannelState(override val name: String,
                   private val comparator: UserPrefixComparator) : ClientChildState() {

    val userMap = ObservableSortedArrayMap(comparator, UserListComparator.Companion.instance)
    val treeMap = CharSequenceTreeMap<ChannelVM.UserVM>()

    inner class User(initialNick: String, initialMode: Char?) : BaseObservable() {

        var displayString: String
        var mode: Char?

        init {
            displayString = if (initialMode == null) initialNick else initialMode + initialNick
            mode = initialMode
        }
    }
}