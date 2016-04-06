package com.tilal6991.channels.ui

import android.databinding.Observable
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import com.tilal6991.channels.BR
import com.tilal6991.channels.R
import com.tilal6991.channels.adapter.DashboardAdapter
import com.tilal6991.channels.adapter.SectionAdapter
import com.tilal6991.channels.base.relayVM
import com.tilal6991.channels.ui.helper.ClientChildListener
import com.tilal6991.channels.util.failAssert
import com.tilal6991.channels.viewmodel.ChannelVM
import com.tilal6991.channels.viewmodel.ClientChildVM
import com.tilal6991.channels.viewmodel.ClientVM
import com.tilal6991.channels.viewmodel.ServerVM
import timber.log.Timber

class DashboardPresenter(override val context: MainActivity) : Presenter {
    override val id: String
        get() = "actions"

    private lateinit var dialog: BottomSheetDialog
    private lateinit var adapter: DashboardAdapter

    private var displayedClient: ClientVM? = null
    private var displayedChild: ClientChildVM? = null

    private val childListener = object : ClientChildListener(context) {
        override fun onChildChange(clientChild: ClientChildVM?) {
            onChildChanged(clientChild)
        }
    }
    private val statusListener = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            if (propertyId != BR.statusInt && propertyId != BR.active) {
                return
            } else if (displayedClient == null || displayedChild == null) {
                Timber.asTree().failAssert()
                return
            }
            updateAction(displayedClient!!, displayedChild!!)
        }

        fun bind() = selectedClientsVM.latest?.addOnPropertyChangedCallback(this)
        fun unbind() = selectedClientsVM.latest?.removeOnPropertyChangedCallback(this)
    }

    override fun setup(savedState: Bundle?) {
        val value = TypedValue()
        val theme: Int
        if (context.theme.resolveAttribute(R.attr.dashboardTheme, value, false)) {
            theme = value.resourceId
        } else {
            theme = R.style.Theme_Design_Light_BottomSheetDialog
        }

        dialog = BottomSheetDialog(context,theme)

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.dashboard_layout, null, false)

        val recycler = view.findViewById(R.id.actions_recycler) as RecyclerView
        val gridLayoutManager = GridLayoutManager(context, 3)
        recycler.layoutManager = gridLayoutManager

        adapter = DashboardAdapter(context) { onActionClicked(it) }
        recycler.adapter = adapter
        adapter.setup()

        gridLayoutManager.spanSizeLookup =
                adapter.getWrappedSpanSizeLookup(object : SectionAdapter.GridSpanSizeLookup {
                    override fun getItemSpanSize(section: Int, position: Int): Int {
                        return 1
                    }

                    override fun getHeaderSpanSize(section: Int): Int {
                        return gridLayoutManager.spanCount
                    }
                })

        dialog.setContentView(view)
    }

    override fun restoreState(bundle: Bundle) {
        val showing = bundle.getBoolean(SHOWING, false)
        if (showing) {
            dialog.show()
        }
    }

    override fun bind() {
        statusListener.bind()
        childListener.bind()

        onChildChanged(selectedChild?.get())
    }

    override fun unbind() {
        statusListener.unbind()
        childListener.unbind()
    }

    override fun saveState(): Bundle {
        val bundle = Bundle()
        bundle.putBoolean(SHOWING, dialog.isShowing)
        return bundle
    }

    fun toggle() {
        if (dialog.isShowing) {
            dialog.dismiss()
        } else {
            dialog.show()
        }
    }

    private fun onChildChanged(it: ClientChildVM?) {
        val latest = relayVM.selectedClients.latest
        displayedClient?.removeOnPropertyChangedCallback(statusListener)
        displayedClient = if (it == null) null else latest
        displayedChild = it
        displayedClient?.addOnPropertyChangedCallback(statusListener)

        if (it == null || latest == null) {
            adapter.setData(null, null, null)
            if (it != null) {
                // Inconsistent state of null client and non-null child.
                Timber.asTree().failAssert()
            }
            return
        }

        updateAction(latest, it)
    }

    private fun updateAction(client: ClientVM, child: ClientChildVM) {
        val status = client.statusInt

        if (child is ChannelVM) {
            updateChannelActions(child, status)
        } else if (child is ServerVM) {
            val (strings, drawables) = getServerActions(status)
            adapter.setData(AdapterData.serverTitles, arrayOf(strings), arrayOf(drawables))
        } else {
            Timber.d("Unknown client child encountered in the dashboard.")
        }
    }

    private fun getServerActions(status: Int): Pair<IntArray, IntArray> {
        val strings: IntArray
        val drawables: IntArray
        if (status == ClientVM.DISCONNECTED) {
            strings = AdapterData.serverDctStrings
            drawables = AdapterData.serverDctDrawables
        } else if (status == ClientVM.DISCONNECTING) {
            strings = AdapterData.serverDctingStrings
            drawables = AdapterData.serverDctingDrawables
        } else {
            strings = AdapterData.serverStrings
            drawables = AdapterData.serverDrawables
        }
        return strings to drawables
    }

    private fun updateChannelActions(channelVM: ChannelVM, status: Int) {
        val (serverStrings, serverDrawables) = getServerActions(status)

        val titles: IntArray
        val stringArray: Array<IntArray>
        val drawableArray: Array<IntArray>
        if (status == ClientVM.CONNECTED) {
            titles = AdapterData.channelTitles

            if (channelVM.active) {
                stringArray = arrayOf(AdapterData.channelStrings, serverStrings)
                drawableArray = arrayOf(AdapterData.channelDrawables, serverDrawables)
            } else {
                stringArray = arrayOf(AdapterData.channelStrings, serverStrings)
                drawableArray = arrayOf(AdapterData.channelDrawables, serverDrawables)
            }
        } else {
            titles = AdapterData.serverTitles
            stringArray = arrayOf(serverStrings)
            drawableArray = arrayOf(serverDrawables)
        }
        adapter.setData(titles, stringArray, drawableArray)
    }

    private fun onActionClicked(stringId: Int) {
        when (stringId) {
            R.string.disconnect -> relayVM.disconnectSelected()
            R.string.close -> relayVM.closeSelected()
            R.string.disconnect_close -> {
                relayVM.disconnectSelected()
                relayVM.closeSelected()
            }
            R.string.reconnect -> relayVM.reconnectSelected()
            R.string.part -> displayedClient?.partSelected()
        }
        dialog.dismiss()
    }

    object AdapterData {
        val serverTitles = intArrayOf(R.string.server_actions)
        val channelTitles = intArrayOf(R.string.channel_actions, R.string.server_actions)

        val serverDctingStrings = intArrayOf(R.string.close)
        val serverDctingDrawables = intArrayOf(R.drawable.ic_close)

        val serverDctStrings = intArrayOf(R.string.close, R.string.reconnect)
        val serverDctDrawables = intArrayOf(R.drawable.ic_close, R.drawable.ic_reconnect)

        val serverStrings = intArrayOf(R.string.disconnect, R.string.disconnect_close)
        val serverDrawables = intArrayOf(R.drawable.ic_disconnect, R.drawable.ic_disconnect_close)

        val channelStrings = intArrayOf(R.string.part, R.string.part_close)
        val channelDrawables = intArrayOf(R.drawable.ic_part, R.drawable.ic_part_close)
    }

    companion object {
        const val SHOWING = "showing"
    }
}