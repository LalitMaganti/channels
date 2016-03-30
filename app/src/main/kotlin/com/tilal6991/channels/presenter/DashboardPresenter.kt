package com.tilal6991.channels.presenter

import android.content.Context
import android.databinding.Observable
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.tilal6991.channels.BR
import com.tilal6991.channels.R
import com.tilal6991.channels.activity.MainActivity
import com.tilal6991.channels.adapter.HeaderViewHolder
import com.tilal6991.channels.adapter.SectionAdapter
import com.tilal6991.channels.base.relayVM
import com.tilal6991.channels.databinding.DashboardItemBinding
import com.tilal6991.channels.presenter.helper.ClientChildListener
import com.tilal6991.channels.util.failAssert
import com.tilal6991.channels.viewmodel.ChannelVM
import com.tilal6991.channels.viewmodel.ClientChildVM
import com.tilal6991.channels.viewmodel.ClientVM
import com.tilal6991.channels.viewmodel.ServerVM
import timber.log.Timber

class DashboardPresenter(override val activity: MainActivity) : Presenter {
    override val id: String
        get() = "actions"

    private lateinit var dialog: BottomSheetDialog
    private lateinit var adapter: Adapter

    private var displayedClient: ClientVM? = null
    private var displayedChild: ClientChildVM? = null

    private val childListener = ClientChildListener(activity) { onChildChanged(it) }
    private val statusListener = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            if (propertyId != BR.statusInt) {
                return
            } else if (displayedClient == null || displayedChild == null) {
                Timber.asTree().failAssert()
                return
            }
            updateAction(displayedClient!!, displayedChild!!)
        }
    }

    override fun setup(savedState: Bundle?) {
        dialog = BottomSheetDialog(activity, R.style.Theme_Design_Light_BottomSheetDialog)

        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.dashboard_layout, null, false)

        val recycler = view.findViewById(R.id.actions_recycler) as RecyclerView
        val gridLayoutManager = GridLayoutManager(activity, 3)
        recycler.layoutManager = gridLayoutManager

        adapter = Adapter(activity) { onActionClicked(it) }
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
        val selectedClient = relayVM.selectedClients.latest
        selectedClient?.addOnPropertyChangedCallback(statusListener)
        onChildChanged(selectedClient?.selectedChild?.get())

        childListener.bind()
    }

    override fun unbind() {
        relayVM.selectedClients.latest?.removeOnPropertyChangedCallback(statusListener)
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
        val serverStrings: IntArray
        val serverDrawables: IntArray
        if (status == ClientVM.DISCONNECTED) {
            serverStrings = AdapterData.serverDisconnectStrings
            serverDrawables = AdapterData.serverDisconnectDrawables
        } else {
            serverStrings = AdapterData.serverStrings
            serverDrawables = AdapterData.serverDrawables
        }

        if (child is ChannelVM) {
            adapter.setData(AdapterData.channelTitles, arrayOf(serverStrings), arrayOf(serverDrawables))
        } else if (child is ServerVM) {
            adapter.setData(AdapterData.serverTitles, arrayOf(serverStrings), arrayOf(serverDrawables))
        } else {
            Timber.d("Unknown client child encountered in the dashboard.")
        }
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
        }
        dialog.dismiss()
    }

    class Adapter(private val context: Context,
                  private val clickListener: (Int) -> Unit) : SectionAdapter<Adapter.ItemViewHolder, HeaderViewHolder>() {
        private val layoutInflater = LayoutInflater.from(context)

        private var titles: IntArray? = null
        private var strings: Array<IntArray>? = null
        private var drawables: Array<IntArray>? = null

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
            if (viewType == HEADER_VIEW_TYPE) {
                return HeaderViewHolder(layoutInflater.inflate(R.layout.dashboard_header, parent, false))
            }
            return ItemViewHolder(DashboardItemBinding.inflate(layoutInflater, parent, false))
        }

        override fun onBindHeaderViewHolder(holder: HeaderViewHolder, section: Int) {
            holder.bind(context.getString(titles!![section]))
        }

        override fun onBindItemViewHolder(holder: ItemViewHolder, section: Int, offset: Int) {
            holder.bind(drawables!![section][offset], strings!![section][offset])
        }

        override fun getSectionCount(): Int {
            return titles?.size ?: 0
        }

        override fun getItemCountInSection(section: Int): Int {
            return strings?.get(section)?.size ?: 0
        }

        override fun isHeaderDisplayedForSection(section: Int): Boolean {
            return true
        }

        fun setData(newTitles: IntArray?, newStrings: Array<IntArray>?, newDrawables: Array<IntArray>?) {
            titles = newTitles
            strings = newStrings
            drawables = newDrawables

            notifySectionedDataSetChanged()
        }

        inner class ItemViewHolder(
                private val binding: DashboardItemBinding) : RecyclerView.ViewHolder(binding.root) {

            fun bind(drawable: Int, string: Int) {
                binding.actionImage.setImageResource(drawable)
                binding.actionText.setText(string)

                binding.root.setOnClickListener { clickListener(string) }
            }
        }
    }

    object AdapterData {
        val serverTitles = intArrayOf(R.string.server_actions)
        val channelTitles = intArrayOf(R.string.channel_actions, R.string.server_actions)

        val serverDisconnectStrings = intArrayOf(R.string.close, R.string.reconnect)
        val serverDisconnectDrawables = intArrayOf(R.drawable.ic_close, R.drawable.ic_cached)

        val serverStrings = intArrayOf(R.string.disconnect, R.string.disconnect_close)
        val serverDrawables = intArrayOf(R.drawable.ic_cancel, R.drawable.ic_close)
    }

    companion object {
        const val SHOWING = "showing"
    }
}