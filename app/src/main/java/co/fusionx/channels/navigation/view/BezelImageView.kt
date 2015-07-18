/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package co.fusionx.channels.navigation.view

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.widget.ImageView
import co.fusionx.channels.R

/**
 * An [android.widget.ImageView] that draws its contents inside a mask and draws a border
 * drawable on top. This is useful for applying a beveled look to image contents, but is also
 * flexible enough for use with other desired aesthetics.
 */
public class BezelImageView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0) : ImageView(context, attrs, defStyle) {

    private val mBlackPaint: Paint
    private val mMaskedPaint: Paint

    private var mBounds: Rect? = null
    private var mBoundsF: RectF? = null

    private val mBorderDrawable: Drawable?
    private val mMaskDrawable: Drawable?

    private var mDesaturateColorFilter: ColorMatrixColorFilter? = null
    private var mDesaturateOnPress = false

    private var mCacheValid = false
    private var mCacheBitmap: Bitmap? = null
    private var mCachedWidth: Int = 0
    private var mCachedHeight: Int = 0

    init {
        // Attribute initialization
        val a = context.obtainStyledAttributes(attrs, R.styleable.BezelImageView, defStyle, 0)

        mMaskDrawable = a.getDrawable(R.styleable.BezelImageView_maskDrawable)
        mMaskDrawable?.callback = this

        mBorderDrawable = a.getDrawable(R.styleable.BezelImageView_borderDrawable)
        mBorderDrawable?.callback = this

        mDesaturateOnPress = a.getBoolean(R.styleable.BezelImageView_desaturateOnPress, mDesaturateOnPress)

        a.recycle()

        // Other initialization
        mBlackPaint = Paint()
        mBlackPaint.color = -16777216

        mMaskedPaint = Paint()
        mMaskedPaint.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_IN))

        // Always want a cache allocated.
        mCacheBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

        if (mDesaturateOnPress) {
            // Create a desaturate color filter for pressed state.
            val cm = ColorMatrix()
            cm.setSaturation(0f)
            mDesaturateColorFilter = ColorMatrixColorFilter(cm)
        }
    }

    override fun setFrame(l: Int, t: Int, r: Int, b: Int): Boolean {
        val changed = super.setFrame(l, t, r, b)
        mBounds = Rect(0, 0, r - l, b - t)
        mBoundsF = RectF(mBounds)

        mBorderDrawable?.bounds = mBounds
        mMaskDrawable?.bounds = mBounds

        if (changed) {
            mCacheValid = false
        }

        return changed
    }

    override fun onDraw(canvas: Canvas) {
        if (mBounds == null) return

        val width = mBounds!!.width()
        val height = mBounds!!.height()

        if (width == 0 || height == 0) return

        if (!mCacheValid || width != mCachedWidth || height != mCachedHeight) {
            // Need to redraw the cache
            if (width == mCachedWidth && height == mCachedHeight) {
                // Have a correct-sized bitmap cache already allocated. Just erase it.
                mCacheBitmap!!.eraseColor(0)
            } else {
                // Allocate a new bitmap with the correct dimensions.
                mCacheBitmap!!.recycle()
                //noinspection AndroidLintDrawAllocation
                mCacheBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                mCachedWidth = width
                mCachedHeight = height
            }

            val cacheCanvas = Canvas(mCacheBitmap)
            if (mMaskDrawable != null) {
                val sc = cacheCanvas.save()
                mMaskDrawable.draw(cacheCanvas)
                mMaskedPaint.setColorFilter(if ((mDesaturateOnPress && isPressed))
                    mDesaturateColorFilter
                else
                    null)
                cacheCanvas.saveLayer(mBoundsF, mMaskedPaint, Canvas.HAS_ALPHA_LAYER_SAVE_FLAG or Canvas.FULL_COLOR_LAYER_SAVE_FLAG)
                super.onDraw(cacheCanvas)
                cacheCanvas.restoreToCount(sc)
            } else if (mDesaturateOnPress && isPressed) {
                val sc = cacheCanvas.save()
                cacheCanvas.drawRect(0f, 0f, mCachedWidth.toFloat(), mCachedHeight.toFloat(), mBlackPaint)
                mMaskedPaint.setColorFilter(mDesaturateColorFilter)
                cacheCanvas.saveLayer(mBoundsF, mMaskedPaint, Canvas.HAS_ALPHA_LAYER_SAVE_FLAG or Canvas.FULL_COLOR_LAYER_SAVE_FLAG)
                super.onDraw(cacheCanvas)
                cacheCanvas.restoreToCount(sc)
            } else {
                super.onDraw(cacheCanvas)
            }

            mBorderDrawable?.draw(cacheCanvas)
        }

        // Draw from cache
        canvas.drawBitmap(mCacheBitmap, mBounds!!.left.toFloat(), mBounds!!.top.toFloat(), null)
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        if (mBorderDrawable != null && mBorderDrawable.isStateful) {
            mBorderDrawable.setState(drawableState)
        }
        if (mMaskDrawable != null && mMaskDrawable.isStateful) {
            mMaskDrawable.setState(drawableState)
        }
        if (isDuplicateParentStateEnabled) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    override fun invalidateDrawable(who: Drawable) {
        if (who === mBorderDrawable || who === mMaskDrawable) {
            invalidate()
        } else {
            super.invalidateDrawable(who)
        }
    }

    override fun verifyDrawable(who: Drawable): Boolean =
            who === mBorderDrawable || who === mMaskDrawable || super.verifyDrawable(who)
}
