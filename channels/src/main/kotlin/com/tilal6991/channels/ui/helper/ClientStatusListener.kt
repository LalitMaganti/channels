package com.tilal6991.channels.ui.helper

import android.databinding.Observable
import android.databinding.ObservableList
import com.tilal6991.channels.BR
import com.tilal6991.channels.collections.ObservableListChangedProxy
import com.tilal6991.channels.configuration.ChannelsConfiguration
import com.tilal6991.channels.ui.Bindable
import com.tilal6991.channels.viewmodel.ClientVM
import com.tilal6991.channels.viewmodel.RelayVM

abstract class ClientStatusListener(private val relayVM: RelayVM) : Bindable {

    private val statusListener = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable, propertyId: Int) {
            if (propertyId == BR.active) {
                val client = sender as ClientVM
                if (!client.active) {
                    client.removeOnPropertyChangedCallback(this)
                }
            } else if (propertyId == BR.statusInt) {
                onStatusChanged(relayVM.activeConfigs)
            }
        }
    }

    private val listListener = object : ObservableListChangedProxy<ChannelsConfiguration>() {
        override fun onListChanged(sender: ObservableList<ChannelsConfiguration>) {
            onStatusChanged(relayVM.activeConfigs)
        }
    }

    override fun bind() {
        for (i in relayVM.activeConfigs) {
            relayVM.configActiveClients[i].addOnPropertyChangedCallback(statusListener)
        }
        relayVM.activeConfigs.addOnListChangedCallback(listListener)
    }

    override fun unbind() {
        relayVM.activeConfigs.removeOnListChangedCallback(listListener)
        for (i in relayVM.activeConfigs) {
            relayVM.configActiveClients[i].removeOnPropertyChangedCallback(statusListener)
        }
    }

    abstract fun onStatusChanged(sender: ObservableList<ChannelsConfiguration>)
}