package com.tilal6991.channels.redux

import android.content.Context
import android.text.TextUtils
import android.widget.LinearLayout.*
import com.github.andrewoma.dexx.collection.IndexedList
import com.tilal6991.channels.R
import com.tilal6991.channels.base.store
import com.tilal6991.channels.redux.state.Client
import com.tilal6991.channels.redux.util.recyclerHeader
import com.tilal6991.channels.redux.util.resolveColor
import com.tilal6991.channels.redux.util.resolveDrawable
import com.tilal6991.channels.redux.util.statusToResource
import com.tilal6991.channels.util.failAssert
import timber.log.Timber
import trikita.anvil.DSL.*
import trikita.anvil.appcompat.v7.AppCompatv7DSL.appCompatImageView
import trikita.anvil.appcompat.v7.AppCompatv7DSL.appCompatTextView

class NavigationClientAdapter(private val context: Context,
                              private var clients: IndexedList<Client>) : SectionAdapter() {

    override fun headerView(section: Int) {
        val textId: Int
        if (section == 0) {
            textId = R.string.header_active_clients
        } else if (section == 1) {
            textId = R.string.header_inactive_clients
        } else {
            return Timber.asTree().failAssert()
        }
        recyclerHeader(context, textId)
    }

    override fun itemView(section: Int, offset: Int) {
        val itemType = getSectionedItemViewType(section, offset)
        if (itemType == FOOTER_ITEM_TYPE) {
            footerItemView(offset)
        } else if (itemType == CLIENT_ITEM_TYPE) {
            clientItemView(section, offset)
        } else {
            Timber.asTree().failAssert()
        }
    }

    private fun clientItemView(section: Int, offset: Int) {
        linearLayout {
            val client = clients[offset]
            onClick {
                context.store.dispatch(Action.SelectClient(client.configuration))
            }
            backgroundResource(context.resolveDrawable(R.attr.selectableItemBackground))

            size(MATCH, dip(72))
            orientation(HORIZONTAL)
            padding(dip(16), dip(8), 0, dip(8))
            linearLayout {
                size(dip(0), WRAP)
                layoutGravity(CENTER_VERTICAL)
                weight(1.0f)
                orientation(VERTICAL)

                appCompatTextView {
                    size(WRAP, WRAP)
                    textSize(sip(16.0f))
                    textColor(context.resolveColor(android.R.attr.textColorPrimary))
                    ellipsize(TextUtils.TruncateAt.END)
                    singleLine(true)
                    text(clients[offset].configuration.name)
                }

                appCompatTextView {
                    size(WRAP, WRAP)
                    textSize(sip(12.0f))
                    textColor(context.resolveColor(android.R.attr.textColorSecondary))
                    margin(0, dip(4), 0, 0)
                    ellipsize(TextUtils.TruncateAt.END)
                    singleLine(true)

                    if (client.status == Client.STATUS_STOPPED) {
                        text(client.configuration.server.hostname)
                    } else {
                        text(statusToResource(client.status))
                    }
                }
            }

            appCompatImageView {
                size(dip(56), dip(56))
                layoutGravity(CENTER_VERTICAL)
                backgroundColor(context.resolveDrawable(R.attr.selectableItemBackground))
                clickable(true)
                focusable(false)
                padding(dip(16))
                visibility(if (client.status == Client.STATUS_STOPPED) VISIBLE else GONE)
                imageResource(R.drawable.ic_settings)
            }
        }
    }

    private fun footerItemView(offset: Int) {
        backgroundResource(context.resolveDrawable(R.attr.selectableItemBackground))

        linearLayout {
            size(MATCH, WRAP)
            orientation(HORIZONTAL)
            padding(dip(8), dip(16), dip(8), dip(16))

            appCompatImageView {
                size(dip(24), dip(24))
                imageResource(if (offset == 0) R.drawable.ic_add else R.drawable.ic_settings)
            }

            appCompatTextView {
                size(MATCH, MATCH)
                textSize(sip(16.0f))
                textColor(context.resolveColor(android.R.attr.textColorPrimary))
                margin(dip(8), 0, 0, 0)
                weight(1.0f)
                ellipsize(TextUtils.TruncateAt.END)
                singleLine(true)

                text(if (offset == 0) R.string.add_server else R.string.settings)
            }
        }
    }

    override fun getItemCountInSection(section: Int): Int {
        if (section == MANAGE_SECTION) {
            return FOOTER_ITEM_COUNT
        }
        return clients.size()
    }

    override fun isHeaderDisplayedForSection(section: Int): Boolean {
        return section != MANAGE_SECTION
    }

    override fun getSectionedItemViewType(section: Int, sectionOffset: Int): Int {
        if (section == MANAGE_SECTION) {
            return FOOTER_ITEM_TYPE
        }
        return CLIENT_ITEM_TYPE
    }

    override fun getSectionCount(): Int {
        return SECTION_COUNT
    }

    override fun getHeaderId(section: Int): Long {
        return -1
    }

    override fun getItemId(section: Int, offset: Int): Long {
        if (section == MANAGE_SECTION) {
            return 1345 + offset.toLong()
        }
        return clients[offset].configuration.id.toLong()
    }

    fun setData(clients: IndexedList<Client>) {
        if (clients === this.clients) {
            return
        }

        this.clients = clients
        notifySectionedDataSetChanged()
    }

    companion object {
        const val SECTION_COUNT = 2
        const val MANAGE_SECTION = 1

        const val FOOTER_ITEM_COUNT = 2

        const val CLIENT_ITEM_TYPE = 20
        const val FOOTER_ITEM_TYPE = 25
    }
}