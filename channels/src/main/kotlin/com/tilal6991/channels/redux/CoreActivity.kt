package com.tilal6991.channels.redux

import android.os.Bundle
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.drawable.DrawerArrowDrawable
import android.support.v7.widget.Toolbar
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import butterknife.bindView
import com.tilal6991.channels.R
import com.tilal6991.channels.base.storeEvents
import com.tilal6991.channels.view.EventRecyclerView
import com.tilal6991.channels.view.NavigationDrawerView

class CoreActivity : AppCompatActivity() {

    private val navDrawerView: NavigationDrawerView by bindView(R.id.navdrawer_view)
    private val eventRecycler: EventRecyclerView by bindView(R.id.event_recycler)
    private val toolbar: Toolbar by bindView(R.id.toolbar)
    private val drawerLayout: DrawerLayout by bindView(R.id.drawer_layout)
    private val navigationHint: TextView by bindView(R.id.navigation_hint)
    private val messageBox: EditText by bindView(R.id.message)
    private val userDrawerView: ViewGroup by bindView(R.id.user_drawer_view)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(DrawerArrowDrawable(this))
    }
}