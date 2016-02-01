package co.fusionx.channels.view

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.support.design.internal.ScrimInsetsFrameLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import butterknife.bindView
import co.fusionx.channels.R
import co.fusionx.channels.adapter.NavigationAdapter
import co.fusionx.channels.relay.ClientChild
import co.fusionx.channels.relay.ClientHost
import timber.log.Timber
import kotlin.properties.Delegates

public class NavigationDrawerView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null) : ScrimInsetsFrameLayout(context, attrs) {

    internal var callbacks by Delegates.notNull<Callbacks>()

    private val recycler: RecyclerView by bindView(R.id.navdrawer_recycler)
    private var adapter: NavigationAdapter by Delegates.notNull()

    public override fun onFinishInflate() {
        super.onFinishInflate()

        recycler.layoutManager = LinearLayoutManager(context)
        if (isInEditMode) return

        adapter = NavigationAdapter(context, { callbacks.onClientClick(it) }) {
            callbacks.onChildClick(it)
        }
        recycler.adapter = adapter
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        adapter.startObserving()
        Timber.v("NavigationDrawerView attached to window")
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        adapter.stopObserving()
        Timber.v("NavigationDrawerView detached from window")
    }

    public override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable(SUPER_STATE, super.onSaveInstanceState())
        bundle.putParcelable(PARCEL_ADAPTER_STATE, adapter.onSaveInstanceState())
        Timber.v("NavigationDrawerView saving state")
        return bundle
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        val superState: Parcelable
        if (state is Bundle) {
            superState = state.getParcelable(SUPER_STATE)
            adapter.onRestoreInstanceState(state.getParcelable<Parcelable>(PARCEL_ADAPTER_STATE))
        } else {
            superState = state
        }
        Timber.v("NavigationDrawerView restoring state")
        super.onRestoreInstanceState(superState)
    }

    interface Callbacks {
        public fun onClientClick(client: ClientHost)
        public fun onChildClick(child: ClientChild)
    }

    companion object {
        private val PARCEL_ADAPTER_STATE = "adapter_state"
    }
}