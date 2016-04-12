package com.tilal6991.channels.redux

import com.tilal6991.channels.configuration.ChannelsConfiguration

sealed class Action {
    class SelectClient(val configuration: ChannelsConfiguration) : Action()

    class Welcome(val configuration: ChannelsConfiguration,
                  val target: String,
                  val message: String) : Action()

    class ChangeNavigationType() : Action()

    class ChangeSelectedChild(val type: Int, val position: Int) : Action()

    class NewConfigurations(val configurations: List<ChannelsConfiguration>) : Action()
}