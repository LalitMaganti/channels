package co.fusionx.channels.inject

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module class RelayModule(private val context: Context) {
    @Singleton @Provides fun context(): Context = context.applicationContext
}