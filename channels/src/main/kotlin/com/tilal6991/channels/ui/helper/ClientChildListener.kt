package com.tilal6991.channels.ui.helper

import android.content.Context
import android.databinding.Observable
import android.databinding.ObservableField
import com.tilal6991.channels.base.relayVM
import com.tilal6991.channels.ui.Bindable
import com.tilal6991.channels.viewmodel.ClientChildVM
import com.tilal6991.channels.viewmodel.SelectedClientsVM

abstract class ClientChildListener(private val context: Context) : Bindable,
        Observable.OnPropertyChangedCallback(), SelectedClientsVM.OnLatestClientChangedCallback {

    private val selectedClientsVM: SelectedClientsVM
        get() = context.relayVM.selectedClients
    private val selectedChild: ObservableField<ClientChildVM>?
        get() = selectedClientsVM.latest?.selectedChild


    override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
        onChildChange(selectedChild?.get())
    }

    override fun onLatestClientChanged() {
        selectedChild?.removeOnPropertyChangedCallback(this)
        onChildChange(selectedChild?.get())
        selectedChild?.addOnPropertyChangedCallback(this)
    }

    override fun bind() {
        selectedClientsVM.addOnClientsChangedCallback(this)
    }

    override fun unbind() {
        selectedClientsVM.removeOnClientsChangedCallback(this)
    }

    abstract fun onChildChange(clientChild: ClientChildVM?)
}