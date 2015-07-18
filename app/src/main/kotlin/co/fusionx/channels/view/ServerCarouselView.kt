package co.fusionx.channels.view

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.RelativeLayout
import co.fusionx.channels.R
import com.amulyakhare.textdrawable.TextDrawable
import kotlin.properties.Delegates

/**
 * BUGS: selecting one image and then another very quickly
 */
public class ServerCarouselView(
        context: Context, attrs: AttributeSet?) : RelativeLayout(context, attrs) {
    private val selected by lazy(LazyThreadSafetyMode.NONE) { findViewById(R.id.profile_image) as ImageView }
    private val penultimate by lazy(LazyThreadSafetyMode.NONE) { findViewById(R.id.profile_image_2) as ImageView }
    private val last by lazy(LazyThreadSafetyMode.NONE) { findViewById(R.id.profile_image_3) as ImageView }

    var selectedView: ImageView? = null
    var selectedX: Float = 0f
    var selectedY: Float = 0f

    val clickListener: (View) -> Unit = {
        // Animate the old current away
        fadeAndMoveToNewPosition(selectedView!!, it.x, it.y)

        // Animate the new current in
        expandAndMoveToNewPosition(it, selectedX, selectedY)

        // Update the current view
        selectedView = it as ImageView
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        selected.setImageDrawable(TextDrawable.builder()
                .round()
                .build("Fr", resources.getColor(R.color.colorAccent)))
        penultimate.setImageDrawable(TextDrawable.builder()
                .round()
                .build("Te", resources.getColor(R.color.colorAccent)))
        last.setImageDrawable(TextDrawable.builder()
                .round()
                .build("Sn", resources.getColor(R.color.colorAccent)))

        /* Increase the size of the selected view */
        val selected = if (selectedView == null) selected else selectedView!!
        ViewCompat.setScaleX(selected, 1.75f)
        ViewCompat.setScaleY(selected, 1.75f)

        // Post to the view so that we are sure we have a measurement before we
        // try and get positions
        selected.post {
            selectedView = selected

            selectedX = ViewCompat.getX(selected)
            selectedY = ViewCompat.getY(selected)

            listOf(selected, penultimate, last)
                    .forEach { it.setOnClickListener(clickListener) }
        }
    }

    private fun expandAndMoveToNewPosition(view: View, oldCurrentX: Float, oldCurrentY: Float) {
        // Update the new elevation
        ViewCompat.setElevation(view, 5f)

        // Expand to 1.75x times while moving to the new position and
        // readd the listener when done
        ViewCompat.animate(view)
                .scaleX(1.75f)
                .scaleY(1.75f)
                .x(oldCurrentX)
                .y(oldCurrentY)
                .setDuration(250)
                .setInterpolator(AccelerateDecelerateInterpolator())
    }

    private fun fadeAndMoveToNewPosition(currentView: ImageView,
                                         newCurrentX: Float,
                                         newCurrentY: Float) {
        // Do the fade with half the full animation time
        ViewCompat.animate(currentView)
                .alpha(0f)
                .setDuration(200)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .withEndAction {
                    // Now move to the new position...
                    ViewCompat.setX(currentView, newCurrentX)
                    ViewCompat.setY(currentView, newCurrentY)

                    // ...reset the size...
                    ViewCompat.setScaleX(currentView, 1f)
                    ViewCompat.setScaleY(currentView, 1f)

                    // ...and the elevation
                    ViewCompat.setElevation(currentView, 0f)

                    // Fade the image back in and re-add the listener
                    ViewCompat.animate(currentView)
                            .setDuration(100)
                            .alpha(1f)
                            .setInterpolator(AccelerateDecelerateInterpolator())
                }
    }
}