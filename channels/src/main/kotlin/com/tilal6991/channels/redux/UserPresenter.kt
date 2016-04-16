package com.tilal6991.channels.redux

import android.content.Context
import android.content.res.Resources
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import com.tilal6991.channels.R
import com.tilal6991.channels.redux.util.getActionBarHeight
import trikita.anvil.Anvil
import trikita.anvil.DSL.*
import trikita.anvil.recyclerview.Recycler

class UserPresenter(private val context: Context) : Anvil.Renderable {

    private val resources: Resources
        get() = context.resources

    override fun view() {
        xml(R.layout.user_drawer) {
            id(R.id.user_drawer_view)
            size(resources.getDimensionPixelSize(R.dimen.user_drawer_width), MATCH)
            layoutGravity(END)
            backgroundColor(ResourcesCompat.getColor(resources, android.R.color.white, null))
            attr({ v, n, o -> ViewCompat.setElevation(v, n) }, dip(10.0f))

            view {
                id(R.id.user_toolbar)
                size(MATCH, getActionBarHeight(context))
                backgroundColor(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))
            }

            Recycler.view {
                init {
                    Recycler.layoutManager(LinearLayoutManager(context))
                }

                id(R.id.user_list_recycler)
                size(MATCH, MATCH)
                clipToPadding(false)
                padding(0, dip(8), 0, dip(8))
            }
        }
    }
}