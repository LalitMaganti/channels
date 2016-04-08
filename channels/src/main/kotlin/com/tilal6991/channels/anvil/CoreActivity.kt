package com.tilal6991.channels.anvil

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.tilal6991.channels.R
import trikita.anvil.BaseDSL.xml
import trikita.anvil.RenderableView

class CoreActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(object : RenderableView(this) {
            override fun view() {
                xml(R.layout.activity_main) {

                }
            }
        })
    }
}