package co.fusionx.channels.viewmodel.persistent

import android.databinding.ObservableField
import android.databinding.ObservableInt
import android.databinding.ObservableList
import android.databinding.ObservableMap
import co.fusionx.channels.databinding.ObservableSortedArrayMap
import co.fusionx.channels.model.Channel
import co.fusionx.channels.model.Client
import co.fusionx.channels.util.charSequenceComparator
import co.fusionx.channels.util.compareTo
import timber.log.Timber

public class ClientVM(private val client: Client) {
    public val name: CharSequence
        get() = client.name
    public val status: ObservableInt = ObservableInt()

    public val selectedChild: ObservableField<ClientChildVM>
    public val children: ObservableList<ClientChildVM>

    private val server: ServerVM
    private val channels: ObservableSortedArrayMap<CharSequence, ChannelVM>

    private val isActive: Boolean
        get() = status.get() != Client.STOPPED

    init {
        server = ServerVM(client.server)
        channels = ObservableSortedArrayMap(charSequenceComparator, ChannelComparator())

        selectedChild = ObservableField(server)
        children = ClientChildVMList(server, channels)

        client.status.subscribe { status.set(it) }
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
        if (isActive == o2.isActive) {
            return client.name.compareTo(o2.client.name)
        } else if (isActive) {
            return -1
        }
        return 1
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
                channels.remove(key)
            } else {
                val channelVM = channels[key]
                if (channelVM == null) {
                    channels.put(key, ChannelVM(channel))
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