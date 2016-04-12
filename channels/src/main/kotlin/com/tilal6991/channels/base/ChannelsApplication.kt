package com.tilal6991.channels.base

import android.app.Application
import android.util.Log
import com.squareup.leakcanary.LeakCanary
import com.tilal6991.channels.db.ConnectionDatabase
import com.tilal6991.channels.redux.Action
import com.tilal6991.channels.redux.store
import com.tilal6991.channels.viewmodel.RelayVM
import rx.android.schedulers.AndroidSchedulers
import timber.log.Timber

class ChannelsApplication : Application() {

    val relayHandle: RelayVM by lazy { RelayVM(this) }

    override fun onCreate() {
        super.onCreate()

        val dbSubs = ConnectionDatabase.instance(this)
                .getConfigurations()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { store.dispatch(Action.NewConfigurations(it)) }

        LeakCanary.install(this)
        Timber.plant(object : Timber.DebugTree() {
            override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
                if (priority == Log.ERROR) {
                    if (t != null) {
                        throw RuntimeException(t)
                    } else {
                        throw IllegalStateException(message)
                    }
                }
                super.log(priority, tag, message, t)
            }
        })
    }
}
