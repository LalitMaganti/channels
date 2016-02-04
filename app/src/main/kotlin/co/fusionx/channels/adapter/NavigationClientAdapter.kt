package co.fusionx.channels.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import co.fusionx.channels.R
import co.fusionx.channels.databinding.NavigationClientBinding
import co.fusionx.channels.util.failAssert
import co.fusionx.channels.viewmodel.persistent.ClientVM
import co.fusionx.channels.viewmodel.persistent.RelayVM
import timber.log.Timber

class NavigationClientAdapter(
        private val context: Context,
        private val relayVM: RelayVM,
        private val clientClickListener: (ClientVM) -> Unit) :
        SectionAdapter<NavigationClientAdapter.ClientViewHolder, HeaderViewHolder>() {

    private val inflater: LayoutInflater

    init {
        inflater = LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == HEADER_VIEW_TYPE) {
            return HeaderViewHolder(inflater.inflate(R.layout.recycler_header, parent, false))
        }
        return ClientViewHolder(NavigationClientBinding.inflate(inflater, parent, false))
    }

    override fun onBindItemViewHolder(holder: ClientViewHolder, section: Int, offset: Int) {
        holder.bind(section, offset)
    }

    override fun onBindHeaderViewHolder(holder: HeaderViewHolder, section: Int) {
        if (section == 0) {
            holder.bind(context.getString(R.string.header_active_clients))
        } else if (section == 1) {
            holder.bind(context.getString(R.string.header_inactive_clients))
        } else {
            Timber.asTree().failAssert()
        }
    }

    override fun getItemCountInSection(section: Int): Int {
        return getListForSection(section).size
    }

    override fun isHeaderDisplayedForSection(section: Int): Boolean {
        return true
    }

    override fun getSectionedItemViewType(section: Int, sectionOffset: Int): Int {
        return 20
    }

    override fun getSectionCount(): Int {
        return 2
    }

    private fun getListForSection(section: Int): List<ClientVM> {
        if (section == 0) {
            return relayVM.activeClients
        }
        return relayVM.inactiveClients
    }

    inner class ClientViewHolder(private val binding: NavigationClientBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(section: Int, offset: Int) {
            val item = getListForSection(section)[offset]

            binding.client = item
            binding.root.setOnClickListener { clientClickListener(item) }

            binding.executePendingBindings()
        }
    }
}