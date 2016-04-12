package com.tilal6991.channels.redux

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import trikita.anvil.DSL.text
import trikita.anvil.DSL.textView
import trikita.anvil.RenderableView
import trikita.anvil.recyclerview.Recycler

class CoreActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(CorePresenter(this))
    }
}