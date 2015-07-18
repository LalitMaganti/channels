package co.fusionx.channels.relay

import co.fusionx.channels.observable.ObservableList
import co.fusionx.relay.EventListener
import java.util.*

class ServerHost(override val name: CharSequence) : ClientChild(), EventListener {
    override val buffer: ObservableList<CharSequence> = ObservableList(ArrayList())

    override fun onSocketConnect() = add("Connection was successful.")
    override fun onGenericCode(code: Int, text: String): Unit = add("$code: $text")
    override fun onWelcome(text: String) = add(text)
}