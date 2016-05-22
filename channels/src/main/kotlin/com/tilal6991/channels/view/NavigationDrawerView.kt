package com.tilal6991.channels.view

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import butterknife.bindView
import com.tilal6991.channels.R
import com.tilal6991.channels.adapter.NavigationAdapter

class NavigationDrawerView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0) : ScrimInsetsLinearLayout(context, attrs, defStyle) {

    private val recycler: RecyclerView by bindView(R.id.navdrawer_recycler)

    public override fun onFinishInflate() {
        super.onFinishInflate()

        recycler.layoutManager = LinearLayoutManager(context)
    }

    fun setAdapter(adapter: NavigationAdapter) {
        recycler.adapter = adapter
    }
}