package com.tilal6991.channels.redux

import android.content.Context
import android.support.v4.widget.TextViewCompat
import android.support.v7.widget.RecyclerView
import android.widget.TextView
import com.github.andrewoma.dexx.collection.IndexedList
import com.tilal6991.channels.redux.util.resolveTextAppearance
import trikita.anvil.DSL.*
import trikita.anvil.RenderableRecyclerViewAdapter
import trikita.anvil.appcompat.v7.AppCompatv7DSL.appCompatTextView

class MainItemAdapter(private val context: Context) : RenderableRecyclerViewAdapter() {

    private var buffer: IndexedList<CharSequence>? = null

    override fun getItemCount(): Int {
        return buffer?.size() ?: 0
    }

    override fun view(holder: RecyclerView.ViewHolder) {
        appCompatTextView {
            size(MATCH, WRAP)
            padding(dip(4), 0, dip(4), dip(4))
            textIsSelectable(true)
            attr({ v, n, o -> TextViewCompat.setTextAppearance((v as TextView), n) },
                    context.resolveTextAppearance(android.R.attr.textAppearanceSmall))

            val position = holder.adapterPosition
            text(buffer?.get(position))
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun setBuffer(list: IndexedList<CharSequence>?)  {
        if (buffer === list) {
            return
        }

        buffer = list
        notifyDataSetChanged()
    }
}