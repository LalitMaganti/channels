package com.tilal6991.channels.viewmodel.helper

import com.tilal6991.channels.viewmodel.ChannelVM
import com.tilal6991.channels.viewmodel.ClientChildVM
import com.tilal6991.channels.viewmodel.ServerVM
import com.tilal6991.relay.ClientGenerator

class UserMessageParser(private val listener: Listener) {

    fun parse(userMessage: String, context: ClientChildVM): String? {
        if (context is ServerVM) {
            return parseServerMessage(userMessage)
        } else if (context is ChannelVM) {
            return parse(userMessage, context)
        }
        return null
    }

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
        // TODO - deal with all the below.
        /*
        "/msg" -> if (arrayLength >= 1) {
            val nick = parsedArray.removeAt(0)
            val message = if (parsedArray.size >= 1)
                IRCUtils.concatenateStringList(parsedArray)
            else
                ""
            server.sendQuery(nick, message)
            return
        }
        "/whois" -> if (arrayLength == 1) {
            val nick = parsedArray.get(0)
            server.sendWhois(nick)
            return
        }
        "/ns" -> if (arrayLength > 1) {
            val message = IRCUtils.concatenateStringList(parsedArray)
            server.sendQuery("NickServ", message)
            return
        }
        else -> if (command.startsWith("/")) {
            server.sendRawLine(command.substring(1) + " "
                    + IRCUtils.concatenateStringList(parsedArray))
            return
        }
        */
        }
        return onUnknownEvent(message)
    }

    fun parse(userMessage: String, context: ChannelVM): String? {
        if (!userMessage.startsWith("/")) {
            listener.onChannelMessage(context, userMessage)
            return ClientGenerator.privmsg(context.name.toString(), userMessage)
        }

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
        // TODO(tilal6991) Deal with actions properly.
            "/me" -> if (message.length >= 0) {
                return ClientGenerator.privmsg(context.name.toString(), message)
            }
        // TODO(tilal6991) Deal with part reasons.
            "/part", "/p" -> if (message.length >= 0) {
                return ClientGenerator.part(message)
            }
        // TODO - deal with all the below.
        /*
        "/mode" -> if (arrayLength == 2) {
            channel.sendUserMode(parsedArray.get(0), parsedArray.get(1))
            return
        }
        "/kick" -> if (arrayLength >= 1) {
            val nick = parsedArray.removeAt(0)
            val reason = if (arrayLength >= 1)
                Optional.of(IRCUtils.concatenateStringList(parsedArray))
            else
                Optional.absent<Any>()
            channel.sendKick(nick, reason)
            return
        }
        "/topic" -> if (arrayLength >= 1) {
            val topic = IRCUtils.concatenateStringList(parsedArray)
            channel.sendTopic(topic)
            return
        }
        */
            else -> {
                return parseServerMessage(message)
            }
        }

        return onUnknownEvent(message)
    }

    private fun onUnknownEvent(rawLine: String): String? {
        return null
    }

    interface Listener {
        fun onChannelMessage(channelVM: ChannelVM, message: String)
    }
}