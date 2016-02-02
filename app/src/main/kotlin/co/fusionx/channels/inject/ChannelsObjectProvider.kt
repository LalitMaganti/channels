package co.fusionx.channels.inject

import co.fusionx.channels.model.RelayHost
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = arrayOf(
        RelayModule::class
))
public interface ChannelsObjectProvider {
    public fun relayHost(): RelayHost
}