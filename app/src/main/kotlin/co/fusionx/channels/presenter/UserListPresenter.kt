package co.fusionx.channels.presenter

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.fusionx.channels.R
import co.fusionx.channels.adapter.HeaderViewHolder
import co.fusionx.channels.adapter.SectionAdapter
import co.fusionx.channels.presenter.helper.ClientChildListener
import co.fusionx.channels.viewmodel.ChannelVM
import co.fusionx.channels.viewmodel.ClientChildVM

class UserListPresenter(override val activity: Activity,
                        private val drawerLayout: DrawerLayout,
                        private val userDrawerView: View) : Presenter {
    override val id: String
        get() = "user_list"

    private val recycler: RecyclerView by userDrawerView.bindView(R.id.user_list_recycler)
    private val childListener = ClientChildListener(activity) { onChildChanged(it) }
    private val adapter = Adapter(activity)

    override fun setup(savedState: Bundle?) {
        adapter.setup()
        onChildChanged(selectedChild?.get())

        recycler.layoutManager = LinearLayoutManager(activity)
        recycler.adapter = adapter
    }

    override fun bind() {
        childListener.bind()
    }

    override fun unbind() {
        childListener.unbind()
    }

    private fun onChildChanged(clientChild: ClientChildVM?) {
        adapter.onChildChanged(clientChild)

        if (clientChild is ChannelVM) {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, userDrawerView)
        } else {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, userDrawerView)
        }
    }

    class Adapter(private val context: Context) : SectionAdapter<RecyclerView.ViewHolder, RecyclerView.ViewHolder>() {
        private val inflater = LayoutInflater.from(context)

        private var displayedChild: ChannelVM? = null

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
            return HeaderViewHolder(inflater.inflate(R.layout.recycler_header, parent, false))
        }

        override fun isHeaderDisplayedForSection(section: Int): Boolean {
            return false
        }

        override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder, section: Int, offset: Int) {
            (holder as HeaderViewHolder).bind(displayedChild?.userMap?.getKeyAt(offset)?.nick ?: "Broken")
        }

        override fun onBindHeaderViewHolder(holder: RecyclerView.ViewHolder, section: Int) {
            (holder as HeaderViewHolder).bind("Header")
        }

        override fun getSectionCount(): Int {
            return 1
        }

        override fun getItemCountInSection(section: Int): Int {
            return displayedChild?.userMap?.size ?: 0
        }

        fun onChildChanged(clientChild: ClientChildVM?) {
            if (clientChild == displayedChild) return

            displayedChild = if (clientChild is ChannelVM) clientChild else null
            notifySectionedDataSetChanged()
        }
    }
}