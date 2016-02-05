package co.fusionx.channels.model

import android.databinding.BaseObservable
import android.databinding.ObservableArrayMap
import android.os.Handler
import android.os.Looper
import co.fusionx.channels.R
import co.fusionx.channels.model.helper.BasicEventListener
import co.fusionx.relay.EventListener
import co.fusionx.relay.RelayClient
import co.fusionx.relay.message.AndroidMessageLoop
import co.fusionx.relay.util.PrefixExtractor
import co.fusionx.relay.util.isChannel
import rx.subjects.BehaviorSubject

class Client(
        val configuration: Configuration) : BaseObservable() {
    val name: CharSequence
        get() = configuration.name
    var server: Server = Server("Server")
    val channels: ObservableArrayMap<String, Channel> = ObservableArrayMap()

    private val client: RelayClient = RelayClient.create(configuration.connectionConfiguration,
            AndroidMessageLoop.create())
    private var statusEnum: Int = STOPPED
        set(it) {
            field = it
            status.onNext(it)
        }

    // Bindable properties.
    // TODO(tilal6991) Fix this to do the correct thing.
    val nick: BehaviorSubject<String> = BehaviorSubject.create("tilal6993")
    val status: BehaviorSubject<Int> = BehaviorSubject.create(statusEnum)

    init {
        client.addEventListener(DispatchingEventListener())
        client.addEventListener(BasicEventListener(client))
    }

    fun startIfStopped(): Boolean {
        val stopped = statusEnum == Client.STOPPED
        if (stopped) {
            statusEnum = CONNECTING
            client.start()
        }
        return stopped
    }

    fun send(message: String) {
        client.send(message)
    }

    private inner class DispatchingEventListener : EventListener {
        private val handler: Handler = Handler(Looper.getMainLooper())

        override fun onSocketConnect() {
            handler.post {
                statusEnum = SOCKET_CONNECTED
                server.onSocketConnect()
            }
        }

        override fun onNames(channelName: String, nickList: List<String>) {
            handler.post {
                channels[channelName]?.onNames(nickList) ?: return@post
            }
        }

        override fun onJoin(prefix: String, channel: String) {
            handler.post {
                val c: Channel
                if (PrefixExtractor.nick(prefix) == nick.value) {
                    c = Channel(channel)
                    channels.put(channel, c)
                } else {
                    c = channels[channel] ?: return@post
                }
                c.onJoin(prefix)
            }
        }

        override fun onOtherCode(code: Int, arguments: List<String>) {
            handler.post {
                server.onOtherCode(code, arguments)
            }
        }

        override fun onWelcome(target: String, text: String) {
            handler.post {
                statusEnum = CONNECTED
                server.onWelcome(target, text)

                nick.onNext(target)
            }
        }

        override fun onPrivmsg(prefix: String, target: String, message: String) {
            handler.post {
                if (target.isChannel()) {
                    channels[target]?.onPrivmsg(prefix, message) ?: return@post
                }
                // TODO(tilal6991) handle the private message case
            }
        }
    }

    companion object {
        const val STOPPED: Int = R.string.status_stopped
        const val CONNECTING: Int = R.string.status_connecting
        const val SOCKET_CONNECTED: Int = R.string.status_socket_connected
        const val CONNECTED: Int = R.string.status_connected
        const val RECONNECTING: Int = R.string.status_reconnecting
        const val DISCONNECTED: Int = R.string.status_disconnected
    }
}