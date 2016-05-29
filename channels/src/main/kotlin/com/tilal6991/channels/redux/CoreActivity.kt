package com.tilal6991.channels.redux

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.drawable.DrawerArrowDrawable
import android.view.ViewGroup
import com.bluelinelabs.conductor.Conductor
import com.tilal6991.channels.R
import com.tilal6991.channels.redux.controller.HomeController
import org.jetbrains.anko.find

class CoreActivity : AppCompatActivity() {

    /*
    private val navDrawerView: NavigationDrawerView by bindView(R.id.navdrawer_view)
    private val eventRecycler: EventRecyclerView by bindView(R.id.event_recycler)
    private val toolbar: Toolbar by bindView(R.id.toolbar)
    private val navigationHint: TextView by bindView(R.id.navigation_hint)
    private val messageBox: EditText by bindView(R.id.message)
    private val userDrawerView: ViewGroup by bindView(R.id.user_drawer_view)
    */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_core)

        val container = find<ViewGroup>(R.id.content_frame)
        val router = Conductor.attachRouter(this, container, savedInstanceState);
        if (!router.hasRootController()) {
            router.setRoot(HomeController())
        }
    }
}