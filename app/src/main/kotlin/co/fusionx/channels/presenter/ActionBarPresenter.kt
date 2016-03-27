package co.fusionx.channels.presenter

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.view.Menu
import android.view.MenuItem
import co.fusionx.channels.R
import co.fusionx.channels.activity.MainActivity
import co.fusionx.channels.base.relayVM
import co.fusionx.channels.presenter.helper.ClientChildListener
import co.fusionx.channels.viewmodel.ClientChildVM

class ActionBarPresenter(override val activity: MainActivity) : Presenter {
    override val id: String
        get() = "toolbar"

    private val childListener = ClientChildListener(activity) { updateActionBar(it) }

    private val actionBar: ActionBar
        get() = activity.supportActionBar!!

    override fun setup(savedState: Bundle?) {
        updateActionBar(selectedChild?.get())
    }

    override fun bind() {
        childListener.bind()
    }

    override fun unbind() {
        childListener.unbind()
    }

    private fun updateActionBar(it: ClientChildVM?) {
        actionBar.title = selectedClientsVM.latest?.name ?: "Channels"
        actionBar.subtitle = it?.name
        activity.supportInvalidateOptionsMenu()
    }

    fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val item = menu.findItem(R.id.menu_action_button)
        val client = relayVM.selectedClients.latest
        item.isVisible = client != null
        return true
    }
}