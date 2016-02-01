package co.fusionx.channels.adapter

import android.content.Context
import android.databinding.Observable
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import co.fusionx.channels.R
import co.fusionx.channels.base.relayHost
import co.fusionx.channels.relay.ClientChild
import co.fusionx.channels.relay.ClientHost
import co.fusionx.channels.relay.RelayHost
import timber.log.Timber

public class NavigationAdapter(
        private val context: Context,
        clientClickListener: (ClientHost) -> Unit,
        childClickListener: (ClientChild) -> Unit) :
        RecyclerView.Adapter<NavigationAdapter.ViewHolder>() {

    internal var currentType: Int = VIEW_TYPE_CLIENT
        private set

    private val inflater: LayoutInflater
    private val relayHost: RelayHost
        get() = context.relayHost

    private val childAdapter: NavigationChildAdapter
    private val clientAdapter: NavigationClientAdapter

    private val headerCount = 1
    private val contentCount: Int
        get() {
            if (currentType == VIEW_TYPE_CHILD) {
                return childAdapter.itemCount
            } else {
                return clientAdapter.itemCount
            }
        }

    private val selectedClientCallback = object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            val client = relayHost.selectedClient.get()
            updateCurrentType(if (client == null) VIEW_TYPE_CLIENT else VIEW_TYPE_CHILD)
        }
    }
    private val observer = ChildAdapterObserver()

    init {
        inflater = LayoutInflater.from(context)

        clientAdapter = NavigationClientAdapter(context) {
            clientClickListener(it)

            // Even if the client does not change we still want to switch this view.
            if (it == relayHost.selectedClient.get()) {
                updateCurrentType(VIEW_TYPE_CHILD)
            }
        }
        childAdapter = NavigationChildAdapter(context, childClickListener)
    }

    fun startObserving() {
        clientAdapter.startObserving()
        clientAdapter.registerAdapterDataObserver(observer)

        relayHost.selectedClient.addOnPropertyChangedCallback(selectedClientCallback)
    }

    fun stopObserving() {
        if (currentType == VIEW_TYPE_CHILD) {
            childAdapter.stopObserving()
            childAdapter.unregisterAdapterDataObserver(observer)
        } else {
            clientAdapter.stopObserving()
            clientAdapter.unregisterAdapterDataObserver(observer)
        }

        relayHost.selectedClient.removeOnPropertyChangedCallback(selectedClientCallback)
    }

    private fun updateCurrentType(type: Int) {
        if (type == currentType) return

        // Stop observing everything old.
        if (currentType == VIEW_TYPE_CLIENT) {
            clientAdapter.stopObserving()
            clientAdapter.unregisterAdapterDataObserver(observer)
        } else if (currentType == VIEW_TYPE_CHILD) {
            childAdapter.stopObserving()
            childAdapter.unregisterAdapterDataObserver(observer)
        } else {
            Timber.e("This should not be happening.")
        }

        // Swap the old items out and the new items in.
        notifyItemRangeRemoved(headerCount, contentCount)
        currentType = type
        notifyItemRangeInserted(headerCount, contentCount)

        // Start observing everything new.
        if (currentType == VIEW_TYPE_CLIENT) {
            clientAdapter.startObserving()
            clientAdapter.registerAdapterDataObserver(observer)
        } else if (currentType == VIEW_TYPE_CHILD) {
            childAdapter.startObserving()
            childAdapter.registerAdapterDataObserver(observer)
        } else {
            Timber.e("This should not be happening.")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, type: Int): ViewHolder? = when (type) {
        VIEW_TYPE_HEADER -> HeaderViewHolder(
                inflater.inflate(R.layout.navigation_header_clients, parent, false))
        VIEW_TYPE_CLIENT -> clientAdapter.onCreateViewHolder(parent, type)
        VIEW_TYPE_CHILD -> childAdapter.onCreateViewHolder(parent, type)
        else -> null
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val viewType = getItemViewType(position)
        if (viewType == VIEW_TYPE_CHILD || viewType == VIEW_TYPE_CLIENT) {
            holder.bind(position - headerCount)
        } else {
            holder.bind(position)
        }
    }

    override fun onViewRecycled(holder: ViewHolder) {
        val position = holder.adapterPosition
        if (position == -1) return

        val viewType = getItemViewType(position)
        if (viewType == VIEW_TYPE_CHILD || viewType == VIEW_TYPE_CLIENT) {
            holder.unbind(position - headerCount)
        } else {
            holder.unbind(position)
        }
    }

    override fun getItemCount(): Int {
        return headerCount + contentCount
    }

    override fun getItemViewType(position: Int): Int {
        if (position < headerCount) {
            return VIEW_TYPE_HEADER
        }
        return currentType
    }

    inner class HeaderViewHolder(itemView: View) : ViewHolder(itemView) {

        private val background = itemView.findViewById(R.id.view_navigation_drawer_header_image)

        private val listener = object : Observable.OnPropertyChangedCallback() {
            override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                onChanged()
            }
        }

        override fun bind(position: Int) {
            relayHost.selectedClient.addOnPropertyChangedCallback(listener)
            onChanged()
        }

        override fun unbind(position: Int) {
            relayHost.selectedClient.removeOnPropertyChangedCallback(listener)
        }

        fun onChanged() {
            if (relayHost.selectedClient.get() == null) {
                background.setOnClickListener(null)
            } else {
                background.setOnClickListener {
                    if (currentType == VIEW_TYPE_CHILD) {
                        updateCurrentType(VIEW_TYPE_CLIENT)
                    } else {
                        updateCurrentType(VIEW_TYPE_CHILD)
                    }
                }
            }
        }
    }

    abstract class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        public abstract fun bind(position: Int)
        public open fun unbind(position: Int) = Unit
    }

    companion object {
        const val VIEW_TYPE_HEADER: Int = 0
        const val VIEW_TYPE_CLIENT: Int = 1
        const val VIEW_TYPE_CHILD: Int = 2

        const val PARCEL_CURRENT_TYPE: String = "current_type"
    }

    private inner class ChildAdapterObserver : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            notifyDataSetChanged()
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
            notifyItemRangeChanged(positionStart + headerCount, itemCount)
        }

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
            notifyItemRangeChanged(positionStart + headerCount, itemCount, payload)
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            notifyItemRangeInserted(positionStart + headerCount, itemCount)
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            notifyItemRangeRemoved(positionStart + headerCount, itemCount)
        }

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
            for (i in 0..itemCount - 1) {
                notifyItemMoved(fromPosition + i + headerCount, toPosition + i + headerCount)
            }
        }
    }

    fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putInt(PARCEL_CURRENT_TYPE, currentType)
        return bundle
    }

    fun onRestoreInstanceState(parcelable: Parcelable) {
        if (parcelable is Bundle) {
            updateCurrentType(parcelable.getInt(PARCEL_CURRENT_TYPE, VIEW_TYPE_CLIENT))
        }
    }
}