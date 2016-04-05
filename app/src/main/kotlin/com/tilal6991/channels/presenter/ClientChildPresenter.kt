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

class ClientChildPresenter(override val context: MainActivity,
                           private val messageInput: EditText,
                           private val navigationHint: TextView,
                           private val eventRecyclerView: EventRecyclerView) : Presenter {
    override val id: String
        get() = "events"

    private lateinit var messageHandler: Bindable

    private var displayedClient: ClientVM? = null
    private var displayedChild: ClientChildVM? = null
    private val childListener = object : ClientChildListener(context) {
        override fun onChildChange(clientChild: ClientChildVM?) {
            switchContent(clientChild)
        }
    }

    private val adapter: MainItemAdapter = MainItemAdapter(context)
    private var listener: ObservableListAdapterProxy<CharSequence> = object : ObservableListAdapterProxy<CharSequence>(adapter) {
        override fun onItemRangeInserted(sender: ObservableList<CharSequence>?, positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(sender, positionStart, itemCount)
            eventRecyclerView.scroll(positionStart + itemCount - 1)
        }
    }
    private val messageBoxListener = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            if (propertyId != BR.statusInt && propertyId != BR.active) {
                return
            }
            val latest = selectedClientsVM.latest!!
            val status = latest.statusInt
            onMessageBoxEnableState(latest.selectedChild.get(), status)
        }

        fun bind(client: ClientVM?, child: ClientChildVM?) {
            client?.addOnPropertyChangedCallback(this)
            child?.addOnPropertyChangedCallback(this)
        }

        fun unbind(client: ClientVM?, child: ClientChildVM?) {
            client?.removeOnPropertyChangedCallback(this)
            child?.removeOnPropertyChangedCallback(this)
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
        messageBoxListener.bind(displayedClient, displayedChild)
    }

    override fun unbind() {
        messageHandler.unbind()
        childListener.unbind()
        messageBoxListener.unbind(displayedClient, displayedChild)
    }

    override fun teardown() {
        messageHandler.teardown()
    }

    private fun onMessageBoxEnableState(selectedChild: ClientChildVM, statusInt: Int) {
        messageInput.enabled = selectedChild.active &&
                (statusInt == ClientVM.CONNECTED || statusInt == ClientVM.SOCKET_CONNECTED)
    }

    private fun switchContent(newChild: ClientChildVM?) {
        if (displayedChild == newChild) return

        listener.unbind(displayedChild?.buffer)
        messageBoxListener.unbind(displayedClient, displayedChild)

        val buffer = newChild?.buffer
        adapter.setBuffer(buffer)
        adapter.notifyDataSetChanged()

        if (newChild == null || buffer == null) {
            eventRecyclerView.visibility = View.GONE
            messageInput.visibility = View.GONE
            navigationHint.visibility = View.VISIBLE

            displayedClient = null
            displayedChild = null
        } else {
            eventRecyclerView.visibility = View.VISIBLE
            messageInput.visibility = View.VISIBLE
            navigationHint.visibility = View.GONE

            displayedClient = selectedClientsVM.latest
            displayedChild = newChild

            listener.bind(buffer)
            eventRecyclerView.forceScroll(buffer.size - 1)

            messageBoxListener.bind(displayedClient, displayedChild)
            onMessageBoxEnableState(newChild, displayedClient!!.statusInt)
        }
    }
}