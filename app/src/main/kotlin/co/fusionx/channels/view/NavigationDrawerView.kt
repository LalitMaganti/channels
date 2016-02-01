package co.fusionx.channels.view

import android.content.Context
import android.support.design.internal.ScrimInsetsFrameLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import butterknife.bindView
import co.fusionx.channels.R
import co.fusionx.channels.adapter.NavigationAdapter

public class NavigationDrawerView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null) : ScrimInsetsFrameLayout(context, attrs) {

    private val recycler: RecyclerView by bindView(R.id.navdrawer_recycler)

    public override fun onFinishInflate() {
        super.onFinishInflate()

        recycler.layoutManager = LinearLayoutManager(context)
    }

    fun setAdapter(adapter: NavigationAdapter) {
        recycler.adapter = adapter
    }
}