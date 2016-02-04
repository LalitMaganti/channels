package co.fusionx.channels.adapter

import android.support.v7.widget.RecyclerView
import co.fusionx.channels.util.failAssert
import timber.log.Timber

public abstract class SectionAdapter<CVH : RecyclerView.ViewHolder,
        HVH : RecyclerView.ViewHolder> : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var setupComplete = false

    private lateinit var sectionAbsolutePositions: Array<Int>
    private var count: Int = 0

    public fun setup() {
        setupComplete = true
        sectionAbsolutePositions = Array(getSectionCount()) { 0 }

        notifySectionedDataSetChanged()
    }

    public abstract fun isHeaderDisplayedForSection(section: Int): Boolean

    public abstract fun onBindItemViewHolder(holder: CVH, section: Int, offset: Int)

    public abstract fun onBindHeaderViewHolder(holder: HVH, section: Int)

    public abstract fun getSectionCount(): Int

    public abstract fun getItemCountInSection(section: Int): Int

    public open fun getSectionedItemViewType(section: Int, sectionOffset: Int): Int {
        return ITEM_VIEW_TYPE
    }

    public open fun getHeaderViewType(section: Int): Int {
        return HEADER_VIEW_TYPE
    }

    public fun notifySectionedDataSetChanged() {
        checkSetup()

        count = 0
        for (i in 0..getSectionCount() - 1) {
            sectionAbsolutePositions[i] = count
            val itemCountInSection = getItemCountInSection(i)
            count += itemCountInSection
        }

        notifyDataSetChanged()
    }

    public fun notifyItemRangeInsertedInSection(section: Int, offset: Int, insertCount: Int) {
        checkSetup()

        val sectionStart = sectionAbsolutePositions[section] + getSectionHeadersBefore(section)
        val sectionCount = getItemCountInSection(section)
        if (sectionCount == insertCount) {
            if (offset != 0) {
                Timber.asTree().failAssert()
            }
            notifyItemRangeInserted(sectionStart, insertCount + getHeaderOffsetForSection(section))
        } else {
            notifyItemRangeInserted(sectionStart + getHeaderOffsetForSection(section) + offset, insertCount)
        }

        count += insertCount
    }

    fun notifyItemRangeMovedInSection(section: Int, fromPosition: Int, toPosition: Int, itemCount: Int) {
        checkSetup()

        val sectionItemStart = sectionAbsolutePositions[section] + getSectionHeadersBefore(section) + getHeaderOffsetForSection(section)
        notifyItemRangeMovedInSection(section, sectionItemStart + fromPosition, sectionItemStart + toPosition, itemCount)
    }

    fun notifyItemRangeChangedInSection(section: Int, positionStart: Int, itemCount: Int) {
        checkSetup()

        val sectionItemStart = sectionAbsolutePositions[section] + getSectionHeadersBefore(section) + getHeaderOffsetForSection(section)
        notifyItemRangeChanged(section, sectionItemStart + positionStart, itemCount)
    }

    public fun notifyItemRangeRemovedInSection(section: Int, offset: Int, removeCount: Int) {
        checkSetup()

        val sectionStart = sectionAbsolutePositions[section] + getSectionHeadersBefore(section)
        val sectionCount = getItemCountInSection(section)
        if (sectionCount == 0) {
            if (offset != 0) {
                Timber.asTree().failAssert()
            }
            notifyItemRangeRemoved(sectionStart, removeCount + getHeaderOffsetForSection(section))
        } else {
            notifyItemRangeRemoved(sectionStart + getHeaderOffsetForSection(section) + offset, removeCount)
        }

        count -= removeCount
    }

    override final fun onBindViewHolder(holder: RecyclerView.ViewHolder, adapterPosition: Int) {
        checkSetup()

        val section = getSectionForAdapterPosition(adapterPosition)
        val sectionOffset = getSectionOffsetForAdapterPosition(section, adapterPosition)

        if (sectionOffset == -1) {
            onBindHeaderViewHolder(holder as HVH, section)
        } else {
            onBindItemViewHolder(holder as CVH, section, sectionOffset)
        }
    }

    override final fun getItemViewType(adapterPosition: Int): Int {
        checkSetup()

        val section = getSectionForAdapterPosition(adapterPosition)
        val sectionOffset = getSectionOffsetForAdapterPosition(section, adapterPosition)

        if (sectionOffset == -1) {
            return getHeaderViewType(section)
        }
        return getSectionedItemViewType(section, sectionOffset)
    }

    override final fun getItemCount(): Int {
        checkSetup()

        return count + getSectionHeadersBefore(getSectionCount())
    }

    private fun checkSetup() {
        if (!setupComplete) {
            throw IllegalStateException("setup() was not called")
        }
    }

    private fun getHeaderOffsetForSection(section: Int): Int {
        return if (isHeaderDisplayedForSection(section)) 1 else 0
    }

    private fun getSectionOffsetForAdapterPosition(section: Int, adapterPosition: Int): Int {
        val nonEmptySections = getSectionHeadersBefore(section)
        val absolutePositionIncludingHeader = adapterPosition - nonEmptySections
        return absolutePositionIncludingHeader - sectionAbsolutePositions[section] - getHeaderOffsetForSection(section)
    }

    private fun getSectionForAdapterPosition(adapterPosition: Int): Int {
        var count = 0
        for (i in 0..getSectionCount() - 1) {
            if (adapterPosition < count) {
                return i - 1
            }
            val itemCountInSection = getItemCountInSection(i)
            if (itemCountInSection > 0) {
                count += itemCountInSection + getHeaderOffsetForSection(i)
            }
        }
        return getSectionCount() - 1
    }

    private fun getSectionHeadersBefore(section: Int): Int {
        var count = 0
        for (i in 0..section - 1) {
            if (getItemCountInSection(i) > 0 && isHeaderDisplayedForSection(i)) {
                count++
            }
        }
        return count
    }

    companion object {
        public const val HEADER_VIEW_TYPE = 5
        public const val ITEM_VIEW_TYPE = 10
    }
}