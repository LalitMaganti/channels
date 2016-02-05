package co.fusionx.channels.presenter

import android.databinding.ObservableList
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.View
import android.widget.EditText
import co.fusionx.channels.adapter.MainItemAdapter
import co.fusionx.channels.controller.MainActivity
import co.fusionx.channels.presenter.helper.ClientChildListener
import co.fusionx.channels.databinding.ObservableListAdapterProxy
import co.fusionx.channels.presenter.helper.MessageTextHandler
import co.fusionx.channels.view.EventRecyclerView
import co.fusionx.channels.viewmodel.persistent.ClientChildVM

class ClientChildPresenter(override val activity: MainActivity,
                           private val messageInput: EditText,
                           private val eventRecyclerView: EventRecyclerView) : Presenter {
    override val id: String
        get() = "events"

    private lateinit var messageHandler: Bindable

    private var displayedChild: ClientChildVM? = null
    private val childListener = ClientChildListener(activity) { switchContent() }

    private val adapter: MainItemAdapter = MainItemAdapter(activity)
    private var listener: ObservableListAdapterProxy<CharSequence> = object : ObservableListAdapterProxy<CharSequence>(adapter) {
        override fun onItemRangeInserted(sender: ObservableList<CharSequence>?, positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(sender, positionStart, itemCount)
            eventRecyclerView.scroll(positionStart + itemCount - 1)
        }
    }

    override fun setup() {
        messageHandler = MessageTextHandler(messageInput)
        messageHandler.setup()

        eventRecyclerView.adapter = adapter
        switchContent()
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

    private fun switchContent() {
        val newChild = selectedChild?.get()
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