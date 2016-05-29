package com.tilal6991.channels.util

import android.view.View
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.rxlifecycle.ControllerEvent
import com.bluelinelabs.conductor.rxlifecycle.ControllerLifecycleProvider
import rx.Observable
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun <V : View> Controller.bindView(id: Int)
        : ReadOnlyProperty<Controller, V> = required(id, viewFinder)

private val Controller.viewFinder: Controller.(Int) -> View?
    get() = { view.findViewById(it) }

private fun viewNotFound(id: Int, desc: KProperty<*>): Nothing =
        throw IllegalStateException("View ID $id for '${desc.name}' not found.")

@Suppress("UNCHECKED_CAST")
private fun <T, V : View> required(id: Int, finder: T.(Int) -> View?)
        = Lazy { t: T, desc -> t.finder(id) as V? ?: viewNotFound(id, desc) }

@Suppress("UNCHECKED_CAST")
private fun <T, V : View> required(ids: IntArray, finder: T.(Int) -> View?)
        = Lazy { t: T, desc -> ids.map { t.finder(it) as V? ?: viewNotFound(it, desc) } }

// Like Kotlin's lazy delegate but the initializer gets the target and metadata passed to it
private class Lazy<T, V>(private val initializer: (T, KProperty<*>) -> V) : ReadOnlyProperty<T, V> {
    private object EMPTY

    private var value: Any? = EMPTY

    override fun getValue(thisRef: T, property: KProperty<*>): V {
        if (value == EMPTY) {
            value = initializer(thisRef, property)
        }
        @Suppress("UNCHECKED_CAST")
        return value as V
    }
}

fun <T> Observable<T>.bindToLifecycle(controller: ControllerLifecycleProvider): Observable<T> = this.compose<T>(controller.bindToLifecycle<T>())

fun <T> Observable<T>.bindUntilEvent(fragment: ControllerLifecycleProvider, event: ControllerEvent): Observable<T> = this.compose<T>(fragment.bindUntilEvent(event))
