package co.fusionx.channels.presenter

import android.os.Bundle

public interface Presenter {
    val id: String

    public fun setup()
    public fun restoreState(bundle: Bundle)
    public fun bind()
    public fun unbind()
    public fun saveState(): Bundle
    public fun teardown()
}