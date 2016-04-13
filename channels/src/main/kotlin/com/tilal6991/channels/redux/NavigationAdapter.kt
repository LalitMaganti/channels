package com.tilal6991.channels.redux

import android.content.Context
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.tilal6991.channels.R
import com.tilal6991.channels.redux.state.Client
import com.tilal6991.channels.redux.util.resolveDrawable
import com.tilal6991.channels.view.BezelImageView
import com.tilal6991.channels.view.ClientCarouselView
import com.tilal6991.channels.view.NavigationHeaderImageView
import trikita.anvil.Anvil
import trikita.anvil.DSL.*
import trikita.anvil.RenderableRecyclerViewAdapter
import trikita.anvil.appcompat.v7.AppCompatv7DSL.appCompatTextView
import java.util.*

class NavigationAdapter(
        private val context: Context,
        private var contentAdapter: Child,
        private val headerClick: (View) -> Unit) : RenderableRecyclerViewAdapter() {

    private val headerCount = 1
    private val latestContentCount: Int
        get() = contentAdapter.itemCount
    private var cachedContentCount: Int = 0

    private val observer = ChildAdapterObserver()

    init {
        contentAdapter.registerAdapterDataObserver(observer)
    }

    fun updateContentAdapter(adapter: Child) {
        contentAdapter.unregisterAdapterDataObserver(observer)
        contentAdapter = adapter
        adapter.registerAdapterDataObserver(observer)

        adapter.notifyDataSetChanged()
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

    override fun view(holder: RecyclerView.ViewHolder) {
        val position = holder.adapterPosition
        if (position == RecyclerView.NO_POSITION) {
            return
        }

        val viewType = getItemViewType(position)
        if (viewType == VIEW_TYPE_HEADER) {
            headerView()
        } else {
            contentAdapter.view(position - headerCount)
        }
    }

    override fun getItemId(position: Int): Long {
        val viewType = getItemViewType(position)
        if (viewType == VIEW_TYPE_HEADER) {
            return -10000000
        }
        return contentAdapter.getItemId(position - headerCount)
    }

    private fun headerView() {
        v(ClientCarouselView::class.java) {
            size(MATCH, dip(172))

            v(NavigationHeaderImageView::class.java) {
                size(MATCH, MATCH)
                backgroundColor(ResourcesCompat.getColor(context.resources,
                        R.color.colorSecondary, null))
                scaleType(ImageView.ScaleType.FIT_XY)

                imageResource(context.resolveDrawable(R.attr.selectableItemBackground))

                if (selectedClient() == null) {
                    onClick(null)
                } else {
                    onClick(headerClick)
                }
            }

            appCompatTextView {
                init { Anvil.currentView<View>().id = R.id.header_subtitle }
                size(WRAP, WRAP)
                alignParentBottom()
                margin(dip(16), 0, 0, dip(8))
                padding(dip(4))
                attr({ v, n, o -> (v as TextView).setTextAppearance(n) },
                        R.style.TextAppearance_Channels_Navigation_SubHeader)

                if (selectedClient() == null) {
                    text(context.resources
                            .getQuantityString(R.plurals.active_client_count, 0).format(0))
                } else {
                    text(statusToString())
                }
            }

            appCompatTextView {
                size(WRAP, WRAP)
                margin(dip(16), 0, 0, 0)
                padding(dip(4))
                above(R.id.header_subtitle)
                attr({ v, n, o -> (v as TextView).setTextAppearance(n) },
                        R.style.TextAppearance_Channels_Navigation_Header)

                if (selectedClient() == null) {
                    text(R.string.app_name)
                } else {
                    text(selectedClient()!!.configuration.name)
                }
            }

            v(BezelImageView::class.java) {
                size(dip(48), dip(48))
                alignParentStart()
                margin(dip(16), dip(48), 0, 0)
                alpha(0.0f)
                padding(dip(8))
                scaleType(ImageView.ScaleType.CENTER_CROP)
                imageResource(R.drawable.person_image_empty)
                visibility(View.INVISIBLE)
            }

            v(BezelImageView::class.java) {
                size(dip(48), dip(48))
                id(R.id.profile_image_3)
                alignParentEnd()
                margin(0, dip(48), 0, 0)
                alpha(0.0f)
                padding(dip(8))
                scaleType(ImageView.ScaleType.CENTER_CROP)
                imageResource(R.drawable.person_image_empty)
                visibility(View.INVISIBLE)
            }

            v(BezelImageView::class.java) {
                size(dip(48), dip(48))
                toStartOf(R.id.profile_image_3)
                margin(0, dip(48), 0, 0)
                alpha(0.0f)
                padding(dip(8))
                scaleType(ImageView.ScaleType.CENTER_CROP)
                imageResource(R.drawable.person_image_empty)
                visibility(View.INVISIBLE)
            }
        }
    }

    private fun statusToString(): Int {
        return when(selectedClient()!!.status) {
            Client.STATUS_CONNECTED -> R.string.status_connected
            Client.STATUS_STOPPED -> R.string.status_disconnected
            Client.STATUS_DISCONNECTED -> R.string.status_disconnected
            Client.STATUS_CONNECTING -> R.string.status_connecting
            Client.STATUS_REGISTERING -> R.string.status_registering
            Client.STATUS_RECONNECTING -> R.string.status_reconnecting
            else -> R.string.app_name
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

    abstract class Child {
        abstract val itemCount: Int
        private var observers: MutableList<RecyclerView.AdapterDataObserver>? = null

        abstract fun getItemViewType(position: Int): Int
        abstract fun view(position: Int)

        fun registerAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
            if (observers == null) {
                observers = ArrayList()
            }
            observers!!.add(observer)
        }

        fun unregisterAdapterDataObserver(observer: RecyclerView.AdapterDataObserver) {
            observers?.remove(observer)
        }

        fun notifyDataSetChanged() {
            observers?.forEach { it.onChanged() }
        }

        fun notifyItemRangeInserted(positionStart: Int, itemCount: Int) {
            observers?.forEach { it.onItemRangeInserted(positionStart, itemCount) }
        }

        fun notifyItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            observers?.forEach { it.onItemRangeMoved(fromPosition, toPosition, itemCount) }
        }

        fun notifyItemRangeChanged(positionStart: Int, itemCount: Int) {
            observers?.forEach { it.onItemRangeChanged(positionStart, itemCount) }
        }

        fun notifyItemRangeRemoved(positionStart: Int, itemCount: Int) {
            observers?.forEach { it.onItemRangeRemoved(positionStart, itemCount) }
        }

        abstract fun getItemId(position: Int): Long
    }

    companion object {
        const val VIEW_TYPE_HEADER: Int = 0
    }
}