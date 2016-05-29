package com.tilal6991.channels.redux.controller

import android.content.Context
import android.support.annotation.IntDef
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.bindView
import com.tilal6991.channels.R
import com.tilal6991.channels.adapter.HeaderViewHolder
import com.tilal6991.channels.adapter.SectionAdapter
import com.tilal6991.channels.redux.state.Client
import com.tilal6991.channels.redux.util.TransactingIndexedList

class NavigationClientAdapter(private val context: Context) :
        SectionAdapter<NavigationClientAdapter.ViewHolder, HeaderViewHolder>() {

    private val inflater = LayoutInflater.from(context)

    private var active: TransactingIndexedList<Client>? = null
    private var inactive: TransactingIndexedList<Client>? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == HEADER_VIEW_TYPE) {
            return HeaderViewHolder(inflater.inflate(R.layout.recycler_header, parent, false))
        } else if (viewType == 25) {
            return FooterViewHolder(inflater.inflate(R.layout.navigation_footer, parent, false))
        }
        return ClientViewHolder(inflater.inflate(R.layout.navigation_client_new, parent, false))
    }

    override fun onBindItemViewHolder(holder: ViewHolder, section: Int, offset: Int) {
        holder.bind(section, offset)
    }

    override fun onBindHeaderViewHolder(holder: HeaderViewHolder, @Section section: Int) {
        if (section == ACTIVE) {
            holder.bind(context.getString(R.string.header_active_clients))
        } else if (section == INACTIVE) {
            holder.bind(context.getString(R.string.header_inactive_clients))
        }
    }

    override fun isHeaderDisplayedForSection(@Section section: Int): Boolean {
        return section != MISC
    }

    override fun getItemCountInSection(@Section section: Int): Int {
        return getListForSection(section)?.size() ?: 0
    }

    fun active(list: TransactingIndexedList<Client>) {
        active = list
    }

    fun inactive(list: TransactingIndexedList<Client>) {
        inactive = list
    }

    override fun getSectionedItemViewType(@Section section: Int, sectionOffset: Int) = when (section) {
        ACTIVE, INACTIVE -> 20
        MISC -> 25
        else -> 20
    }

    private fun getListForSection(@Section section: Int): TransactingIndexedList<Client>? = when (section) {
        ACTIVE -> active
        INACTIVE -> inactive
        else -> active
    }

    override fun getSectionCount(): Int {
        return 3
    }

    abstract class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(@Section section: Int, offset: Int)
    }

    inner class ClientViewHolder(private val view: View) : ViewHolder(view) {
        private val title by bindView<TextView>(R.id.drawer_client_title)
        private val status by bindView<TextView>(R.id.drawer_client_status)
        private val manage by bindView<ImageView>(R.id.drawer_client_manage)

        override fun bind(@Section section: Int, offset: Int) {
            val client = getListForSection(section)?.get(offset) ?: return
            val configuration = client.configuration

            title.text = configuration.name
            status.text = configuration.server.hostname
            manage.visibility = View.GONE
        }
    }

    inner class FooterViewHolder(private val view: View) : ViewHolder(view) {
        private val image by bindView<ImageView>(R.id.image)
        private val text by bindView<TextView>(R.id.text)

        override fun bind(@Section section: Int, offset: Int) {
            text.setText(if (offset == 0) R.string.add_server else R.string.settings)
            image.setImageResource(if (offset == 0) R.drawable.ic_add else R.drawable.ic_settings)
        }
    }

    companion object {
        const val ACTIVE = 0
        const val INACTIVE = 1
        const val MISC = 2
    }

    @IntDef(ACTIVE.toLong(), INACTIVE.toLong(), MISC.toLong())
    annotation class Section
}