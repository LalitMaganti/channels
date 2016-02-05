package co.fusionx.channels.inject

import co.fusionx.channels.viewmodel.persistent.RelayVM
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
        RelayModule::class
)) interface ChannelsObjectProvider {
    fun relayViewModel(): RelayVM
}