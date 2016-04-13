package com.tilal6991.channels.base

import android.app.Application
import android.content.Context
import android.util.Log
import com.squareup.leakcanary.LeakCanary
import com.tilal6991.channels.db.ConnectionDatabase
import com.tilal6991.channels.redux.Action
import com.tilal6991.channels.redux.bansa.Store
import com.tilal6991.channels.redux.bansa.applyMiddleware
import com.tilal6991.channels.redux.bansa.createStore
import com.tilal6991.channels.redux.initialState
import com.tilal6991.channels.redux.reducer
import com.tilal6991.channels.redux.relayMiddleware
import com.tilal6991.channels.redux.state.GlobalState
import com.tilal6991.channels.viewmodel.RelayVM
import rx.android.schedulers.AndroidSchedulers
import timber.log.Timber

class ChannelsApplication : Application() {

    private val function = relayMiddleware(this as Context)
    val store = (applyMiddleware(function))(createStore(initialState, reducer))
    val relayHandle: RelayVM by lazy { RelayVM(this) }

    override fun onCreate() {
        super.onCreate()

        val dbSubs = ConnectionDatabase.instance(this)
                .getConfigurations()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { store.dispatch(Action.NewConfigurations(it)) }

        LeakCanary.install(this)
        Timber.plant(Timber.DebugTree())
    }
}
