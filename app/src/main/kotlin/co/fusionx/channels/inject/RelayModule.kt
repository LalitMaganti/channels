package co.fusionx.channels.inject

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
public class RelayModule(private val context: Context) {
    @Singleton @Provides public fun context(): Context = context.applicationContext
}