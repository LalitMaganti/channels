package co.fusionx.channels.ui

import android.content.res.Configuration
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import butterknife.bindView
import co.fusionx.channels.R
import co.fusionx.channels.base.objectProvider
import co.fusionx.channels.base.relayHost
import co.fusionx.channels.relay.ClientChild
import co.fusionx.channels.relay.ClientHost
import co.fusionx.channels.relay.RelayHost
import co.fusionx.channels.view.EventRecyclerView
import co.fusionx.channels.view.NavigationDrawerView
import kotlin.properties.Delegates

public class MainActivity : AppCompatActivity() {

    private val navDrawerView: NavigationDrawerView by bindView(R.id.navdrawer_view)
    private val eventRecycler: EventRecyclerView by bindView(R.id.event_recycler)
    private val toolbar: Toolbar by bindView(R.id.toolbar)
    private val drawerLayout: DrawerLayout by bindView(R.id.drawer_layout)
    private val appbar: AppBarLayout by bindView(R.id.appbar)

    private var actionBarDrawerToggle: ActionBarDrawerToggle by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        actionBarDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0)
        navDrawerView.callbacks = object : NavigationDrawerView.Callbacks {
            override fun onClientClick(client: ClientHost) {
                val alreadySelected = relayHost.select(client)
                navDrawerView.switchToChildList()

                if (!alreadySelected) {
                    eventRecycler.switchContent()
                    drawerLayout.closeDrawers()
                }
            }

            override fun onChildClick(child: ClientChild) {
                relayHost.selectedClient!!.select(child)
                drawerLayout.closeDrawers()
            }
        }
    }

    /* ActionBarDrawerToggle overrides */
    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        actionBarDrawerToggle.syncState()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        // appbar.setExpanded(true)
    }

    override fun onConfigurationChanged(config: Configuration) {
        super.onConfigurationChanged(config)
        actionBarDrawerToggle.onConfigurationChanged(config)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
            actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
}
