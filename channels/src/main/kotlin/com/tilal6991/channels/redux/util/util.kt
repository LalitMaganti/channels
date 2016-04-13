package com.tilal6991.channels.redux.util

import android.content.Context
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.widget.TextViewCompat
import android.util.TypedValue
import android.widget.TextView
import com.github.andrewoma.dexx.collection.IndexedList
import com.github.andrewoma.dexx.collection.IndexedLists
import com.github.andrewoma.dexx.collection.Traversable
import com.tilal6991.channels.R
import com.tilal6991.channels.configuration.ChannelsConfiguration
import com.tilal6991.channels.redux.state.*
import com.tilal6991.channels.util.failAssert
import com.tilal6991.relay.MoreStringUtils
import com.tilal6991.relay.ReplyCodes
import timber.log.Timber
import trikita.anvil.DSL.*
import java.util.*

sealed class Either<out A, out B> private constructor() {
    class Left<A>(val value: A) : Either<A, Nothing>()
    class Right<B>(val value: B) : Either<Nothing, B>()
}

fun Context.resolveDrawable(attr: Int): Int {
    val tv = TypedValue();
    if (theme.resolveAttribute(attr, tv, true)) {
        return tv.resourceId
    }
    return 0
}

fun Context.resolveTextAppearance(attr: Int): Int {
    val tv = TypedValue();
    if (theme.resolveAttribute(attr, tv, true)) {
        return tv.resourceId
    }
    return 0
}

fun Context.resolveColor(attr: Int): Int {
    val tv = TypedValue();
    if (theme.resolveAttribute(attr, tv, true)) {
        return ResourcesCompat.getColor(resources, tv.resourceId, null)
    }
    return 0
}

fun <T : Any> IndexedList<T>.getOrNull(index: Int): T? {
    return if (index >= 0 && index < size()) get(index) else null
}

fun <T> IndexedList<T>.pullToFront(item: T): IndexedList<T> {
    val i = indexOf(item)
    if (i == 0) {
        return this
    } else if (i < 0) {
        return prepend(item)
    }
    return IndexedLists.builder<T>()
            .add(item)
            .addAll(take(i) as Traversable<T>)
            .addAll(drop(i + 1) as Traversable<T>)
            .build()
}

fun GlobalState.mutateSelected(transformer: (Client) -> Client?): GlobalState {
    val item = selectedClients.getOrNull(0) ?: return this
    return mutate(clients.clientMutate(item, transformer))
}

fun SortedIndexedList<Client>.clientMutate(
        item: ChannelsConfiguration,
        transformer: (Client) -> Client?): SortedIndexedList<Client> {
    return binaryMutate(item, { it.configuration }) {
        if (it == null) null else transformer(it)
    }
}

fun <T, E : Comparable<E>> SortedIndexedList<T>.binaryMutate(
        item: E,
        selector: (T) -> E,
        transformer: (T?) -> T?): SortedIndexedList<T> {
    return mutate(binarySearch(item, selector), transformer)
}

fun statusToResource(status: Int): Int = when (status) {
    Client.STATUS_CONNECTED -> R.string.status_connected
    Client.STATUS_STOPPED -> R.string.status_disconnected
    Client.STATUS_DISCONNECTED -> R.string.status_disconnected
    Client.STATUS_CONNECTING -> R.string.status_connecting
    Client.STATUS_REGISTERING -> R.string.status_registering
    Client.STATUS_RECONNECTING -> R.string.status_reconnecting
    else -> R.string.app_name
}

fun <T> SortedIndexedList<T>.mutate(index: Int,
                                    transformer: (T?) -> T?): SortedIndexedList<T> {
    if (index < 0) {
        val new = transformer(null)
        if (new == null) {
            Timber.asTree().failAssert()
            return this
        }
        return add(new)
    }

    val old = get(index)
    val new = transformer(old)
    if (old === new || new == null) {
        return this
    }
    return set(index, new)
}

fun String.nickFromPrefix(): String {
    return MoreStringUtils.nickFromPrefix(this)
}

fun Channel.append(text: String?): Channel {
    if (text == null) {
        return this
    }
    return copy(buffer = buffer.append(text))
}

fun Server.append(text: String?): Server {
    if (text == null) {
        return this
    }
    return copy(buffer = buffer.append(text))
}

internal fun Int.displayCode(): Boolean {
    return displayedCodes.contains(this)
}

private val displayedCodes: Set<Int> = arrayOf(
        ReplyCodes.RPL_YOURHOST, ReplyCodes.RPL_CREATED, ReplyCodes.RPL_MYINFO,
        ReplyCodes.RPL_LUSERCLIENT, ReplyCodes.RPL_LUSEROP, ReplyCodes.RPL_LUSERUNKNOWN,
        ReplyCodes.RPL_LUSERCHANNELS, ReplyCodes.RPL_LUSERME, ReplyCodes.RPL_LOCALUSERS,
        ReplyCodes.RPL_GLOBALUSERS, ReplyCodes.RPL_STATSCONN, ReplyCodes.RPL_MOTDSTART,
        ReplyCodes.RPL_MOTD, ReplyCodes.RPL_ENDOFMOTD
).toCollection(HashSet())

fun recyclerHeader(context: Context, id: Int) {
    textView {
        size(MATCH, WRAP)
        gravity(CENTER_VERTICAL or START)
        maxLines(1)
        padding(dip(16), dip(12), dip(16), 0)
        attr({ v, n, o -> TextViewCompat.setTextAppearance((v as TextView), n) },
                R.style.TextAppearance_AppCompat_Body2)
        textColor(context.resolveColor(android.R.attr.textColorSecondary))
        text(id)
    }
}