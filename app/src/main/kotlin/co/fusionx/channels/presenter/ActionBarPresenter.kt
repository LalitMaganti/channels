package co.fusionx.channels.presenter

import android.os.Bundle
import android.support.v7.app.ActionBar
import co.fusionx.channels.activity.MainActivity
import co.fusionx.channels.presenter.helper.ClientChildListener

class ActionBarPresenter(override val activity: MainActivity) : Presenter {
    override val id: String
        get() = "toolbar"

    private val childListener = ClientChildListener(activity) { updateActionBar() }

    private val actionBar: ActionBar
        get() = activity.supportActionBar!!

    override fun setup(savedState: Bundle?) {
        updateActionBar()
    }

    override fun bind() {
        childListener.bind()
    }

    override fun unbind() {
        childListener.unbind()
    }

    private fun updateActionBar() {
        actionBar.title = selectedClientsVM.latest?.name ?: "Channels"
        actionBar.subtitle = selectedChild?.get()?.name
    }
}