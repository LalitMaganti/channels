package com.tilal6991.channels.presenter.helper

import android.content.Context
import com.tilal6991.channels.base.relayVM
import com.tilal6991.channels.presenter.Bindable
import com.tilal6991.channels.viewmodel.SelectedClientsVM

abstract class ClientListener(private val context: Context) : SelectedClientsVM.OnLatestClientChangedCallback, Bindable {

    override fun bind() {
        context.relayVM.selectedClients.addOnClientsChangedCallback(this)
    }

    override fun unbind() {
        context.relayVM.selectedClients.removeOnClientsChangedCallback(this)
    }
}