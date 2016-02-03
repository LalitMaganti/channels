package co.fusionx.channels.databinding

import android.content.Context
import android.databinding.Observable
import android.databinding.ObservableField
import co.fusionx.channels.base.relayVM
import co.fusionx.channels.viewmodel.persistent.ClientChildVM
import co.fusionx.channels.viewmodel.persistent.ClientVM

public class ClientChildListener(private val context: Context,
                                 private val callback: () -> Unit) {
    private val selectedClient: ObservableField<ClientVM?>
        get() = context.relayVM.selectedClient
    private val selectedChild: ObservableField<ClientChildVM>?
        get() = selectedClient.get()?.selectedChild

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
        selectedClient.addOnPropertyChangedCallback(clientListener)
    }

    fun unbind() {
        selectedClient.addOnPropertyChangedCallback(clientListener)
    }
}