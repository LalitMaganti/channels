package co.fusionx.channels.viewmodel.persistent

import android.databinding.BaseObservable

public class SelectedClientsVM : BaseObservable() {
    public var latest: ClientVM? = null
        private set
    public var penultimate: ClientVM? = null
        private set
    public var antepenultimate: ClientVM? = null
        private set

    fun select(client: ClientVM) {
        antepenultimate = penultimate
        penultimate = latest
        latest = client

        notifyChange()
    }

    public fun selectPenultimate() {
        var oldLatest = latest
        latest = penultimate
        penultimate = oldLatest

        notifyChange()
    }

    public fun selectAntePenultimate() {
        var oldLatest = latest
        latest = antepenultimate
        antepenultimate = oldLatest

        notifyChange()
    }
}