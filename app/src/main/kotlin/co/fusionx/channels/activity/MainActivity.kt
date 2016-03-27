package co.fusionx.channels.activity

import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.drawable.DrawerArrowDrawable
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import butterknife.bindView
import co.fusionx.channels.R
import co.fusionx.channels.base.relayVM
import co.fusionx.channels.configuration.ChannelsConfiguration
import co.fusionx.channels.presenter.*
import co.fusionx.channels.util.addAll
import co.fusionx.channels.view.EventRecyclerView
import co.fusionx.channels.view.NavigationDrawerView
import co.fusionx.channels.viewmodel.persistent.ClientChildVM
import java.util.*

class MainActivity : AppCompatActivity() {

    private val navDrawerView: NavigationDrawerView by bindView(R.id.navdrawer_view)
    private val eventRecycler: EventRecyclerView by bindView(R.id.event_recycler)
    private val toolbar: Toolbar by bindView(R.id.toolbar)
    private val drawerLayout: DrawerLayout by bindView(R.id.drawer_layout)
    private val messageBox: EditText by bindView(R.id.message)
    private val userDrawerView: ViewGroup by bindView(R.id.user_drawer_view)

    private lateinit var navigationPresenter: NavigationPresenter

    private val presenters: MutableCollection<Presenter> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeAsUpIndicator(DrawerArrowDrawable(this))

        navigationPresenter = NavigationPresenter(this, navDrawerView)
        presenters.addAll(
                navigationPresenter,
                ClientChildPresenter(this, messageBox, eventRecycler),
                ActionBarPresenter(this),
                UserListPresenter(this, drawerLayout, userDrawerView)
        )
        presenters.forEach { it.setup(savedInstanceState?.getBundle(it.id)) }

        // If there are no selected server, then start with the drawer open.
        if (relayVM.selectedClients.latest == null) {
            drawerLayout.openDrawer(navDrawerView);
        }
    }

    fun onClientClick(configuration: ChannelsConfiguration) {
        relayVM.select(configuration)
    }

    fun onChildClick(child: ClientChildVM) {
        relayVM.selectedClients.latest!!.select(child)
        drawerLayout.closeDrawer(navDrawerView)
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

    /* ActionBarDrawerToggle overrides */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            drawerLayout.toggle(navDrawerView)
        }
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
    }
}
