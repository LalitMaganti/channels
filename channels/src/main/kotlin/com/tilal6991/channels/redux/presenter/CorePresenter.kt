package com.tilal6991.channels.redux.presenter

import android.content.res.Resources
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.drawable.DrawerArrowDrawable
import android.support.v7.widget.Toolbar
import android.text.InputType
import android.view.Gravity
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import com.tilal6991.channels.R
import com.tilal6991.channels.base.store
import com.tilal6991.channels.redux.*
import com.tilal6991.channels.redux.state.Channel
import com.tilal6991.channels.redux.util.resolveDimen
import com.tilal6991.channels.view.NavigationDrawerView.Companion.navigationDrawerView
import trikita.anvil.Anvil
import trikita.anvil.Anvil.currentView
import trikita.anvil.DSL
import trikita.anvil.DSL.*
import trikita.anvil.appcompat.v7.AppCompatv7DSL
import trikita.anvil.appcompat.v7.AppCompatv7DSL.*
import trikita.anvil.design.DesignDSL.appBarLayout
import trikita.anvil.design.DesignDSL.coordinatorLayout
import trikita.anvil.recyclerview.v7.RecyclerViewv7DSL.*

class CorePresenter(private val context: AppCompatActivity) : Anvil.Renderable {
    private lateinit var eventPresenter: EventPresenter
    private lateinit var userPresenter: UserPresenter

    private lateinit var clientAdapter: NavigationClientAdapter
    private lateinit var childAdapter: NavigationChildAdapter
    private lateinit var currentAdapter: NavigationAdapter.Child
    private lateinit var navigationAdapter: NavigationAdapter

    private lateinit var subscription: Runnable

    private var navigationLockedState = DrawerLayout.LOCK_MODE_LOCKED_OPEN
    private var userLockedState = DrawerLayout.LOCK_MODE_LOCKED_CLOSED
    private var drawerVisible = true

    val resources: Resources
        get() = context.resources

    fun setup(savedInstanceState: Bundle?) {
        clientAdapter = NavigationClientAdapter(context, currentState.clients) {
            context.store.dispatch(Actions.SelectClient(it.configuration))
            currentAdapter = childAdapter
        }
        clientAdapter.setup()

        childAdapter = NavigationChildAdapter(context) { t, o ->
            context.store.dispatch(Actions.ChangeSelectedChild(t, o))
            drawerVisible = false
        }
        childAdapter.setup()

        navigationAdapter = NavigationAdapter(context, clientAdapter) {
            currentAdapter = if (currentAdapter == clientAdapter) {
                childAdapter
            } else {
                clientAdapter
            }
        }
        currentAdapter = clientAdapter

        eventPresenter = EventPresenter(context)
        eventPresenter.setup()

        userPresenter = UserPresenter(context)
        userPresenter.setup()

        if (savedInstanceState != null) {
            navigationLockedState = savedInstanceState.getInt(NAV_LOCKED_STATE,
                    DrawerLayout.LOCK_MODE_LOCKED_OPEN)
            drawerVisible = savedInstanceState.getBoolean(NAV_VISIBLE, true)
            userLockedState = savedInstanceState.getInt(USER_LOCKED_STATE,
                    DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
    }

    fun bind() {
        subscription = subscribe(context) {
            childAdapter.setData(selectedClient())
            clientAdapter.setData(it.clients)
            navigationAdapter.setData(selectedClient())

            val selectedChild = selectedChild()
            if (selectedChild == null) {
                navigationLockedState = DrawerLayout.LOCK_MODE_LOCKED_OPEN
            } else {
                navigationLockedState = DrawerLayout.LOCK_MODE_UNLOCKED
            }

            if (selectedChild is Channel) {
                userLockedState = DrawerLayout.LOCK_MODE_UNLOCKED
            } else {
                userLockedState = DrawerLayout.LOCK_MODE_LOCKED_CLOSED
            }
        }
        userPresenter.bind()
        eventPresenter.bind()
    }

    fun saveInstanceState(): Bundle {
        val bundle = Bundle()
        bundle.putInt(NAV_LOCKED_STATE, navigationLockedState)
        bundle.putBoolean(NAV_VISIBLE, drawerVisible)
        bundle.putInt(USER_LOCKED_STATE, userLockedState)
        return bundle
    }

    fun unbind() {
        subscription.run()
        userPresenter.unbind()
        eventPresenter.unbind()
    }

    override fun view() {
        linearLayout {
            size(MATCH, MATCH)
            DSL.orientation(LinearLayout.VERTICAL)

            coordinatorLayout {
                id(R.id.main_content)
                size(MATCH, dip(0))
                weight(1.0f)

                eventPresenter.view()

                appCompatTextView {
                    size(MATCH, MATCH)
                    enabled(false)
                    DSL.gravity(CENTER)
                    padding(dip(32))
                    text(R.string.no_child_selected)
                    visibility(selectedChild() == null)
                }

                appBarLayout {
                    size(MATCH, WRAP)

                    xml(R.layout.core_toolbar) {
                        init {
                            val currentView = currentView<Toolbar>()
                            currentView.navigationIcon = DrawerArrowDrawable(context)
                        }

                        val layoutParams = AppBarLayout.LayoutParams(MATCH_PARENT,
                                context.resolveDimen(R.attr.actionBarSize))
                        layoutParams.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
                        layoutParams(layoutParams)

                        navigationOnClickListener {
                            drawerVisible = !drawerVisible
                            Anvil.render()
                        }

                        AppCompatv7DSL.title(selectedChild()?.name ?: "Channels")
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

            recyclerView {
                linearLayoutManager()
                size(MATCH, MATCH)
                adapter(navigationAdapter)
                navigationAdapter.updateContentAdapter(currentAdapter)
            }
        }

        userPresenter.view()

        attr({ v, n, o -> (v as DrawerLayout).setDrawerLockMode(n, Gravity.START) }, navigationLockedState)
        attr({ v, n, o -> (v as DrawerLayout).setDrawerLockMode(n, Gravity.END) }, userLockedState)
        if (navigationLockedState == DrawerLayout.LOCK_MODE_UNLOCKED) {
            attr({ v, n, o ->
                if (n) {
                    (v as DrawerLayout).openDrawer(Gravity.START)
                } else {
                    (v as DrawerLayout).closeDrawer(Gravity.START)
                }
            }, drawerVisible)
        }
    }

    companion object {
        const val NAV_LOCKED_STATE = "nav_locked"
        const val USER_LOCKED_STATE = "user_locked"
        const val NAV_VISIBLE = "nav_visible"
    }
}