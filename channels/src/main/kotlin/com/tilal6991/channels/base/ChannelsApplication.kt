package com.tilal6991.channels.base

import android.app.Application
import com.brianegan.bansa.BaseStore
import com.tilal6991.channels.db.ConnectionDatabase
import com.tilal6991.channels.redux.Actions
import com.tilal6991.channels.redux.initialState
import com.tilal6991.channels.redux.reducer
import com.tilal6991.channels.redux.relayMiddleware
import com.tilal6991.channels.viewmodel.RelayVM
import rx.android.schedulers.AndroidSchedulers
import timber.log.Timber

class ChannelsApplication : Application() {

    private val relayMiddleware = relayMiddleware(this)
    val store = BaseStore(initialState, reducer, relayMiddleware)
    val relayHandle: RelayVM by lazy { RelayVM(this) }

    override fun onCreate() {
        super.onCreate()

        val dbSubs = ConnectionDatabase.instance(this)
                .getConfigurations()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { store.dispatch(Actions.NewConfigurations(it)) }

        // LeakCanary.install(this)
        Timber.plant(Timber.DebugTree())
    }
}
