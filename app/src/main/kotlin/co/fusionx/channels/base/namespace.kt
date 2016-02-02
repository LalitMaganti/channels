package co.fusionx.channels.base

import android.content.Context
import android.view.View
import co.fusionx.channels.inject.ChannelsObjectProvider
import co.fusionx.channels.presenter.Presenter
import co.fusionx.channels.relay.RelayHost

public val Context.app: ChannelsApplication
    get() = applicationContext as ChannelsApplication

public val Context.relayHost: RelayHost
    get() = app.provider.relayHost()
public val View.relayHost: RelayHost
    get() = context.relayHost
public val Presenter.relayHost: RelayHost
    get() = activity.relayHost