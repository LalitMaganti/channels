package co.fusionx.channels.base

import android.content.Context
import android.view.View
import co.fusionx.channels.inject.ChannelsObjectProvider
import co.fusionx.channels.inject.RelayModule
import co.fusionx.channels.presenter.Presenter
import co.fusionx.channels.viewmodel.persistent.RelayVM

public val Context.app: ChannelsApplication
    get() = applicationContext as ChannelsApplication

public val Context.relayVM: RelayVM
    get() = app.provider.relayViewModel()
public val View.relayVM: RelayVM
    get() = context.relayVM
public val Presenter.relayVM: RelayVM
    get() = activity.relayVM