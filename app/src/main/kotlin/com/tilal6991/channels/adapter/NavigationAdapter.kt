package com.tilal6991.channels.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.tilal6991.channels.databinding.NavigationHeaderBinding
import com.tilal6991.channels.viewmodel.NavigationHeaderVM
import com.tilal6991.channels.viewmodel.SelectedClientsVM

class NavigationAdapter(
        private val context: Context,
        private var contentAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>,
        private val headerVM: NavigationHeaderVM,
        private val selectedClientsVM: SelectedClientsVM) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val inflater: LayoutInflater

    private val headerCount = 1
    private val latestContentCount: Int
        get() = contentAdapter.itemCount
    private var cachedContentCount: Int = 0

    private val observer = ChildAdapterObserver()

    init {
        inflater = LayoutInflater.from(context)

        contentAdapter.registerAdapterDataObserver(observer)
    }

    fun updateContentAdapter(adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>) {
        contentAdapter.unregisterAdapterDataObserver(observer)
        contentAdapter = adapter
        adapter.registerAdapterDataObserver(observer)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, type: Int): RecyclerView.ViewHolder? = when (type) {
        VIEW_TYPE_HEADER ->
            HeaderViewHolder(NavigationHeaderBinding.inflate(inflater, parent, false))
        else -> contentAdapter.onCreateViewHolder(parent, type)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        if (viewType == VIEW_TYPE_HEADER) {
            (holder as HeaderViewHolder).bind()
        } else {
            contentAdapter.onBindViewHolder(holder, position - headerCount)
        }
    }

    override fun getItemCount(): Int {
        cachedContentCount = latestContentCount
        return headerCount + cachedContentCount
    }

    override fun getItemViewType(position: Int): Int {
        if (position < headerCount) {
            return VIEW_TYPE_HEADER
        }
        return contentAdapter.getItemViewType(position - headerCount)
    }

    inner class HeaderViewHolder(
            private val binding: NavigationHeaderBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            binding.headerVm = headerVM
            binding.viewNavigationDrawerServerCarousel.setAdapter(selectedClientsVM)

            binding.executePendingBindings()
        }
    }

    private inner class ChildAdapterObserver : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            notifyItemRangeRemoved(headerCount, cachedContentCount)
            cachedContentCount = latestContentCount
            notifyItemRangeInserted(headerCount, cachedContentCount)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            notifyItemRangeChanged(positionStart + headerCount, itemCount)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            notifyItemRangeChanged(positionStart + headerCount, itemCount, payload)
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            notifyItemRangeInserted(positionStart + headerCount, itemCount)

            cachedContentCount += itemCount
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            notifyItemRangeRemoved(positionStart + headerCount, itemCount)

            cachedContentCount -= itemCount
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            for (i in 0..itemCount - 1) {
                notifyItemMoved(fromPosition + i + headerCount, toPosition + i + headerCount)
            }
        }
    }

    companion object {
        const val VIEW_TYPE_HEADER: Int = 0
    }
}