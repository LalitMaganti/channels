package co.fusionx.channels.presenter

import android.content.Context
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import co.fusionx.channels.R
import co.fusionx.channels.activity.MainActivity
import co.fusionx.channels.adapter.HeaderViewHolder
import co.fusionx.channels.adapter.SectionAdapter
import co.fusionx.channels.base.relayVM
import co.fusionx.channels.databinding.DashboardItemBinding
import co.fusionx.channels.presenter.helper.ClientChildListener
import co.fusionx.channels.viewmodel.ChannelVM
import co.fusionx.channels.viewmodel.ClientChildVM
import co.fusionx.channels.viewmodel.ClientVM
import co.fusionx.channels.viewmodel.ServerVM
import timber.log.Timber

class DashboardPresenter(override val activity: MainActivity) : Presenter {
    override val id: String
        get() = "actions"

    private lateinit var dialog: BottomSheetDialog
    private lateinit var adapter: Adapter

    private val childListener = ClientChildListener(activity) { updateActions(it) }

    fun toggle() {
        if (dialog.isShowing) {
            dialog.dismiss()
        } else {
            dialog.show()
        }
    }

    override fun setup(savedState: Bundle?) {
        dialog = BottomSheetDialog(activity, R.style.Theme_Design_Light_BottomSheetDialog)

        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.dashboard_layout, null, false)

        val recycler = view.findViewById(R.id.actions_recycler) as RecyclerView
        val gridLayoutManager = GridLayoutManager(activity, 3)
        recycler.layoutManager = gridLayoutManager

        adapter = Adapter(activity)
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

    private fun updateActions(it: ClientChildVM?) {
        if (it == null) {
            adapter.setData(null, null, null)
            return
        }

        val status = relayVM.selectedClients.latest?.statusInt
        val serverStrings: IntArray
        val serverDrawables: IntArray
        if (status == ClientVM.DISCONNECTED) {
            serverStrings = AdapterData.serverDisconnectStrings
            serverDrawables = AdapterData.serverDisconnectDrawables
        } else {
            serverStrings = AdapterData.serverStrings
            serverDrawables = AdapterData.serverDrawables
        }

        if (it is ChannelVM) {
            adapter.setData(AdapterData.channelTitles, arrayOf(serverStrings), arrayOf(serverDrawables))
        } else if (it is ServerVM) {
            adapter.setData(AdapterData.serverTitles, arrayOf(serverStrings), arrayOf(serverDrawables))
        } else {
            Timber.d("Unknown client child encountered in the dashboard.")
        }
    }

    override fun restoreState(bundle: Bundle) {
        val showing = bundle.getBoolean(SHOWING, false)
        if (showing) {
            dialog.show()
        }
    }

    override fun bind() {
        childListener.bind()
    }

    override fun unbind() {
        childListener.unbind()
    }

    override fun saveState(): Bundle {
        val bundle = Bundle()
        bundle.putBoolean(SHOWING, dialog.isShowing)
        return bundle
    }

    class Adapter(private val context: Context) : SectionAdapter<Adapter.ItemViewHolder, HeaderViewHolder>() {
        private val layoutInflater = LayoutInflater.from(context)

        private var titles: IntArray? = null
        private var strings: Array<IntArray>? = null
        private var drawables: Array<IntArray>? = null

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
            if (viewType == HEADER_VIEW_TYPE) {
                return HeaderViewHolder(layoutInflater.inflate(R.layout.recycler_header, parent, false))
            }
            return ItemViewHolder(DashboardItemBinding.inflate(layoutInflater, parent, false))
        }

        override fun onBindHeaderViewHolder(holder: HeaderViewHolder, section: Int) {
            holder.bind("Test")
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