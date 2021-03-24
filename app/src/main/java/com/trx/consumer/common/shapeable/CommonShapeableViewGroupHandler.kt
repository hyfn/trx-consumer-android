package com.trx.consumer.common.shapeable

import android.content.res.TypedArray
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.core.view.size
import com.google.android.material.shape.Shapeable
import com.trx.consumer.common.CommonImageView

class CommonShapeableViewGroupHandler<out T>(
    shapeableViewProvider: () -> T
) : CommonShapeableHandler<T>(shapeableViewProvider) where T : ViewGroup, T : Shapeable {

    private val borderView = CommonImageView(shapeableView.context)

    override fun initBackground(typedArray: TypedArray, shapeableRes: CommonShapeableRes) {
        super.initBackground(typedArray, shapeableRes)
        shapeableView.apply {
            doOnPreDraw {
                borderView.apply {
                    isClickable = false
                    isFocusable = false
                    layoutParams = ViewGroup.LayoutParams(shapeableView.width, shapeableView.height)
                    shapeAppearanceModel = shapeableView.shapeAppearanceModel
                    shapeableHandler.border(borderColor, borderWidth)
                }
                addView(borderView, size)
            }
        }
    }

    override fun border(color: Int, width: Float) {
        super.border(color, width)
        borderView.shapeableHandler.border(color, width)
    }

    override fun corners(radius: Float, cornerFamily: Int) {
        super.corners(radius, cornerFamily)
        borderView.shapeableHandler.corners(radius, cornerFamily)
    }
}
