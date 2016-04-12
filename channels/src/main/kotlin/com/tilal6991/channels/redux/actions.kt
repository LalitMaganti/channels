package com.tilal6991.channels.redux

import com.tilal6991.channels.configuration.ChannelsConfiguration

sealed class Action {
    class SelectClient(val configuration: ChannelsConfiguration) : Action()

    class ChangeNavigationType() : Action()

    class ChangeSelectedChild(val type: Int, val position: Int) : Action()

    class NewConfigurations(val configurations: List<ChannelsConfiguration>) : Action()

    class RelayEvent(val configuration: ChannelsConfiguration,
                     val event: Events.Event) : Action()
}