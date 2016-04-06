package com.tilal6991.channels.inject

import com.tilal6991.channels.viewmodel.RelayVM
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
        RelayModule::class
)) interface ChannelsObjectProvider {
    fun relayViewModel(): RelayVM
}