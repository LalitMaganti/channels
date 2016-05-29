package com.tilal6991.channels.redux.controller

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.rxlifecycle.ControllerEvent
import com.tilal6991.channels.R
import com.tilal6991.channels.adapter.SectionAdapter
import com.tilal6991.channels.base.storeEvents
import com.tilal6991.channels.redux.select
import com.tilal6991.channels.redux.util.TransactingAdapterHelper
import com.tilal6991.channels.util.bindUntilEvent
import org.jetbrains.anko.find

class NavigationController : BaseController() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.view_navigation_drawer, container, false)
    }

    override fun onViewCreated(view: View) {
        val clientAdapter = NavigationClientAdapter(view.context)
        clientAdapter.setup()

        val activeHelper = TransactingAdapterHelper(SectionAdapter.ObserverProxy(0, clientAdapter))
        val inactiveHelper = TransactingAdapterHelper(SectionAdapter.ObserverProxy(1, clientAdapter))

        val recycler = view.find<RecyclerView>(R.id.navdrawer_recycler)
        val adapter = NavigationAdapter(view.context, clientAdapter)

        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(view.context)

        storeEvents.select { it.clients }
                .subscribe {
                    clientAdapter.active(it)
                    activeHelper.onNewList(it)

                    // TODO(tilal6991) Remove inactive stuff.
                    clientAdapter.inactive(it)
                    inactiveHelper.onNewList(it)
                }
    }
}