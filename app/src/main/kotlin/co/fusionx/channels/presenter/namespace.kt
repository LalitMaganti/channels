package co.fusionx.channels.presenter

import android.databinding.ObservableField
import android.os.Bundle
import co.fusionx.channels.base.relayVM
import co.fusionx.channels.controller.MainActivity
import co.fusionx.channels.viewmodel.persistent.ClientChildVM
import co.fusionx.channels.viewmodel.persistent.ClientVM
import co.fusionx.channels.viewmodel.persistent.SelectedClientsVM

interface Bindable {
    fun setup() = Unit
    fun bind() = Unit
    fun unbind() = Unit
    fun teardown() = Unit
}

interface Presenter : Bindable {
    val activity: MainActivity
    val id: String

    val selectedClientsVM: SelectedClientsVM
        get() = activity.relayVM.selectedClients
    val selectedChild: ObservableField<ClientChildVM>?
        get() = selectedClientsVM.latest?.selectedChild

    fun restoreState(bundle: Bundle) = Unit
    fun saveState(): Bundle = Bundle.EMPTY

    fun getString(id: Int): String {
        return activity.getString(id)
    }

    fun getString(id: Int, vararg args: Any): String {
        return activity.getString(id, args)
    }

    fun getQuantityString(id: Int, quantity: Int): String {
        return activity.resources.getQuantityString(id, quantity)
    }
}