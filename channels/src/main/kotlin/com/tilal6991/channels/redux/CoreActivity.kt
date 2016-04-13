package com.tilal6991.channels.redux

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.FrameLayout
import trikita.anvil.Anvil

class CoreActivity : AppCompatActivity() {
    private lateinit var corePresenter: CorePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        corePresenter = CorePresenter(this)
        setContentView(Anvil.mount(FrameLayout(this), corePresenter))
        corePresenter.setup()
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