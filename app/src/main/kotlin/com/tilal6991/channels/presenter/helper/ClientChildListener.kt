package com.tilal6991.channels.presenter.helper

import android.content.Context
import android.databinding.Observable
import android.databinding.ObservableField
import com.tilal6991.channels.base.relayVM
import com.tilal6991.channels.presenter.Bindable
import com.tilal6991.channels.viewmodel.ClientChildVM
import com.tilal6991.channels.viewmodel.SelectedClientsVM

class ClientChildListener(private val context: Context,
                          private val callback: (ClientChildVM?) -> Unit) : Bindable {
    private val selectedClientsVM: SelectedClientsVM
        get() = context.relayVM.selectedClients
    private val selectedChild: ObservableField<ClientChildVM>?
        get() = selectedClientsVM.latest?.selectedChild

    private val clientListener = object : SelectedClientsVM.OnLatestClientChangedCallback {
        override fun onLatestClientChanged() {
            selectedChild?.removeOnPropertyChangedCallback(childListener)
            callback(selectedChild?.get())
            selectedChild?.addOnPropertyChangedCallback(childListener)
        }
    }

    private val childListener = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            callback(selectedChild?.get())
        }
    }

    override fun bind() {
        selectedClientsVM.addOnClientsChangedCallback(clientListener)
    }

    override fun unbind() {
        selectedClientsVM.removeOnClientsChangedCallback(clientListener)
    }
}