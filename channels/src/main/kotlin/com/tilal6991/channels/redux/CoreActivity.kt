package com.tilal6991.channels.redux

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import android.widget.FrameLayout
import com.tilal6991.channels.R
import com.tilal6991.channels.redux.presenter.CorePresenter
import trikita.anvil.Anvil

class CoreActivity : AppCompatActivity() {
    private lateinit var corePresenter: CorePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        corePresenter = CorePresenter(this)
        corePresenter.setup()

        val parent = findViewById(android.R.id.content) as ViewGroup
        val drawerLayout = layoutInflater.inflate(R.layout.activity_core, parent, false)
        setContentView(Anvil.mount(drawerLayout, corePresenter))

        Anvil.render()
    }

    override fun onStart() {
        super.onStart()

        corePresenter.bind()
    }

    override fun onStop() {
        super.onStop()

        corePresenter.unbind()
    }
}