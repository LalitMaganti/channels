package co.fusionx.channels.presenter

import android.databinding.ObservableField
import android.os.Bundle
import co.fusionx.channels.base.relayHost
import co.fusionx.channels.controller.MainActivity
import co.fusionx.channels.model.ClientChild
import co.fusionx.channels.model.Client

public interface Presenter {
    val activity: MainActivity
    val id: String

    val selectedClient: ObservableField<Client?>
        get() = activity.relayHost.selectedClient
    val selectedChild: ObservableField<ClientChild>?
        get() = selectedClient.get()?.selectedChild

    public fun setup() = Unit
    public fun restoreState(bundle: Bundle) = Unit
    public fun bind() = Unit
    public fun unbind() = Unit
    public fun saveState(): Bundle = Bundle.EMPTY
    public fun teardown() = Unit
}