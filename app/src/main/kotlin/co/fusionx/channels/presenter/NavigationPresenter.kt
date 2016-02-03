package co.fusionx.channels.presenter

import android.databinding.Observable
import android.os.Bundle
import android.view.View
import co.fusionx.channels.R
import co.fusionx.channels.adapter.NavigationAdapter
import co.fusionx.channels.adapter.NavigationChildAdapter
import co.fusionx.channels.adapter.NavigationClientAdapter
import co.fusionx.channels.base.relayVM
import co.fusionx.channels.controller.MainActivity
import co.fusionx.channels.databinding.ObservableListAdapterProxy
import co.fusionx.channels.databinding.SortedListAdapterProxy
import co.fusionx.channels.view.NavigationDrawerView
import co.fusionx.channels.viewmodel.persistent.ClientChildVM
import co.fusionx.channels.viewmodel.persistent.SelectedClientsVM
import co.fusionx.channels.viewmodel.transitory.NavigationHeaderVM
import timber.log.Timber

public class NavigationPresenter(override val activity: MainActivity,
                                 private val view: NavigationDrawerView) : Presenter {
    override val id: String get() = "NAVIGATION_PRESENTER"

    private var currentType: Int = VIEW_TYPE_CLIENT

    private lateinit var clientAdapter: NavigationClientAdapter
    private lateinit var clientListener: SortedListAdapterProxy
    private lateinit var childAdapter: NavigationChildAdapter
    private lateinit var childListener: ObservableListAdapterProxy<ClientChildVM>
    private lateinit var adapter: NavigationAdapter
    private lateinit var headerVM: NavigationHeaderVM

    private val selectedClientCallback = object : SelectedClientsVM.OnLatestClientChangedCallback {
        override fun onLatestClientChanged() {
            val client = selectedClientsVM.latest
            updateCurrentType(if (client == null) VIEW_TYPE_CLIENT else VIEW_TYPE_CHILD)
        }
    }
    private val headerClickListener = View.OnClickListener {
        if (currentType == VIEW_TYPE_CHILD) {
            updateCurrentType(VIEW_TYPE_CLIENT)
        } else {
            updateCurrentType(VIEW_TYPE_CHILD)
        }
    }
    private val connectedCountCallback = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            updateHeader()
        }
    }

    override fun setup() {
        headerVM = NavigationHeaderVM()

        clientAdapter = NavigationClientAdapter(view.context) {
            activity.onClientClick(it)

            // Make sure we're displaying the child view.
            updateCurrentType(VIEW_TYPE_CHILD)
        }
        clientListener = SortedListAdapterProxy(clientAdapter)

        childAdapter = NavigationChildAdapter(view.context) {
            activity.onChildClick(it)
        }
        childListener = ObservableListAdapterProxy<ClientChildVM>(childAdapter)

        adapter = NavigationAdapter(view.context, clientAdapter, headerVM, selectedClientsVM)
        view.setAdapter(adapter)
    }

    override fun restoreState(bundle: Bundle) {
        updateCurrentType(bundle.getInt(PARCEL_CURRENT_TYPE))
    }

    override fun bind() {
        relayVM.clients.addObserver(clientListener)
        selectedClientsVM.addOnClientsChangedCallback(selectedClientCallback)
        relayVM.connectedClientCount.addOnPropertyChangedCallback(connectedCountCallback)

        updateHeader()
    }

    override fun unbind() {
        relayVM.selectedClients.removeOnClientsChangedCallback(selectedClientCallback)
        if (currentType == VIEW_TYPE_CHILD) {
            relayVM.selectedClients.latest?.children?.removeOnListChangedCallback(childListener)
        } else {
            relayVM.clients.removeObserver(clientListener)
        }
        relayVM.connectedClientCount.removeOnPropertyChangedCallback(connectedCountCallback)
    }

    private fun updateCurrentType(type: Int) {
        if (type == currentType) return

        // Stop observing everything old.
        if (currentType == VIEW_TYPE_CLIENT) {
            relayVM.clients.removeObserver(clientListener)
        } else if (currentType == VIEW_TYPE_CHILD) {
            relayVM.selectedClients.latest?.children?.removeOnListChangedCallback(childListener)
        } else {
            Timber.e("This should not be happening. $currentType is not valid.")
        }

        // Swap the old items out and the new items in.
        currentType = type

        // Start observing everything new.
        if (currentType == VIEW_TYPE_CLIENT) {
            adapter.updateContentAdapter(clientAdapter)
            relayVM.clients.addObserver(clientListener)
        } else if (currentType == VIEW_TYPE_CHILD) {
            adapter.updateContentAdapter(childAdapter)
            relayVM.selectedClients.latest!!.children.addOnListChangedCallback(childListener)
        } else {
            Timber.e("This should not be happening. $currentType is not valid.")
        }
        updateHeader()
    }

    private fun updateHeader() {
        if (currentType == VIEW_TYPE_CLIENT) {
            headerVM.updateText(getString(R.string.app_name),
                    getQuantityString(R.plurals.connected_client_count, relayVM.connectedClientCount.get())
                            .format(relayVM.connectedClientCount.get()))
        } else {
            headerVM.updateText(selectedClientsVM.latest!!.name, selectedChild!!.get()!!.name)
        }

        if (selectedClientsVM.latest == null) {
            headerVM.updateListener(null)
        } else {
            headerVM.updateListener(headerClickListener)
        }
    }

    override fun saveState(): Bundle {
        val bundle = Bundle()
        bundle.putInt(PARCEL_CURRENT_TYPE, currentType)
        return bundle
    }

    companion object {
        const val VIEW_TYPE_CLIENT: Int = 1
        const val VIEW_TYPE_CHILD: Int = 2

        const val PARCEL_CURRENT_TYPE: String = "current_type"
    }
}