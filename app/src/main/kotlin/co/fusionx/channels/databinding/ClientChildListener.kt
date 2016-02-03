package co.fusionx.channels.databinding

import android.content.Context
import android.databinding.Observable
import android.databinding.ObservableField
import co.fusionx.channels.base.relayVM
import co.fusionx.channels.viewmodel.persistent.ClientChildVM
import co.fusionx.channels.viewmodel.persistent.ClientVM
import co.fusionx.channels.viewmodel.persistent.SelectedClientsVM

public class ClientChildListener(private val context: Context,
                                 private val callback: () -> Unit) {
    private val selectedClientsVM: SelectedClientsVM
        get() = context.relayVM.selectedClient
    private val selectedChild: ObservableField<ClientChildVM>?
        get() = selectedClientsVM.latest?.selectedChild

    private val clientListener = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            selectedChild?.removeOnPropertyChangedCallback(childListener)
            callback()
            selectedChild?.addOnPropertyChangedCallback(childListener)
        }
    }

    private val childListener = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            callback()
        }
    }

    fun bind() {
        selectedClientsVM.addOnPropertyChangedCallback(clientListener)
    }

    fun unbind() {
        selectedClientsVM.addOnPropertyChangedCallback(clientListener)
    }
}