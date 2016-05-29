package com.tilal6991.channels.redux.controller

import android.support.v4.widget.DrawerLayout
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.ChildControllerTransaction
import com.bluelinelabs.conductor.changehandler.SimpleSwapChangeHandler
import com.tilal6991.channels.R
import com.tilal6991.channels.base.storeEvents
import com.tilal6991.channels.redux.Selectors
import com.tilal6991.channels.redux.select
import com.tilal6991.channels.util.bindToLifecycle
import com.tilal6991.channels.util.bindView

class HomeController : BaseController() {

    private val drawerLayout: DrawerLayout by bindView(R.id.drawer_layout)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.controller_home, container, false)
    }

    override fun onViewCreated(view: View) {
        val handler = SimpleSwapChangeHandler(false)
        addChildController(ChildControllerTransaction.builder(ContentController(), R.id.drawer_layout)
                .pushChangeHandler(handler).build())
        addChildController(ChildControllerTransaction.builder(NavigationController(), R.id.drawer_layout)
                .pushChangeHandler(handler).build())

        // Handle drawer state correctly.
        storeEvents.select(Selectors.selectedClient)
                .bindToLifecycle(this)
                .distinctUntilChanged()
                .map { it == null }
                .subscribe {
                    if (it) {
                        drawerLayout.setDrawerLockMode(Gravity.LEFT, DrawerLayout.LOCK_MODE_LOCKED_OPEN)
                        drawerLayout.setDrawerLockMode(Gravity.RIGHT, DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                    } else {
                        drawerLayout.setDrawerLockMode(Gravity.LEFT, DrawerLayout.LOCK_MODE_UNLOCKED)
                        drawerLayout.setDrawerLockMode(Gravity.RIGHT, DrawerLayout.LOCK_MODE_UNLOCKED)

                        drawerLayout.closeDrawers()
                    }
                }
    }
}