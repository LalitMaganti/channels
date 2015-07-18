package co.fusionx.channels.view

import android.view.View
import kotlin.properties.Delegates

val View.SUPER_STATE by lazy(LazyThreadSafetyMode.NONE) { "super_state" }