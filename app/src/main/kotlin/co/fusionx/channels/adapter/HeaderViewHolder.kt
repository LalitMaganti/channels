package co.fusionx.channels.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView

class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(headerText: String) {
        (itemView as TextView).text = headerText
    }
}