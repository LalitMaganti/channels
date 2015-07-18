package co.fusionx.channels.base

import android.content.Context
import android.view.View
import co.fusionx.channels.inject.ChannelsObjectProvider
import co.fusionx.channels.relay.RelayHost

public val Context.app: ChannelsApplication
    get() = applicationContext as ChannelsApplication
public val View.app: ChannelsApplication
    get() = context.app

public val Context.objectProvider: ChannelsObjectProvider
    get() = app.provider
public val View.objectProvider: ChannelsObjectProvider
    get() = app.provider

public val Context.relayHost: RelayHost
    get() = objectProvider.relayHost()
public val View.relayHost: RelayHost
    get() = objectProvider.relayHost()