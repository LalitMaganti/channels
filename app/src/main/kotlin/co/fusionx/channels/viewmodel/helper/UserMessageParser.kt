package co.fusionx.channels.viewmodel.helper

import co.fusionx.channels.viewmodel.persistent.ChannelVM
import co.fusionx.channels.viewmodel.persistent.ClientChildVM
import co.fusionx.channels.viewmodel.persistent.ServerVM
import co.fusionx.relay.protocol.ClientGenerator

public object UserMessageParser {
    fun parse(userMessage: String, context: ClientChildVM, serverVM: ServerVM): String? {
        if (context is ServerVM) {
            return parseServerMessage(userMessage, serverVM)
        } else if (context is ChannelVM) {
            return parse(userMessage, context, serverVM)
        }
        return null
    }

    fun parseServerMessage(userMessage: String, serverVM: ServerVM): String? {
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
        "/raw", "/quote" -> {
            server.sendRawLine(IRCUtils.concatenateStringList(parsedArray))
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

    fun parse(userMessage: String, context: ChannelVM, serverVM: ServerVM): String? {
        if (!userMessage.startsWith("/")) {
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
                return parseServerMessage(message, serverVM)
            }
        }

        return onUnknownEvent(message)
    }

    private fun onUnknownEvent(rawLine: String): String? {
        return null
    }
}