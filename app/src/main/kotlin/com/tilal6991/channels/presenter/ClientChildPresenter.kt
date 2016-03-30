package com.tilal6991.channels.presenter

import android.databinding.ObservableList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.View
import android.widget.EditText
import com.tilal6991.channels.adapter.MainItemAdapter
import com.tilal6991.channels.activity.MainActivity
import com.tilal6991.channels.presenter.helper.ClientChildListener
import com.tilal6991.channels.collections.ObservableListAdapterProxy
import com.tilal6991.channels.presenter.helper.MessageTextHandler
import com.tilal6991.channels.view.EventRecyclerView
import com.tilal6991.channels.viewmodel.ClientChildVM

class ClientChildPresenter(override val activity: MainActivity,
                           private val messageInput: EditText,
                           private val eventRecyclerView: EventRecyclerView) : Presenter {
    override val id: String
        get() = "events"

    private lateinit var messageHandler: Bindable

    private var displayedChild: ClientChildVM? = null
    private val childListener = ClientChildListener(activity) { switchContent(it) }

    private val adapter: MainItemAdapter = MainItemAdapter(activity)
    private var listener: ObservableListAdapterProxy<CharSequence> = object : ObservableListAdapterProxy<CharSequence>(adapter) {
        override fun onItemRangeInserted(sender: ObservableList<CharSequence>?, positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(sender, positionStart, itemCount)
            eventRecyclerView.scroll(positionStart + itemCount - 1)
        }
    }

    override fun setup(savedState: Bundle?) {
        messageHandler = MessageTextHandler(messageInput)
        messageHandler.setup()

        eventRecyclerView.adapter = adapter
        switchContent(selectedChild?.get())
    }

    override fun bind() {
        messageHandler.bind()
        childListener.bind()
    }

    override fun unbind() {
        messageHandler.unbind()
        childListener.unbind()
    }

    override fun teardown() {
        messageHandler.teardown()
    }

    private fun switchContent(newChild: ClientChildVM?) {
        if (displayedChild == newChild) return

        displayedChild?.buffer?.removeOnListChangedCallback(listener)

        val buffer = newChild?.buffer
        adapter.setBuffer(buffer)
        adapter.notifyDataSetChanged()

        // Scroll to the bottom once the items are present.
        if (buffer == null) {
            eventRecyclerView.visibility = View.GONE
            messageInput.visibility = View.GONE
        } else {
            eventRecyclerView.visibility = View.VISIBLE
            messageInput.visibility = View.VISIBLE

            eventRecyclerView.forceScroll(buffer.size - 1)
            buffer.addOnListChangedCallback(listener)
        }

        displayedChild = newChild
    }
}