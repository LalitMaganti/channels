package co.fusionx.channels.presenter

import android.app.Activity
import android.databinding.ObservableField
import android.os.Bundle
import android.view.View
import co.fusionx.channels.base.relayVM
import co.fusionx.channels.viewmodel.ClientChildVM
import co.fusionx.channels.viewmodel.SelectedClientsVM
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

interface Bindable {
    fun setup() = Unit
    fun bind() = Unit
    fun unbind() = Unit
    fun teardown() = Unit
}

interface Presenter {
    val activity: Activity
    val id: String

    val selectedClientsVM: SelectedClientsVM
        get() = activity.relayVM.selectedClients
    val selectedChild: ObservableField<ClientChildVM>?
        get() = selectedClientsVM.latest?.selectedChild

    fun setup(savedState: Bundle?) = Unit
    fun restoreState(bundle: Bundle) = Unit
    fun bind() = Unit
    fun unbind() = Unit
    fun saveState(): Bundle = Bundle.EMPTY
    fun teardown() = Unit

    fun getString(id: Int): String {
        return activity.getString(id)
    }

    fun getString(id: Int, vararg args: Any): String {
        return activity.getString(id, args)
    }

    fun getQuantityString(id: Int, quantity: Int): String {
        return activity.resources.getQuantityString(id, quantity)
    }
}

private fun viewNotFound(id: Int, desc: KProperty<*>): Nothing =
        throw IllegalStateException("View ID $id for '${desc.name}' not found.")

fun <V : View> View.bindView(id: Int): ReadOnlyProperty<Presenter, V> =
        Lazy { t, desc -> findViewById(id) as V? ?: viewNotFound(id, desc) }

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

