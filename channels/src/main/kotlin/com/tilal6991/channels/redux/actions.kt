package com.tilal6991.channels.redux

import com.brianegan.bansa.Action
import com.tilal6991.channels.configuration.ChannelsConfiguration

sealed class Actions : Action {
    class SelectClient(val configuration: ChannelsConfiguration) : Actions()

    class ChangeSelectedChild(val type: Int, val position: Int) : Actions()

    class NewConfigurations(val configurations: List<ChannelsConfiguration>) : Actions()

    class RelayEvent(val configuration: ChannelsConfiguration,
                     val event: Events.Event) : Actions()
}