package co.fusionx.channels.databinding

import android.content.Context
import android.databinding.Observable
import android.databinding.ObservableField
import co.fusionx.channels.base.relayHost
import co.fusionx.channels.model.ClientChild
import co.fusionx.channels.model.Client

public class ClientChildListener(private val context: Context,
                                 private val callback: () -> Unit) {
    private val selectedClient: ObservableField<Client?>
        get() = context.relayHost.selectedClient
    private val selectedChild: ObservableField<ClientChild>?
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