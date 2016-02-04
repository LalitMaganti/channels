package co.fusionx.channels.viewmodel.persistent

import android.content.Context
import android.databinding.*
import co.fusionx.channels.BR
import co.fusionx.channels.databinding.ObservableSortedArrayMap
import co.fusionx.channels.model.Channel
import co.fusionx.channels.model.Client
import co.fusionx.channels.util.charSequenceComparator
import co.fusionx.channels.util.compareTo
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

    public fun select(child: ClientChildVM) {
        if (selectedChild == child) return
        selectedChild.set(child)
    }

    fun onSelected(): Boolean {
        val newConnect = client.startIfStopped()
        selectedChild.set(server)
        return newConnect
    }

    fun areItemsTheSame(item2: ClientVM): Boolean {
        return client.name == item2.client.name
    }

    fun areContentsTheSame(newItem: ClientVM): Boolean {
        return client.name == newItem.client.name
    }

    fun compareTo(o2: ClientVM): Int {
        return client.name.compareTo(o2.client.name)
    }

    private inner class ObservableMapObserver :
            ObservableMap.OnMapChangedCallback<ObservableMap<String, Channel>, String, Channel>() {
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
                    Timber.d("This case should not occur")
                }
            }
        }
    }

    private class ChannelComparator : ObservableSortedArrayMap.HyperComparator<ChannelVM> {
        override fun areItemsTheSame(item1: ChannelVM, item2: ChannelVM): Boolean {
            return item1.name == item2.name
        }

        override fun areContentsTheSame(oldItem: ChannelVM, newItem: ChannelVM): Boolean {
            return oldItem.name == newItem.name
        }
    }
}