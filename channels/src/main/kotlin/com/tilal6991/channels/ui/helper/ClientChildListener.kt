package com.tilal6991.channels.ui.helper

import android.content.Context
import android.databinding.Observable
import com.tilal6991.channels.base.relayVM
import com.tilal6991.channels.ui.Bindable
import com.tilal6991.channels.viewmodel.ClientChildVM
import com.tilal6991.channels.viewmodel.ClientVM
import com.tilal6991.channels.viewmodel.SelectedClientsVM

abstract class ClientChildListener(private val context: Context) : Bindable,
        Observable.OnPropertyChangedCallback(), SelectedClientsVM.OnLatestClientChangedCallback {

    private val selectedClientsVM: SelectedClientsVM
        get() = context.relayVM.selectedClients

    private var registeredClient: ClientVM? = null

    override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
        onChildChange(registeredClient?.selectedChild?.get())
    }

    override fun onLatestClientChanged() {
        registeredClient?.removeOnPropertyChangedCallback(this)
        registeredClient = selectedClientsVM.latest
        onChildChange(registeredClient?.selectedChild?.get())
        registeredClient?.selectedChild?.addOnPropertyChangedCallback(this)
    }

    override fun bind() {
        selectedClientsVM.addOnClientsChangedCallback(this)

        registeredClient = selectedClientsVM.latest
        registeredClient?.selectedChild?.addOnPropertyChangedCallback(this)
    }

    override fun unbind() {
        registeredClient?.selectedChild?.removeOnPropertyChangedCallback(this)
        registeredClient = null

        selectedClientsVM.removeOnClientsChangedCallback(this)
    }

    abstract fun onChildChange(clientChild: ClientChildVM?)
}