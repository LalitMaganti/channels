package com.tilal6991.channels.collections

import android.databinding.ObservableList
import com.tilal6991.channels.adapter.SectionAdapter

open class ListSectionProxy<T>(
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