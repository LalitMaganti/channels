package co.fusionx.channels.controller

import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.ArraySet
import android.view.MenuItem
import butterknife.bindView
import co.fusionx.channels.R
import co.fusionx.channels.base.relayVM
import co.fusionx.channels.presenter.ActionBarPresenter
import co.fusionx.channels.presenter.EventPresenter
import co.fusionx.channels.presenter.NavigationPresenter
import co.fusionx.channels.presenter.Presenter
import co.fusionx.channels.model.ClientChild
import co.fusionx.channels.model.Client
import co.fusionx.channels.util.addAll
import co.fusionx.channels.view.EventRecyclerView
import co.fusionx.channels.view.NavigationDrawerView
import co.fusionx.channels.viewmodel.ClientChildVM
import co.fusionx.channels.viewmodel.ClientVM

public class MainActivity : AppCompatActivity() {

    private val navDrawerView: NavigationDrawerView by bindView(R.id.navdrawer_view)
    private val eventRecycler: EventRecyclerView by bindView(R.id.event_recycler)
    private val toolbar: Toolbar by bindView(R.id.toolbar)
    private val drawerLayout: DrawerLayout by bindView(R.id.drawer_layout)

    private val presenters: MutableCollection<Presenter> = ArraySet()

    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0)

        presenters.addAll(
                NavigationPresenter(this, navDrawerView),
                EventPresenter(this, eventRecycler),
                ActionBarPresenter(this)
        )

        presenters.forEach { it.setup() }
    }

    fun onClientClick(clientVM: ClientVM) {
        val alreadySelected = relayVM.select(clientVM)
        if (!alreadySelected) {
            drawerLayout.closeDrawers()
        }
    }

    fun onChildClick(child: ClientChildVM) {
        relayVM.selectedClient.get()!!.select(child)
        drawerLayout.closeDrawers()
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
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        actionBarDrawerToggle.syncState()
    }

    override fun onConfigurationChanged(config: Configuration) {
        super.onConfigurationChanged(config)
        actionBarDrawerToggle.onConfigurationChanged(config)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
            actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
}
