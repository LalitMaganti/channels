package co.fusionx.channels.adapter

import android.content.Context
import android.databinding.ObservableList
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import co.fusionx.channels.R

class MainItemAdapter(context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val layoutInflater = LayoutInflater.from(context)
    private var buffer: ObservableList<CharSequence>? = null

    override fun getItemCount(): Int = buffer?.size ?: 0

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder.itemView as TextView).text = buffer!![position]
    }

    override fun onCreateViewHolder(parent: ViewGroup?,
                                    viewType: Int): RecyclerView.ViewHolder? {
        val view = layoutInflater.inflate(R.layout.message_item, parent, false)
        return object : RecyclerView.ViewHolder(view) {}
    }

    fun setBuffer(list: ObservableList<CharSequence>?) {
        buffer = list
    }
}