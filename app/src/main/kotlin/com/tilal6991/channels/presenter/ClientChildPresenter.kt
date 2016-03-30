package com.tilal6991.channels.presenter

import android.databinding.Observable
import android.databinding.ObservableList
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.tilal6991.channels.BR
import com.tilal6991.channels.activity.MainActivity
import com.tilal6991.channels.adapter.MainItemAdapter
import com.tilal6991.channels.collections.ObservableListAdapterProxy
import com.tilal6991.channels.presenter.helper.ClientChildListener
import com.tilal6991.channels.presenter.helper.MessageTextHandler
import com.tilal6991.channels.view.EventRecyclerView
import com.tilal6991.channels.viewmodel.ClientChildVM
import com.tilal6991.channels.viewmodel.ClientVM
import org.jetbrains.anko.enabled

class ClientChildPresenter(override val activity: MainActivity,
                           private val messageInput: EditText,
                           private val navigationHint: TextView,
                           private val eventRecyclerView: EventRecyclerView) : Presenter {
    override val id: String
        get() = "events"

    private lateinit var messageHandler: Bindable

    private var displayedClient: ClientVM? = null
    private var displayedChild: ClientChildVM? = null
    private val childListener = ClientChildListener(activity) {
        switchContent(it)
    }

    private val adapter: MainItemAdapter = MainItemAdapter(activity)
    private var listener: ObservableListAdapterProxy<CharSequence> = object : ObservableListAdapterProxy<CharSequence>(adapter) {
        override fun onItemRangeInserted(sender: ObservableList<CharSequence>?, positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(sender, positionStart, itemCount)
            eventRecyclerView.scroll(positionStart + itemCount - 1)
        }
    }
    private val statusListener = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            if (propertyId != BR.statusInt) {
                return
            }
            onStatusChanged(selectedClientsVM.latest?.statusInt!!)
        }
    }

    override fun setup(savedState: Bundle?) {
        messageHandler = MessageTextHandler(messageInput)
        messageHandler.setup()

        eventRecyclerView.adapter = adapter
    }

    override fun bind() {
        switchContent(selectedChild?.get())

        messageHandler.bind()
        childListener.bind()

        selectedClientsVM.latest?.addOnPropertyChangedCallback(statusListener)
    }

    override fun unbind() {
        messageHandler.unbind()
        childListener.unbind()

        selectedClientsVM.latest?.removeOnPropertyChangedCallback(statusListener)
    }

    override fun teardown() {
        messageHandler.teardown()
    }

    private fun onStatusChanged(statusInt: Int) {
        messageInput.enabled = statusInt == ClientVM.CONNECTED || statusInt == ClientVM.SOCKET_CONNECTED
    }

    private fun switchContent(newChild: ClientChildVM?) {
        if (displayedChild == newChild) return

        displayedClient?.removeOnPropertyChangedCallback(statusListener)
        displayedChild?.buffer?.removeOnListChangedCallback(listener)

        val buffer = newChild?.buffer
        adapter.setBuffer(buffer)
        adapter.notifyDataSetChanged()

        // Scroll to the bottom once the items are present.
        if (buffer == null) {
            eventRecyclerView.visibility = View.GONE
            messageInput.visibility = View.GONE
            navigationHint.visibility = View.VISIBLE

            displayedClient = null
        } else {
            eventRecyclerView.visibility = View.VISIBLE
            messageInput.visibility = View.VISIBLE
            navigationHint.visibility = View.GONE

            eventRecyclerView.forceScroll(buffer.size - 1)
            buffer.addOnListChangedCallback(listener)

            displayedClient = selectedClientsVM.latest
            displayedClient!!.addOnPropertyChangedCallback(statusListener)
            onStatusChanged(displayedClient!!.statusInt)
        }

        displayedChild = newChild
    }
}