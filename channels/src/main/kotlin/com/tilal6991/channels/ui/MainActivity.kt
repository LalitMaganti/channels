package com.tilal6991.channels.ui

import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.drawable.DrawerArrowDrawable
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import butterknife.bindView
import com.tilal6991.channels.R
import com.tilal6991.channels.base.relayVM
import com.tilal6991.channels.configuration.ChannelsConfiguration
import com.tilal6991.channels.ui.*
import com.tilal6991.channels.util.addAll
import com.tilal6991.channels.view.EventRecyclerView
import com.tilal6991.channels.view.NavigationDrawerView
import com.tilal6991.channels.viewmodel.ClientChildVM
import java.util.*

class MainActivity : AppCompatActivity() {

    private val navDrawerView: NavigationDrawerView by bindView(R.id.navdrawer_view)
    private val eventRecycler: EventRecyclerView by bindView(R.id.event_recycler)
    private val toolbar: Toolbar by bindView(R.id.toolbar)
    private val drawerLayout: DrawerLayout by bindView(R.id.drawer_layout)
    private val navigationHint: TextView by bindView(R.id.navigation_hint)
    private val messageBox: EditText by bindView(R.id.message)
    private val userDrawerView: ViewGroup by bindView(R.id.user_drawer_view)

    private lateinit var navigationPresenter: NavigationPresenter
    private lateinit var actionBarPresenter: ActionBarPresenter
    private lateinit var dashboardPresenter: DashboardPresenter

    private val presenters: MutableCollection<Presenter> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(DrawerArrowDrawable(this))

        navigationPresenter = NavigationPresenter(this, drawerLayout, navDrawerView)
        actionBarPresenter = ActionBarPresenter(this)
        dashboardPresenter = DashboardPresenter(this)

        presenters.addAll(
                navigationPresenter,
                ClientChildPresenter(this, messageBox, navigationHint, eventRecycler),
                actionBarPresenter,
                UserListPresenter(this, drawerLayout, userDrawerView),
                dashboardPresenter
        )
        presenters.forEach { it.setup(savedInstanceState?.getBundle(it.id)) }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        presenters.forEach { it.restoreState(savedInstanceState.getBundle(it.id)) }
    }

    override fun onStart() {
        super.onStart()
        presenters.forEach { it.bind() }
    }

    override fun onStop() {
        super.onStop()
        presenters.forEach { it.unbind() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        presenters.forEach { outState.putBundle(it.id, it.saveState()) }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenters.forEach { it.teardown() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_activity, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        return actionBarPresenter.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.toggle(navDrawerView)
                return true
            }
            R.id.menu_action_button -> {
                dashboardPresenter.toggle()
                return true
            }
        }

        delegate
        return super.onOptionsItemSelected(item)
    }

    private fun DrawerLayout.toggle(view: View) {
        if (isDrawerOpen(view)) {
            closeDrawer(view)
        } else {
            openDrawer(view)
        }
    }

    companion object {
        const val REQUEST_EDIT = 100
        const val REQUEST_SETTINGS = 101
    }
}
