package com.tilal6991.channels.redux.util

import com.tilal6991.relay.ClientGenerator

object UserMessageParser {

    fun parseServerMessage(userMessage: String): String? {
        var message = userMessage
        val spaceIndex = userMessage.indexOf(' ')
        val command: String
        if (spaceIndex == -1) {
            command = userMessage
        } else {
            command = userMessage.substring(0, spaceIndex)
            message = userMessage.substring(spaceIndex + 1)
        }

        when (command) {
            "/join", "/j" -> if (message.length >= 1) {
                return ClientGenerator.join(message)
            }
            "/nick" -> if (message.length >= 1) {
                return ClientGenerator.nick(message)
            }
            "/raw", "/quote" -> if (message.length >= 1) {
                return message
            }
        }
        return onUnknownEvent(message)
    }

    private fun onUnknownEvent(rawLine: String): String? {
        return null
    }
}