package com.tilal6991.channels.redux.controller

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tilal6991.channels.R
import com.tilal6991.channels.redux.controller.NavigationClientAdapter
import com.tilal6991.channels.base.relayVM
import com.tilal6991.channels.redux.controller.NavigationAdapter
import org.jetbrains.anko.find

class NavigationController : BaseController() {

    private lateinit var adapter: NavigationAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.view_navigation_drawer, container, false)
    }

    override fun onViewCreated(view: View) {
        val ad = NavigationClientAdapter(view.context)
        ad.setup()

        val recycler = view.find<RecyclerView>(R.id.navdrawer_recycler)
        adapter = NavigationAdapter(view.context, ad)


        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(view.context)
    }
}