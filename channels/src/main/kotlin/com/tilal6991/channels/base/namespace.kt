package com.tilal6991.channels.base

import android.content.Context
import android.view.View
import com.tilal6991.channels.redux.Action
import com.tilal6991.channels.redux.bansa.Store
import com.tilal6991.channels.redux.state.GlobalState
import com.tilal6991.channels.ui.Presenter
import com.tilal6991.channels.viewmodel.RelayVM

val Context.app: ChannelsApplication
    get() = applicationContext as ChannelsApplication

val Context.relayVM: RelayVM
    get() = app.relayHandle
val Presenter.relayVM: RelayVM
    get() = context.relayVM

val Context.store: Store<GlobalState, Action>
    get() = app.store
val View.store: Store<GlobalState, Action>
    get() = context.store