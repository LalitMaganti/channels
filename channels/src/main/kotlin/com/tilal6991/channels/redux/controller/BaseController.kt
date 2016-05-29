package com.tilal6991.channels.redux.controller

import android.view.View
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.rxlifecycle.RxController

abstract class BaseController : RxController() {

    init {
        addLifecycleListener(object : LifecycleListener() {
            override fun postCreateView(controller: Controller, view: View) {
                onViewCreated(view)
            }
        })
    }

    open fun onViewCreated(view: View) = Unit
}