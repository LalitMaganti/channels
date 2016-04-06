package com.tilal6991.channels.collections

import android.databinding.ObservableList

abstract class ObservableListChangedProxy<T> : ObservableList.OnListChangedCallback<ObservableList<T>>() {

    override final fun onItemRangeRemoved(sender: ObservableList<T>, positionStart: Int, itemCount: Int) {
        onListChanged(sender)
    }

    override final fun onChanged(sender: ObservableList<T>) {
        onListChanged(sender)
    }

    override final fun onItemRangeChanged(sender: ObservableList<T>, positionStart: Int, itemCount: Int) {
        onListChanged(sender)
    }

    override final fun onItemRangeMoved(sender: ObservableList<T>, fromPosition: Int, toPosition: Int, itemCount: Int) {
        onListChanged(sender)
    }

    override final fun onItemRangeInserted(sender: ObservableList<T>, positionStart: Int, itemCount: Int) {
        onListChanged(sender)
    }

    protected abstract fun onListChanged(sender: ObservableList<T>)
}