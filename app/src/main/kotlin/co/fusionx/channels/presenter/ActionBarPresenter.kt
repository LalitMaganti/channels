package co.fusionx.channels.presenter

import android.support.v7.app.ActionBar
import co.fusionx.channels.controller.MainActivity
import co.fusionx.channels.databinding.ClientChildListener

public class ActionBarPresenter(override val activity: MainActivity) : Presenter {
    override val id: String
        get() = "toolbar"

    private val childListener = ClientChildListener(activity) { updateActionBar() }

    private val actionBar: ActionBar
        get() = activity.supportActionBar

    private fun updateActionBar() {
        actionBar.title = selectedClient.get()?.name ?: "Channels"
        actionBar.subtitle = selectedChild?.get()?.name
    }

    override fun setup() {
        updateActionBar()
    }

    override fun bind() {
        childListener.bind()
    }

    override fun unbind() {
        childListener.unbind()
    }

}