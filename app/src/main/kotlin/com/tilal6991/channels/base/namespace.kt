package com.tilal6991.channels.base

import android.content.Context
import com.tilal6991.channels.presenter.Presenter
import com.tilal6991.channels.viewmodel.RelayVM

val Context.app: ChannelsApplication
    get() = applicationContext as ChannelsApplication

val Context.relayVM: RelayVM
    get() = app.provider.relayViewModel()
val Presenter.relayVM: RelayVM
    get() = activity.relayVM