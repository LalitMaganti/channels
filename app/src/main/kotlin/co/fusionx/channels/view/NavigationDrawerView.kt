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
import kotlin.properties.Delegates

public class NavigationDrawerView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null) : ScrimInsetsFrameLayout(context, attrs) {

    internal var callbacks by Delegates.notNull<Callbacks>()

    private val recycler: RecyclerView by bindView(R.id.navdrawer_recycler)
    private val emptyRecyclerLayout: EmptyViewRecyclerViewLayout
            by bindView(R.id.empty_recycler_parent)
    private var adapter: NavigationAdapter by Delegates.notNull()

    public override fun onFinishInflate() {
        super.onFinishInflate()

        recycler.layoutManager = LinearLayoutManager(context)

        adapter = NavigationAdapter(context, { callbacks.onClientClick(it) }) {
            callbacks.onChildClick(it)
        }
        emptyRecyclerLayout.setRecyclerAdapter(adapter)
    }

    public override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable(SUPER_STATE, super.onSaveInstanceState())
        bundle.putInt(STATE_CURRENT_TYPE, adapter.currentType)
        return bundle
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        val superState: Parcelable
        if (state is Bundle) {
            superState = state.getParcelable(SUPER_STATE)
            adapter.updateCurrentType(state.getInt(STATE_CURRENT_TYPE, -1))
        } else {
            superState = state
        }
        super.onRestoreInstanceState(superState)
    }

    internal fun switchToChildList() {
        adapter.updateCurrentType(NavigationAdapter.VIEW_TYPE_CHILD)
    }

    internal fun switchToClientList() {
        adapter.updateCurrentType(NavigationAdapter.VIEW_TYPE_CLIENT)
    }

    interface Callbacks {
        public fun onClientClick(client: ClientHost)
        public fun onChildClick(child: ClientChild)
    }

    companion object {
        private val STATE_CURRENT_TYPE = "current_type"
    }
}