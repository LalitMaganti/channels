package co.fusionx.channels.databinding

import android.databinding.ObservableList
import co.fusionx.channels.adapter.SectionAdapter

public open class ListSectionProxy<T>(
        private val section: Int,
        private val adapter: SectionAdapter<*, *>) : ObservableList.OnListChangedCallback<ObservableList<T>>() {

    override fun onItemRangeRemoved(sender: ObservableList<T>, positionStart: Int, itemCount: Int) {
        adapter.notifyItemRangeRemovedInSection(section, positionStart, itemCount)
    }

    override fun onItemRangeMoved(sender: ObservableList<T>, fromPosition: Int, toPosition: Int, itemCount: Int) {
        adapter.notifyItemRangeMovedInSection(section, fromPosition, toPosition, itemCount)
    }

    override fun onChanged(sender: ObservableList<T>) {
        adapter.notifySectionedDataSetChanged()
    }

    override fun onItemRangeInserted(sender: ObservableList<T>, positionStart: Int, itemCount: Int) {
        adapter.notifyItemRangeInsertedInSection(section, positionStart, itemCount)
    }

    override fun onItemRangeChanged(sender: ObservableList<T>, positionStart: Int, itemCount: Int) {
        adapter.notifyItemRangeChangedInSection(section, positionStart, itemCount)
    }
}