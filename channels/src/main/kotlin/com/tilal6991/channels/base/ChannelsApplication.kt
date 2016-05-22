package com.tilal6991.channels.base

import android.app.Application
import com.brianegan.bansa.BaseStore
import com.tilal6991.channels.db.ConnectionDatabase
import com.tilal6991.channels.redux.*
import com.tilal6991.channels.viewmodel.RelayVM
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import timber.log.Timber

class ChannelsApplication : Application() {

    private val relayMiddleware = relayMiddleware(this)
    val store = RxStore(BaseStore(initialState, reducer, relayMiddleware), Schedulers.immediate())
    val relayHandle: RelayVM by lazy { RelayVM(this) }

    val state = store.observable()
            // .onBackpressureDrop()
            // .observeOn(AndroidSchedulers.mainThread())
            .share()

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
