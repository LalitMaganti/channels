package co.fusionx.channels.base

import android.content.Context
import android.view.View
import co.fusionx.channels.inject.ChannelsObjectProvider
import co.fusionx.channels.inject.RelayModule
import co.fusionx.channels.presenter.Presenter
import co.fusionx.channels.viewmodel.persistent.RelayVM

val Context.app: ChannelsApplication
    get() = applicationContext as ChannelsApplication

val Context.relayVM: RelayVM
    get() = app.provider.relayViewModel()
val View.relayVM: RelayVM
    get() = context.relayVM
val Presenter.relayVM: RelayVM
    get() = activity.relayVM