package co.fusionx.channels.viewmodel.listener

import co.fusionx.channels.viewmodel.persistent.ServerVM
import co.fusionx.relay.EventListener

class ServerDelegatingListener(private val serverVM: ServerVM) : EventListener {

    override fun onSocketConnect() {
        serverVM.onSocketConnect()
    }

    override fun onOtherCode(code: Int, arguments: List<String>) {
        serverVM.onOtherCode(code, arguments)
    }

    override fun onWelcome(target: String, text: String) {
        serverVM.onWelcome(target, text)
    }
}