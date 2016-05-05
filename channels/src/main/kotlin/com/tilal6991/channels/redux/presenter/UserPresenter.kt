package com.tilal6991.channels.redux.presenter

import android.content.Context
import android.content.res.Resources
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.view.ViewCompat
import android.support.v4.widget.DrawerLayout
import android.support.v4.widget.TextViewCompat
import android.widget.LinearLayout
import android.widget.TextView
import com.github.andrewoma.dexx.collection.IndexedList
import com.tilal6991.channels.R
import com.tilal6991.channels.redux.SectionAdapter
import com.tilal6991.channels.redux.selectedChild
import com.tilal6991.channels.redux.selectedClient
import com.tilal6991.channels.redux.state.Channel
import com.tilal6991.channels.redux.state.Client
import com.tilal6991.channels.redux.state.ClientChild
import com.tilal6991.channels.redux.subscribe
import com.tilal6991.channels.redux.util.*
import trikita.anvil.Anvil
import trikita.anvil.DSL.*
import trikita.anvil.appcompat.v7.AppCompatv7DSL.appCompatTextView
import trikita.anvil.recyclerview.v7.RecyclerViewv7DSL.*
import java.util.*

class UserPresenter(private val context: Context) : Anvil.Renderable {

    private val resources: Resources
        get() = context.resources

    private lateinit var adapter: Adapter
    private lateinit var runnable: Runnable

    fun setup() {
        adapter = Adapter(context)
        adapter.setup()
    }

    fun bind() {
        runnable = subscribe(context) {
            adapter.onChildChanged(selectedClient(), selectedChild())
        }
    }

    fun unbind() {
        runnable.run()
    }

    override fun view() {
        xml(R.layout.user_drawer) {
            id(R.id.user_drawer_view)
            size(resources.getDimensionPixelSize(R.dimen.user_drawer_width), MATCH)
            backgroundColor(ResourcesCompat.getColor(
                    resources, R.color.navigation_background_color, null))
            backgroundColor(ResourcesCompat.getColor(resources, android.R.color.white, null))
            orientation(LinearLayout.VERTICAL)

            attr({ v, n, o -> (v.layoutParams as DrawerLayout.LayoutParams).gravity = n }, END)
            attr({ v, n, o -> ViewCompat.setElevation(v, n) }, dip(10.0f))

            view {
                id(R.id.user_toolbar)

                size(MATCH, context.resolveDimen(R.attr.actionBarSize))
                backgroundColor(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))
            }

            recyclerView {
                id(R.id.user_list_recycler)
                size(MATCH, MATCH)
                clipToPadding(false)
                padding(0, dip(8), 0, dip(8))

                adapter(adapter)
                linearLayoutManager()
            }
        }
    }

    class Adapter(private val context: Context) : SectionAdapter() {

        private var displayedClient: Client? = null
        private var displayedChild: Channel? = null

        private var mapTransactionNumber: Int = 0
        private val listsTransactionNumbers: MutableList<Int> = ArrayList()

        override fun isHeaderDisplayedForSection(section: Int): Boolean {
            return true
        }

        override fun itemView(section: Int, offset: Int) {
            val valueAt = displayedChild?.modeMap?.get(section)
            val displayString = valueAt?.users?.get(offset)?.nick

            appCompatTextView {
                size(MATCH, WRAP)
                backgroundResource(context.resolveDrawable(R.attr.selectableItemBackground))
                gravity(CENTER_VERTICAL)
                minHeight(context.resolveDimen(android.R.attr.listPreferredItemHeightSmall))
                padding(dip(16), 0, dip(16), 0)
                attr({ v, n, o -> TextViewCompat.setTextAppearance((v as TextView), n) },
                        context.resolveTextAppearance(android.R.attr.textAppearanceListItem))
                textIsSelectable(false)

                text(displayString ?: "Broken")
            }
        }

        override fun headerView(section: Int) {
            val userMap = displayedChild?.modeMap
            val secItem = userMap?.get(section) ?: return
            recyclerHeader(context, "${secItem.users.size()} ${secItem.char} users")
        }

        override fun getHeaderId(section: Int): Long {
            return section.toLong()
        }

        override fun getItemId(section: Int, offset: Int): Long {
            return section.toLong() * 41 + offset.toLong()
        }

        override fun getSectionCount(): Int {
            return displayedChild?.modeMap?.size() ?: 0
        }

        override fun getItemCountInSection(section: Int): Int {
            return displayedChild?.modeMap?.get(section)?.users?.size() ?: 0
        }

        fun onChildChanged(client: Client?, clientChild: ClientChild?) {
            if (clientChild === displayedChild) {
                return
            }

            val isSameChild = (client?.configuration?.name == displayedClient?.configuration?.name
                    && clientChild?.name == displayedChild?.name)
            if (isSameChild) {
                if (client == null || clientChild == null || clientChild !is Channel) {
                    // TODO(tilal6991) - these are error cases.
                    return resetNonChannel()
                }

                displayedChild = clientChild
                displayedClient = client

                // TODO(tilal6991) - actually deal with this properly.
                if (clientChild.modeMap.transactionCount() > mapTransactionNumber) {
                    return resetChannel(client, clientChild)
                }

                for ((i, p) in clientChild.modeMap.withIndex()) {
                    val list = p.component2()
                    val toConsume = list.transactionCount() - listsTransactionNumbers[i]
                    if (toConsume > list.maxSize()) {
                        resetChannel(client, clientChild)
                    } else if (toConsume > 0) {
                        consumeSection(p.component2().transactions, toConsume, i)
                        listsTransactionNumbers[i] = list.transactionCount()
                    }
                }
            } else {
                if (clientChild is Channel) {
                    resetChannel(client, clientChild)
                } else {
                    resetNonChannel()
                }
            }
        }

        private fun consumeSection(transactions: IndexedList<TransactingIndexedList.Transaction>,
                                   toConsume: Int,
                                   section: Int) {
            val start = transactions.size() - toConsume
            for (i in start..transactions.size() - 1) {
                val t = transactions.get(i)
                when (t.type) {
                    TransactingIndexedList.ADD ->
                        notifyItemRangeInsertedInSection(section, t.startIndex, t.count)
                    TransactingIndexedList.REMOVE ->
                        notifyItemRangeInsertedInSection(section, t.startIndex, t.count)
                    TransactingIndexedList.MOVE ->
                        notifyItemRangeMovedInSection(section, t.startIndex, t.toIndex, t.count)
                    TransactingIndexedList.CHANGE ->
                        notifyItemRangeChangedInSection(section, t.startIndex, t.count)
                }
            }
        }

        private fun resetChannel(client: Client?, clientChild: Channel) {
            displayedChild = clientChild
            displayedClient = client

            mapTransactionNumber = clientChild.modeMap.transactionCount()
            listsTransactionNumbers.clear()
            for ((k, v) in clientChild.modeMap) {
                listsTransactionNumbers.add(v.transactionCount())
            }
            notifySectionedDataSetChanged()
        }

        private fun resetNonChannel() {
            displayedChild = null
            displayedClient = null

            mapTransactionNumber = 0
            listsTransactionNumbers.clear()
            notifySectionedDataSetChanged()
        }
    }
}