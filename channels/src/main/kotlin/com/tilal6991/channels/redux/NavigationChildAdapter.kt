package com.tilal6991.channels.redux

import android.content.Context
import android.text.TextUtils
import android.widget.LinearLayout.VERTICAL
import com.tilal6991.channels.R
import com.tilal6991.channels.redux.state.Client
import com.tilal6991.channels.redux.util.recyclerHeader
import com.tilal6991.channels.redux.util.resolveColor
import com.tilal6991.channels.redux.util.resolveDrawable
import com.tilal6991.channels.util.failAssert
import timber.log.Timber
import trikita.anvil.DSL.*

class NavigationChildAdapter(private val context: Context) : SectionAdapter() {

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
        backgroundColor(context.resolveDrawable(R.attr.selectableItemBackground))
        onClick {
            val type = if (section == 0) Client.SELECTED_SERVER else Client.SELECTED_CHANNEL
            store.dispatch(Action.ChangeSelectedChild(type, offset))
        }

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
                text(selectedChild?.name)
            }

            textView {
                size(WRAP, WRAP)
                textSize(sip(12.0f))
                textColor(context.resolveColor(android.R.attr.textColorSecondary))
                margin(0, dip(4), 0, 0)
                ellipsize(TextUtils.TruncateAt.END)
                singleLine(true)
                text(message(selectedChild))
            }
        }
    }

    override fun getSectionedItemViewType(section: Int, sectionOffset: Int): Int {
        return 10
    }

    override fun getItemCountInSection(section: Int): Int {
        if (section == 0) {
            return 1
        }
        return selectedClient?.channels?.size() ?: 0
    }

    override fun isHeaderDisplayedForSection(section: Int): Boolean {
        return section == 1
    }

    // TODO(tilal6991) make this 3 when PMs come into play.
    override fun getSectionCount(): Int {
        return 2
    }
}