package com.tilal6991.channels.redux

import android.content.res.Resources
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.drawable.DrawerArrowDrawable
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.text.InputType
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import com.tilal6991.channels.R
import com.tilal6991.channels.view.EventRecyclerView
import com.tilal6991.channels.view.NavigationDrawerView.Companion.navigationDrawerView
import trikita.anvil.Anvil
import trikita.anvil.Anvil.currentView
import trikita.anvil.DSL
import trikita.anvil.DSL.*
import trikita.anvil.appcompat.v7.AppCompatv7DSL.*
import trikita.anvil.design.DesignDSL.appBarLayout
import trikita.anvil.design.DesignDSL.coordinatorLayout
import trikita.anvil.recyclerview.Recycler

class CorePresenter(private val context: AppCompatActivity) : Anvil.Renderable {
    private var clientAdapter: NavigationClientAdapter? = null
    private var childAdapter: NavigationChildAdapter? = null
    private var currentAdapter: NavigationAdapter.Child? = null
    private var navigationAdapter: NavigationAdapter? = null
    private var eventAdapter: MainItemAdapter? = null

    private lateinit var subscription: Runnable

    private var locked = DrawerLayout.LOCK_MODE_LOCKED_OPEN
    private var drawerVisible = true

    val resources: Resources
        get() = context.resources

    fun setup() {
        clientAdapter = NavigationClientAdapter(context, currentState.clients)
        clientAdapter!!.setup()

        childAdapter = NavigationChildAdapter(context)
        childAdapter!!.setup()

        navigationAdapter = NavigationAdapter(context, clientAdapter!!) {
            currentAdapter = if (currentAdapter == clientAdapter) {
                childAdapter
            } else {
                clientAdapter
            }
            navigationAdapter?.updateContentAdapter(currentAdapter!!)
        }
        currentAdapter = clientAdapter
        navigationAdapter?.setHasStableIds(true)

        eventAdapter = MainItemAdapter(context)
        eventAdapter?.setData(selectedChild()?.buffer)
        eventAdapter?.setHasStableIds(true)
    }

    fun bind() {
        subscription = subscribe(context) {
            childAdapter?.setData(selectedClient())
            clientAdapter?.setData(it.clients)
            eventAdapter?.setData(selectedChild()?.buffer)
            navigationAdapter?.setData(selectedClient())

            if (selectedChild() == null) {
                locked = DrawerLayout.LOCK_MODE_LOCKED_OPEN
            } else {
                locked = DrawerLayout.LOCK_MODE_UNLOCKED
            }
        }
    }

    fun unbind() {
        subscription.run()
    }

    override fun view() {
        val selectedChildBuffer = selectedChild()?.buffer

        linearLayout {
            size(MATCH, MATCH)
            DSL.orientation(LinearLayout.VERTICAL)

            coordinatorLayout {
                id(R.id.main_content)
                size(MATCH, dip(0))
                weight(1.0f)

                v(EventRecyclerView::class.java) {
                    init {
                        Recycler.layoutManager(LinearLayoutManager(context))
                    }

                    val layoutParams = CoordinatorLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                    layoutParams.behavior = AppBarLayout.ScrollingViewBehavior()
                    layoutParams(layoutParams)

                    id(R.id.event_recycler)
                    padding(dip(8))
                    clipToPadding(false)
                    visibility(selectedChildBuffer != null)

                    Recycler.adapter(eventAdapter)
                }

                appCompatTextView {
                    size(MATCH, MATCH)
                    enabled(false)
                    DSL.gravity(CENTER)
                    padding(dip(32))
                    text(R.string.no_child_selected)
                    visibility(selectedChildBuffer == null)
                }

                appBarLayout {
                    size(MATCH, WRAP)

                    xml(R.layout.core_toolbar) {
                        init {
                            val currentView = currentView<Toolbar>()
                            currentView.navigationIcon = DrawerArrowDrawable(context)
                        }

                        val layoutParams = AppBarLayout.LayoutParams(MATCH_PARENT, getActionBarHeight())
                        layoutParams.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
                        layoutParams(layoutParams)

                        navigationOnClickListener {
                            drawerVisible = !drawerVisible
                            Anvil.render()
                        }

                        title(selectedChild()?.name ?: "Channels")
                        subtitle(selectedClient()?.configuration?.name)
                        backgroundColor(ResourcesCompat.getColor(resources, R.color.colorPrimary, null))
                    }
                }
            }

            frameLayout {
                size(MATCH, WRAP)

                appCompatEditText {
                    size(MATCH, WRAP)

                    hint(R.string.message_hint)
                    DSL.imeOptions(EditorInfo.IME_ACTION_SEND)
                    DSL.inputType(InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE or
                            InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
                }
            }
        }

        navigationDrawerView {
            size(resources.getDimensionPixelSize(R.dimen.navigation_drawer_width), MATCH)
            attr({ v, n, o -> (v.layoutParams as DrawerLayout.LayoutParams).gravity = n }, START)
            backgroundColor(ResourcesCompat.getColor(
                    resources, R.color.navigation_background_color, null))
            fitsSystemWindows(true)

            Recycler.view {
                init {
                    Recycler.layoutManager(LinearLayoutManager(context))
                }

                size(MATCH, MATCH)
                Recycler.adapter(navigationAdapter)
            }
        }

        attr({ v, n, o -> (v as DrawerLayout).setDrawerLockMode(n) }, locked)
        if (locked == DrawerLayout.LOCK_MODE_UNLOCKED) {
            attr({ v, n, o ->
                if (n) {
                    (v as DrawerLayout).openDrawer(Gravity.START)
                } else {
                    (v as DrawerLayout).closeDrawer(Gravity.START)
                }
            }, drawerVisible)
        }
    }

    private fun getActionBarHeight(): Int {
        // Calculate ActionBar height
        val tv = TypedValue();
        if (context.theme.resolveAttribute(R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics);
        }
        return 0
    }
}