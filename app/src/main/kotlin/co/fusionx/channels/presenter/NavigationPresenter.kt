package co.fusionx.channels.presenter

import android.databinding.Observable
import android.os.Bundle
import co.fusionx.channels.adapter.NavigationAdapter
import co.fusionx.channels.adapter.NavigationChildAdapter
import co.fusionx.channels.adapter.NavigationClientAdapter
import co.fusionx.channels.base.relayVM
import co.fusionx.channels.controller.MainActivity
import co.fusionx.channels.databinding.ObservableListAdapterProxy
import co.fusionx.channels.databinding.SortedListAdapterProxy
import co.fusionx.channels.view.NavigationDrawerView
import co.fusionx.channels.viewmodel.ClientChildVM
import timber.log.Timber

public class NavigationPresenter(override val activity: MainActivity,
                                 private val view: NavigationDrawerView) : Presenter {
    override val id: String get() = "NAVIGATION_PRESENTER"

    internal var currentType: Int = VIEW_TYPE_CLIENT
        private set

    private lateinit var clientAdapter: NavigationClientAdapter
    private lateinit var clientListener: SortedListAdapterProxy
    private lateinit var childAdapter: NavigationChildAdapter
    private lateinit var childListener: ObservableListAdapterProxy<ClientChildVM>
    private lateinit var adapter: NavigationAdapter

    private val selectedClientCallback = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            val client = relayVM.selectedClient.get()
            updateCurrentType(if (client == null) VIEW_TYPE_CLIENT else VIEW_TYPE_CHILD)
            adapter.updateHeader()
        }
    }

    override fun setup() {
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

        adapter = NavigationAdapter(view.context, clientAdapter) {
            if (currentType == VIEW_TYPE_CHILD) {
                updateCurrentType(VIEW_TYPE_CLIENT)
            } else {
                updateCurrentType(VIEW_TYPE_CHILD)
            }
        }
        view.setAdapter(adapter)
    }

    private fun updateCurrentType(type: Int) {
        if (type == currentType) return

        // Stop observing everything old.
        if (currentType == VIEW_TYPE_CLIENT) {
            relayVM.clients.removeObserver(clientListener)
        } else if (currentType == VIEW_TYPE_CHILD) {
            relayVM.selectedClient.get()?.children?.removeOnListChangedCallback(childListener)
        } else {
            Timber.e("This should not be happening.")
        }

        // Swap the old items out and the new items in.
        currentType = type

        // Start observing everything new.
        if (currentType == VIEW_TYPE_CLIENT) {
            adapter.updateContentAdapter(clientAdapter)
            relayVM.clients.addObserver(clientListener)
        } else if (currentType == VIEW_TYPE_CHILD) {
            adapter.updateContentAdapter(childAdapter)
            relayVM.selectedClient.get()!!.children.addOnListChangedCallback(childListener)
        } else {
            Timber.e("This should not be happening.")
        }
    }

    override fun restoreState(bundle: Bundle) {
        updateCurrentType(bundle.getInt(PARCEL_CURRENT_TYPE))
    }

    override fun bind() {
        relayVM.clients.addObserver(clientListener)
        relayVM.selectedClient.addOnPropertyChangedCallback(selectedClientCallback)
    }

    override fun unbind() {
        if (currentType == VIEW_TYPE_CHILD) {
            relayVM.selectedClient.get()?.children?.removeOnListChangedCallback(childListener)
        } else {
            relayVM.clients.removeObserver(clientListener)
        }
        relayVM.selectedClient.removeOnPropertyChangedCallback(selectedClientCallback)
    }

    override fun saveState(): Bundle {
        val bundle = Bundle()
        bundle.putInt(PARCEL_CURRENT_TYPE, currentType)
        return Bundle()
    }

    companion object {
        const val VIEW_TYPE_CLIENT: Int = 1
        const val VIEW_TYPE_CHILD: Int = 2

        const val PARCEL_CURRENT_TYPE: String = "current_type"
    }
}