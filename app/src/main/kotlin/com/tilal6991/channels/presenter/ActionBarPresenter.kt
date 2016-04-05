package com.tilal6991.channels.presenter

import android.support.v7.app.ActionBar
import android.view.Menu
import com.tilal6991.channels.R
import com.tilal6991.channels.activity.MainActivity
import com.tilal6991.channels.base.relayVM
import com.tilal6991.channels.presenter.helper.ClientChildListener
import com.tilal6991.channels.viewmodel.ClientChildVM

class ActionBarPresenter(override val activity: MainActivity) : Presenter {
    override val id: String
        get() = "toolbar"

    private val childListener = object : ClientChildListener(activity) {
        override fun onChildChange(clientChild: ClientChildVM?) {
            updateActionBar(clientChild)
        }
    }

    private val actionBar: ActionBar?
        get() = activity.supportActionBar

    override fun bind() {
        childListener.bind()
        updateActionBar(selectedChild?.get())
    }

    override fun unbind() {
        childListener.unbind()
    }

    private fun updateActionBar(it: ClientChildVM?) {
        actionBar?.title = it?.name ?: "Channels"
        actionBar?.subtitle = selectedClientsVM.latest?.name
        activity.supportInvalidateOptionsMenu()
    }

    fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val item = menu.findItem(R.id.menu_action_button)
        val client = relayVM.selectedClients.latest
        item.isVisible = client != null
        return true
    }
}