package co.fusionx.channels.presenter

import android.databinding.ObservableList
import co.fusionx.channels.adapter.MainItemAdapter
import co.fusionx.channels.controller.MainActivity
import co.fusionx.channels.databinding.ClientChildListener
import co.fusionx.channels.databinding.ObservableListAdapterProxy
import co.fusionx.channels.relay.ClientChild
import co.fusionx.channels.view.EventRecyclerView

class EventPresenter(override val activity: MainActivity,
                     private val eventRecyclerView: EventRecyclerView) : Presenter {
    override val id: String
        get() = "events"

    private var displayedChild: ClientChild? = null
    private val childListener = ClientChildListener(activity) { switchContent() }

    private val adapter: MainItemAdapter = MainItemAdapter(activity)
    private var listener: ObservableListAdapterProxy<CharSequence> = object : ObservableListAdapterProxy<CharSequence>(adapter) {
        override fun onItemRangeInserted(sender: ObservableList<CharSequence>?, positionStart: Int, itemCount: Int) {
            super.onItemRangeInserted(sender, positionStart, itemCount)
            eventRecyclerView.scroll(positionStart + itemCount - 1)
        }
    }

    override fun setup() {
        eventRecyclerView.adapter = adapter
        switchContent()
    }

    override fun bind() {
        childListener.bind()
    }

    override fun unbind() {
        childListener.unbind()
    }

    private fun switchContent() {
        if (displayedChild == selectedChild?.get()) return

        val buffer = selectedChild?.get()?.buffer
        adapter.setBuffer(buffer)
        adapter.notifyDataSetChanged()

        // Scroll to the bottom once the items are present.
        if (buffer != null) {
            eventRecyclerView.forceScroll(buffer.size - 1)
        }

        displayedChild?.buffer?.removeOnListChangedCallback(listener)
        displayedChild = selectedChild?.get()
        displayedChild?.buffer?.addOnListChangedCallback(listener)
    }
}