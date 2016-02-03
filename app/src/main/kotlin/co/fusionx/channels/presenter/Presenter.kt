package co.fusionx.channels.presenter

import android.databinding.ObservableField
import android.os.Bundle
import co.fusionx.channels.base.relayVM
import co.fusionx.channels.controller.MainActivity
import co.fusionx.channels.viewmodel.ClientChildVM
import co.fusionx.channels.viewmodel.ClientVM

public interface Presenter {
    val activity: MainActivity
    val id: String

    val selectedClient: ObservableField<ClientVM?>
        get() = activity.relayVM.selectedClient
    val selectedChild: ObservableField<ClientChildVM>?
        get() = selectedClient.get()?.selectedChild

    public fun setup() = Unit
    public fun restoreState(bundle: Bundle) = Unit
    public fun bind() = Unit
    public fun unbind() = Unit
    public fun saveState(): Bundle = Bundle.EMPTY
    public fun teardown() = Unit
}