package com.tilal6991.channels.redux.controller

import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.drawable.DrawerArrowDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tilal6991.channels.R
import org.jetbrains.anko.find

class ContentController : BaseController() {

    private val supportActivity: AppCompatActivity
        get() = activity as AppCompatActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
        return inflater.inflate(R.layout.controller_content, container, false)
    }

    override fun onViewCreated(view: View) {
        supportActivity.setSupportActionBar(view.find(R.id.toolbar))

        val actionBar = supportActivity.supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeAsUpIndicator(DrawerArrowDrawable(activity))
    }
}