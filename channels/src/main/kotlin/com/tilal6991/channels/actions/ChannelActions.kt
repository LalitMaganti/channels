package com.tilal6991.channels.actions

sealed class ChannelActions {
    object ADD_USER : ChannelActions()
    object REMOVE_USER : ChannelActions()
    object NICK_CHANGE : ChannelActions()
}