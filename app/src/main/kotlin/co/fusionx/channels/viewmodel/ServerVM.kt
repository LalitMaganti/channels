package co.fusionx.channels.viewmodel

import co.fusionx.channels.model.ClientChild
import co.fusionx.channels.model.Server

public class ServerVM(private val server: Server) : ClientChildVM(server) {
}