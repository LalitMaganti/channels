package co.fusionx.channels.viewmodel.listener

import android.content.Context
import android.databinding.BaseObservable
import android.os.Handler
import android.os.Looper
import co.fusionx.channels.BR
import co.fusionx.channels.R
import co.fusionx.channels.relay.Configuration
import co.fusionx.channels.viewmodel.persistent.UserVM
import co.fusionx.relay.EventListener

class ClientStateListener(private val context: Context,
                          private val configuration: Configuration,
                          private val observable: BaseObservable) : EventListener {

    var user: UserVM = UserVM("tilal6993")
    var status: String = context.getString(STOPPED)
    val isActive: Boolean
        get() = _status != STOPPED

    private var _status: Int = STOPPED
        set(it) {
            field = it
            status = context.getString(it)

            observable.notifyPropertyChanged(BR.status)
            observable.notifyPropertyChanged(BR.active)
        }

    fun onSelected(): Boolean {
        if (_status == STOPPED) {
            _status = CONNECTING
            return false
        }
        return true
    }

    override fun onSocketConnect() {
        _status = SOCKET_CONNECTED
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