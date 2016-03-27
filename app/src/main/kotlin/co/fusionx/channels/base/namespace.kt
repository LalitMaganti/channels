package co.fusionx.channels.base

import android.content.Context
import co.fusionx.channels.presenter.Presenter
import co.fusionx.channels.viewmodel.RelayVM

val Context.app: ChannelsApplication
    get() = applicationContext as ChannelsApplication

val Context.relayVM: RelayVM
    get() = app.provider.relayViewModel()
val Presenter.relayVM: RelayVM
    get() = activity.relayVM