package co.fusionx.channels.viewmodel.persistent

import android.util.ArraySet

public class SelectedClientsVM {
    public var latest: ClientVM? = null
        private set
    public var penultimate: ClientVM? = null
        private set
    public var antepenultimate: ClientVM? = null
        private set

    private val callbacks: MutableCollection<OnClientsChangedCallback> = ArraySet<OnClientsChangedCallback>()

    fun select(client: ClientVM) {
        if (client == latest) {
            return
        } else if (client == penultimate) {
            return selectPenultimate()
        } else if (client == antepenultimate) {
            return selectAntePenultimate()
        }

        antepenultimate = penultimate
        penultimate = latest
        latest = client

        callbacks.forEach { it.onNewClientAdded() }
    }

    public fun selectPenultimate() {
        var oldLatest = latest
        latest = penultimate
        penultimate = oldLatest

        callbacks.forEach { it.onLatestPenultimateSwap() }
    }

    public fun selectAntePenultimate() {
        var oldLatest = latest
        latest = antepenultimate
        antepenultimate = oldLatest

        callbacks.forEach { it.onLatestAntePenultimateSwap() }
    }

    public fun addOnClientsChangedCallback(callback: OnClientsChangedCallback) {
        callbacks.add(callback)
    }

    public fun removeOnClientsChangedCallback(callback: OnClientsChangedCallback) {
        callbacks.remove(callback)
    }

    public interface OnClientsChangedCallback {
        public fun onNewClientAdded()
        public fun onLatestPenultimateSwap()
        public fun onLatestAntePenultimateSwap()
    }

    public interface OnLatestClientChangedCallback : OnClientsChangedCallback {
        public override fun onNewClientAdded() {
            onLatestClientChanged()
        }

        public override fun onLatestPenultimateSwap() {
            onLatestClientChanged()
        }

        public override fun onLatestAntePenultimateSwap() {
            onLatestClientChanged()
        }

        public fun onLatestClientChanged()
    }
}