package co.fusionx.channels.viewmodel.persistent

import android.content.Context
import android.databinding.*
import co.fusionx.channels.BR
import co.fusionx.channels.databinding.ObservableSortedArrayMap
import co.fusionx.channels.model.Channel
import co.fusionx.channels.model.Client
import co.fusionx.channels.model.ClientChild
import co.fusionx.channels.util.charSequenceComparator
import co.fusionx.channels.util.compareTo
import co.fusionx.channels.util.failAssert
import co.fusionx.channels.viewmodel.helper.ChannelComparator
import co.fusionx.channels.viewmodel.helper.UserMessageParser
import timber.log.Timber

public class ClientVM(private val context: Context,
                      private val client: Client) : BaseObservable() {
    public val name: CharSequence
        get() = client.name
    public val hostname: CharSequence
        get() = client.configuration.connectionConfiguration.hostname

    public val status: ObservableField<String> = ObservableField()
    public val isActive: Boolean
        @Bindable get() = client.status.value != Client.STOPPED

    public val selectedChild: ObservableField<ClientChildVM>
    public val server: ServerVM
    public val channels: ObservableList<ChannelVM>

    private val channelMap: ObservableSortedArrayMap<CharSequence, ChannelVM>

    init {
        server = ServerVM(client.server)
        channelMap = ObservableSortedArrayMap(charSequenceComparator, ChannelComparator())

        selectedChild = ObservableField(server)
        channels = channelMap.valuesAsObservableList()

        client.status.subscribe {
            status.set(context.getString(it))
            notifyPropertyChanged(BR.active)
        }
        client.channels.addOnMapChangedCallback(ObservableMapObserver())
    }

    fun sendUserMessage(userMessage: String, context: ClientChildVM) {
        val message = UserMessageParser.parse(userMessage, context, server) ?: return
        client.send(message)
    }

    public fun select(child: ClientChildVM) {
        selectedChild.set(child)
    }

    public fun onSelected(): Boolean {
        val newConnect = client.startIfStopped()
        selectedChild.set(server)
        return newConnect
    }

    private inner class ObservableMapObserver : ObservableMap.OnMapChangedCallback<ObservableMap<String, Channel>, String, Channel>() {
        override fun onMapChanged(sender: ObservableMap<String, Channel>, key: String?) {
            // TODO(tilla6991) figure out if this needs to be handled.
            if (key == null) {
                return
            }

            val channel = sender[key]
            if (channel == null) {
                channelMap.remove(key)
            } else {
                val channelVM = channelMap[key]
                if (channelVM == null) {
                    channelMap.put(key, ChannelVM(channel))
                } else {
                    // TODO(tilla6991) figure out if this needs to be handled.
                    Timber.asTree().failAssert()
                }
            }
        }
    }
}