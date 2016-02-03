package co.fusionx.channels.presenter

import android.databinding.ObservableField
import android.os.Bundle
import co.fusionx.channels.base.relayVM
import co.fusionx.channels.controller.MainActivity
import co.fusionx.channels.viewmodel.persistent.ClientChildVM
import co.fusionx.channels.viewmodel.persistent.ClientVM
import co.fusionx.channels.viewmodel.persistent.SelectedClientsVM

public interface Presenter {
    val activity: MainActivity
    val id: String

    val selectedClientsVM: SelectedClientsVM
        get() = activity.relayVM.selectedClients
    val selectedChild: ObservableField<ClientChildVM>?
        get() = selectedClientsVM.latest?.selectedChild

    public fun setup() = Unit
    public fun restoreState(bundle: Bundle) = Unit
    public fun bind() = Unit
    public fun unbind() = Unit
    public fun saveState(): Bundle = Bundle.EMPTY
    public fun teardown() = Unit

    public fun getString(id: Int): String {
        return activity.getString(id)
    }

    public fun getString(id: Int, vararg args: Any): String {
        return activity.getString(id, args)
    }

    public fun getQuantityString(id: Int, quantity: Int): String {
        return activity.resources.getQuantityString(id, quantity)
    }
}