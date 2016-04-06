package com.tilal6991.channels.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.tilal6991.channels.R
import com.tilal6991.channels.databinding.DashboardItemBinding

class DashboardAdapter(private val context: Context,
                       private val clickListener: (Int) -> Unit) : SectionAdapter<DashboardAdapter.ItemViewHolder, HeaderViewHolder>() {
    private val layoutInflater = LayoutInflater.from(context)

    private var titles: IntArray? = null
    private var strings: Array<IntArray>? = null
    private var drawables: Array<IntArray>? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        if (viewType == HEADER_VIEW_TYPE) {
            return HeaderViewHolder(layoutInflater.inflate(R.layout.dashboard_header, parent, false))
        }
        return ItemViewHolder(DashboardItemBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindHeaderViewHolder(holder: HeaderViewHolder, section: Int) {
        holder.bind(context.getString(titles!![section]))
    }

    override fun onBindItemViewHolder(holder: ItemViewHolder, section: Int, offset: Int) {
        holder.bind(drawables!![section][offset], strings!![section][offset])
    }

    override fun getSectionCount(): Int {
        return titles?.size ?: 0
    }

    override fun getItemCountInSection(section: Int): Int {
        return strings?.get(section)?.size ?: 0
    }

    override fun isHeaderDisplayedForSection(section: Int): Boolean {
        return true
    }

    fun setData(newTitles: IntArray?, newStrings: Array<IntArray>?, newDrawables: Array<IntArray>?) {
        titles = newTitles
        strings = newStrings
        drawables = newDrawables

        notifySectionedDataSetChanged()
    }

    inner class ItemViewHolder(
            private val binding: DashboardItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(drawable: Int, string: Int) {
            binding.actionImage.setImageResource(drawable)
            binding.actionText.setText(string)

            binding.root.setOnClickListener { clickListener(string) }
        }
    }
}