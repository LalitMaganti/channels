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
import trikita.anvil.DSL.*

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
    return if (index < size()) get(index) else null
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

fun <T> SortedIndexedList<T>.mutate(index: Int, client: T): SortedIndexedList<T> {
    if (get(index) === client) {
        return this
    }
    return set(index, client)
}

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