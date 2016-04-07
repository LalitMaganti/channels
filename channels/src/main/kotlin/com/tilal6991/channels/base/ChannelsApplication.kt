package com.tilal6991.channels.base

import android.app.Application
import android.util.Log
import com.squareup.leakcanary.LeakCanary
import com.tilal6991.channels.viewmodel.RelayVM
import timber.log.Timber

class ChannelsApplication : Application() {

    val relayHandle: RelayVM by lazy { RelayVM(this) }

    override fun onCreate() {
        super.onCreate()

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
