package com.tilal6991.channels.redux

import android.content.Context
import android.text.TextUtils
import android.widget.LinearLayout.VERTICAL
import com.github.andrewoma.dexx.collection.IndexedList
import com.tilal6991.channels.R
import com.tilal6991.channels.base.store
import com.tilal6991.channels.redux.state.Client
import com.tilal6991.channels.redux.util.TransactingIndexedList
import com.tilal6991.channels.redux.util.recyclerHeader
import com.tilal6991.channels.redux.util.resolveColor
import com.tilal6991.channels.redux.util.resolveDrawable
import com.tilal6991.channels.util.failAssert
import timber.log.Timber
import trikita.anvil.DSL.*

class NavigationChildAdapter(private val context: Context) : SectionAdapter() {

    private var displayedClient: Client? = null
    private var transactionNumber = 0

    override fun headerView(section: Int) {
        val text: Int
        if (section == 1) {
            text = R.string.header_channels
        } else if (section == 2) {
            text = R.string.header_private_messages
        } else {
            Timber.asTree().failAssert()
            return
        }
        recyclerHeader(context, text)
    }

    override fun itemView(section: Int, offset: Int) {
        backgroundResource(context.resolveDrawable(R.attr.selectableItemBackground))
        onClick {
            val type = if (section == 0) Client.SELECTED_SERVER else Client.SELECTED_CHANNEL
            context.store.dispatch(Action.ChangeSelectedChild(type, offset))
        }

        val child = if (section == 0) displayedClient?.server else displayedClient?.channels?.get(offset)
        linearLayout {
            size(MATCH, WRAP)
            orientation(VERTICAL)
            padding(dip(16))

            textView {
                size(WRAP, WRAP)
                textSize(sip(16.0f))
                textColor(context.resolveColor(android.R.attr.textColorPrimary))
                ellipsize(TextUtils.TruncateAt.END)
                singleLine(true)
                text(child?.name)
            }

            textView {
                size(WRAP, WRAP)
                textSize(sip(12.0f))
                textColor(context.resolveColor(android.R.attr.textColorSecondary))
                margin(0, dip(4), 0, 0)
                ellipsize(TextUtils.TruncateAt.END)
                singleLine(true)
                text(message(child))
            }
        }
    }

    override fun getHeaderId(section: Int): Long {
        return 10000 - 1
    }

    override fun getItemId(section: Int, offset: Int): Long {
        if (section == 0) {
            return 10000 + (displayedClient?.configuration?.id?.toLong() ?: 0)
        }
        val channel = displayedClient?.channels?.get(offset)
        return channel?.name?.hashCode()?.toLong() ?: 0
    }

    override fun getSectionedItemViewType(section: Int, sectionOffset: Int): Int {
        return 10
    }

    override fun getItemCountInSection(section: Int): Int {
        if (section == 0) {
            return if (displayedClient == null) 0 else 1
        }
        return displayedClient?.channels?.size() ?: 0
    }

    override fun isHeaderDisplayedForSection(section: Int): Boolean {
        return section == 1
    }

    // TODO(tilal6991) make this 3 when PMs come into play.
    override fun getSectionCount(): Int {
        return 2
    }

    fun setData(selectedClient: Client?) {
        if (selectedClient === displayedClient) {
            return
        }

        val oldClient = displayedClient
        displayedClient = selectedClient

        if (oldClient?.configuration?.name != selectedClient?.configuration?.name) {
            transactionNumber = selectedClient?.channels?.transactionNumber() ?: 0
            return notifySectionedDataSetChanged()
        }

        if (oldClient?.server !== selectedClient?.server) {
            notifyItemRangeChangedInSection(0, 0, 1)
        }

        if (selectedClient != null && oldClient?.channels !== selectedClient.channels) {
            val channels = selectedClient.channels
            val toConsume = channels.transactionNumber() - transactionNumber
            if (toConsume > channels.maxSize()) {
                notifySectionedDataSetChanged()
            } else if (toConsume > 0) {
                consumeTransactions(channels.transactions, toConsume)
            }
            transactionNumber = channels.transactionNumber()
        }
    }

    private fun consumeTransactions(transactions: IndexedList<TransactingIndexedList.Transaction>,
                                    toConsume: Int) {
        val start = transactions.size() - toConsume
        for (i in start..transactions.size() - 1) {
            val t = transactions.get(i)
            when (t.type) {
                TransactingIndexedList.ADD ->
                    notifyItemRangeInsertedInSection(1, t.startIndex, t.count)
                TransactingIndexedList.REMOVE ->
                    notifyItemRangeInsertedInSection(1, t.startIndex, t.count)
                TransactingIndexedList.MOVE ->
                    notifyItemRangeMovedInSection(1, t.startIndex, t.toIndex, t.count)
                TransactingIndexedList.CHANGE ->
                    notifyItemRangeChangedInSection(1, t.startIndex, t.count)
            }
        }
    }
}