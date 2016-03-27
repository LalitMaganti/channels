package co.fusionx.channels.presenter

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import co.fusionx.channels.R
import co.fusionx.channels.activity.MainActivity
import co.fusionx.channels.adapter.HeaderViewHolder
import co.fusionx.channels.adapter.SectionAdapter
import co.fusionx.channels.databinding.ActionsItemBinding

class ActionsPresenter(override val activity: MainActivity) : Presenter {
    override val id: String
        get() = "actions"

    private lateinit var dialog: BottomSheetDialog

    fun toggle() {
        if (dialog.isShowing) {
            dialog.dismiss()
        } else {
            dialog.show()
        }
    }

    override fun setup(savedState: Bundle?) {
        dialog = BottomSheetDialog(activity, R.style.Theme_Design_Light_BottomSheetDialog)

        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.actions_layout, null, false)

        val recycler = view.findViewById(R.id.actions_recycler) as RecyclerView
        val gridLayoutManager = GridLayoutManager(activity, 4)
        recycler.layoutManager = gridLayoutManager

        val adapter = Adapter(activity)
        recycler.adapter = adapter
        adapter.setup()

        gridLayoutManager.spanSizeLookup =
                adapter.getWrappedSpanSizeLookup(object : SectionAdapter.GridSpanSizeLookup {
                    override fun getItemSpanSize(section: Int, position: Int): Int {
                        return 1
                    }

                    override fun getHeaderSpanSize(section: Int): Int {
                        return gridLayoutManager.spanCount
                    }
                })

        dialog.setContentView(view)
    }

    override fun restoreState(bundle: Bundle) {
        val showing = bundle.getBoolean(SHOWING, false)
        if (showing) {
            dialog.show()
        }
    }

    override fun saveState(): Bundle {
        val bundle = Bundle()
        bundle.putBoolean(SHOWING, dialog.isShowing)
        return bundle
    }

    class Adapter(private val context: Context) : SectionAdapter<Adapter.ItemViewHolder, HeaderViewHolder>() {
        private val layoutInflater = LayoutInflater.from(context)

        val actionDrawableColor: Int

        init {
            val attribute = intArrayOf(R.attr.actionDrawableColor)
            val array = context.theme.obtainStyledAttributes(attribute)
            actionDrawableColor = array.getColor(0, Color.TRANSPARENT)
            array.recycle()
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
            if (viewType == HEADER_VIEW_TYPE) {
                return HeaderViewHolder(layoutInflater.inflate(R.layout.recycler_header, parent, false))
            }
            return ItemViewHolder(ActionsItemBinding.inflate(layoutInflater, parent, false))
        }

        override fun onBindHeaderViewHolder(holder: HeaderViewHolder, section: Int) {
            holder.bind("Test")
        }

        override fun onBindItemViewHolder(holder: ItemViewHolder, section: Int, offset: Int) {
            holder.bind(R.drawable.ic_cancel_black_72dp to R.string.user_auth)
        }

        override fun getSectionCount(): Int {
            return 5
        }

        override fun getItemCountInSection(section: Int): Int {
            return 10
        }

        override fun isHeaderDisplayedForSection(section: Int): Boolean {
            return true
        }

        inner class ItemViewHolder(
                private val binding: ActionsItemBinding) : RecyclerView.ViewHolder(binding.root) {

            fun bind(data: Pair<Int, Int>) {
                val drawable = itemView.context.getDrawable(data.first)
                val compat = DrawableCompat.wrap(drawable)
                compat.setTint(actionDrawableColor)

                binding.actionImage.setImageDrawable(compat)
                binding.actionText.setText(data.second)
            }
        }
    }

    companion object {
        const val SHOWING = "showing"
    }
}