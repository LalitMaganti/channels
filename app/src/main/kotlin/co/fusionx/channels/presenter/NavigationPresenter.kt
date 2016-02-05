package co.fusionx.channels.presenter

import android.databinding.Observable
import android.databinding.ObservableList
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import co.fusionx.channels.R
import co.fusionx.channels.adapter.NavigationAdapter
import co.fusionx.channels.adapter.NavigationChildAdapter
import co.fusionx.channels.adapter.NavigationClientAdapter
import co.fusionx.channels.adapter.SectionAdapter
import co.fusionx.channels.base.relayVM
import co.fusionx.channels.controller.MainActivity
import co.fusionx.channels.databinding.ListSectionProxy
import co.fusionx.channels.view.NavigationDrawerView
import co.fusionx.channels.viewmodel.persistent.ChannelVM
import co.fusionx.channels.viewmodel.persistent.ClientChildVM
import co.fusionx.channels.viewmodel.persistent.ClientVM
import co.fusionx.channels.viewmodel.persistent.SelectedClientsVM
import co.fusionx.channels.viewmodel.transitory.NavigationHeaderVM

class NavigationPresenter(override val activity: MainActivity,
                                 private val view: NavigationDrawerView) : Presenter {
    override val id: String get() = "NAVIGATION_PRESENTER"

    private lateinit var currentHelper: Helper
    private lateinit var clientHelper: ClientHelper
    private lateinit var childHelper: ChildHelper
    private lateinit var adapter: NavigationAdapter
    private lateinit var headerVM: NavigationHeaderVM

    private val selectedClientCallback = object : SelectedClientsVM.OnLatestClientChangedCallback {
        override fun onLatestClientChanged() {
            val client = selectedClientsVM.latest
            updateCurrentType(if (client == null) clientHelper else childHelper)
        }
    }
    private val headerClickListener = View.OnClickListener {
        if (currentHelper == childHelper) {
            updateCurrentType(clientHelper)
        } else {
            updateCurrentType(childHelper)
        }
    }

    override fun setup() {
        headerVM = NavigationHeaderVM()

        clientHelper = ClientHelper()
        clientHelper.setup()

        childHelper = ChildHelper()
        childHelper.setup()

        currentHelper = clientHelper

        adapter = NavigationAdapter(view.context, clientHelper.clientAdapter, headerVM, selectedClientsVM)
        view.setAdapter(adapter)
    }

    override fun restoreState(bundle: Bundle) {
        val isChild = bundle.getInt(PARCEL_CURRENT_TYPE) == VIEW_TYPE_CHILD
        updateCurrentType(if (isChild) childHelper else clientHelper)
    }

    override fun bind() {
        currentHelper.bind()
        selectedClientsVM.addOnClientsChangedCallback(selectedClientCallback)

        // Make sure we're displaying the most up to date information.
        updateHeader()
        clientHelper.adapter.notifySectionedDataSetChanged()
    }

    override fun unbind() {
        relayVM.selectedClients.removeOnClientsChangedCallback(selectedClientCallback)
        currentHelper.unbind()
    }

    private fun updateCurrentType(helper: Helper) {
        if (helper == currentHelper) {
            // Although the type data displayed might not have changed, we might have changed
            // clients in child view.
            helper.rebind()
            return
        }

        // Stop observing everything old.
        currentHelper.unbind()

        // Swap the old items out and the new items in.
        currentHelper = helper

        // Start observing everything new.
        currentHelper.adapter.notifySectionedDataSetChanged()
        adapter.updateContentAdapter(currentHelper.adapter)
        currentHelper.bind()

        // Update the headers as well.
        updateHeader()
    }

    private fun updateHeader() {
        currentHelper.updateHeader()

        if (selectedClientsVM.latest == null) {
            headerVM.updateListener(null)
        } else {
            headerVM.updateListener(headerClickListener)
        }
    }

    override fun saveState(): Bundle {
        val bundle = Bundle()
        val value = if (currentHelper == clientHelper) VIEW_TYPE_CLIENT else VIEW_TYPE_CHILD
        bundle.putInt(PARCEL_CURRENT_TYPE, value)
        return bundle
    }

    private inner class ClientHelper : Helper {
        override val adapter: SectionAdapter<out RecyclerView.ViewHolder, out RecyclerView.ViewHolder>
            get() = clientAdapter

        lateinit var clientAdapter: NavigationClientAdapter
        private lateinit var activeClientListener: ListSectionProxy<ClientVM>
        private lateinit var inactiveClientListener: ListSectionProxy<ClientVM>

        override fun setup() {
            clientAdapter = NavigationClientAdapter(view.context, relayVM) {
                activity.onClientClick(it)

                // Make sure we're displaying the child view.
                updateCurrentType(childHelper)
            }
            clientAdapter.setup()

            activeClientListener = object : ListSectionProxy<ClientVM>(0, clientAdapter) {
                override fun onItemRangeInserted(sender: ObservableList<ClientVM>, positionStart: Int, itemCount: Int) {
                    this@NavigationPresenter.updateHeader()
                    super.onItemRangeInserted(sender, positionStart, itemCount)
                }

                override fun onChanged(sender: ObservableList<ClientVM>) {
                    this@NavigationPresenter.updateHeader()
                    super.onChanged(sender)
                }

                override fun onItemRangeRemoved(sender: ObservableList<ClientVM>, positionStart: Int, itemCount: Int) {
                    this@NavigationPresenter.updateHeader()
                    super.onItemRangeRemoved(sender, positionStart, itemCount)
                }
            }
            inactiveClientListener = ListSectionProxy<ClientVM>(1, clientAdapter)
        }

        override fun bind() {
            relayVM.activeClients.addOnListChangedCallback(activeClientListener)
            relayVM.inactiveClients.addOnListChangedCallback(inactiveClientListener)
        }

        override fun rebind() {
        }

        override fun updateHeader() {
            val count = relayVM.activeClients.size
            headerVM.updateText(getString(R.string.app_name),
                    getQuantityString(R.plurals.connected_client_count, count).format(count))
        }

        override fun unbind() {
            relayVM.activeClients.removeOnListChangedCallback(activeClientListener)
            relayVM.inactiveClients.removeOnListChangedCallback(inactiveClientListener)
        }
    }

    private inner class ChildHelper : Helper {
        override val adapter: SectionAdapter<out RecyclerView.ViewHolder, out RecyclerView.ViewHolder>
            get() = childAdapter

        private var displayedClient: ClientVM? = null
        private val displayedChannels: ObservableList<ChannelVM>?
            get() = displayedClient?.channels

        private lateinit var childAdapter: NavigationChildAdapter
        private lateinit var channelsListener: ListSectionProxy<ChannelVM>
        private lateinit var selectedChildChanged: Observable.OnPropertyChangedCallback

        override fun setup() {
            childAdapter = NavigationChildAdapter(view.context) {
                activity.onChildClick(it)
            }
            childAdapter.setup()

            selectedChildChanged = object : Observable.OnPropertyChangedCallback() {
                override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                    this@NavigationPresenter.updateHeader()
                }
            }
            channelsListener = ListSectionProxy<ChannelVM>(1, childAdapter)
        }

        override fun bind() {
            displayedClient = relayVM.selectedClients.latest

            displayedChannels!!.addOnListChangedCallback(channelsListener)
            displayedClient!!.selectedChild.addOnPropertyChangedCallback(selectedChildChanged)
        }

        override fun rebind() {
            if (displayedClient == relayVM.selectedClients.latest) {
                return
            }

            unbind()
            bind()

            this@NavigationPresenter.updateHeader()
            childAdapter.notifySectionedDataSetChanged()
        }

        override fun updateHeader() {
            headerVM.updateText(selectedClientsVM.latest!!.name, selectedChild!!.get()!!.name)
        }

        override fun unbind() {
            displayedChannels?.removeOnListChangedCallback(channelsListener)
            displayedClient?.selectedChild?.removeOnPropertyChangedCallback(selectedChildChanged)

            displayedClient = null
        }
    }

    private interface Helper {
        val adapter: SectionAdapter<out RecyclerView.ViewHolder, out RecyclerView.ViewHolder>

        fun setup()
        fun bind()
        fun rebind()
        fun updateHeader()
        fun unbind()
    }

    companion object {
        const val VIEW_TYPE_CLIENT: Int = 1
        const val VIEW_TYPE_CHILD: Int = 2

        const val PARCEL_CURRENT_TYPE: String = "current_type"
    }
}