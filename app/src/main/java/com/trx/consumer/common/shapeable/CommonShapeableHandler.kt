package com.trx.consumer.common.shapeable

import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.core.content.withStyledAttributes
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.shape.Shapeable
import com.trx.consumer.R
import com.trx.consumer.extensions.color

open class CommonShapeableHandler<out T>(
    private val shapeableViewProvider: () -> T
) where T : View, T : Shapeable {

    val shapeableView: T
        get() = shapeableViewProvider()

    var shapeAppearanceModel = ShapeAppearanceModel()
        set(value) {
            field = value
            refreshBackground()
        }

    @ColorInt
    protected var bgColor: Int = 0

    @ColorInt
    protected var borderColor: Int = 0
    protected var borderWidth: Float = 0F

    fun init(attrs: AttributeSet?, shapeableRes: CommonShapeableRes = CommonShapeableRes()) {
        shapeableView.clipToOutline = true
        shapeableView.context.apply {
            withStyledAttributes(attrs, shapeableRes.styleableRes) {
                val styleResID = getResourceId(R.styleable.CommonShapeable_shapeStyle, 0)
                if (styleResID == 0) {
                    initBackground(this, shapeableRes)
                } else {
                    withStyledAttributes(styleResID, shapeableRes.styleableRes) {
                        initBackground(this, shapeableRes)
                    }
                }
            }
        }
    }

    open fun initBackground(typedArray: TypedArray, shapeableRes: CommonShapeableRes) {
        typedArray.apply {
            val context = shapeableView.context
            val shapeAppearanceResId = getResourceId(
                R.styleable.CommonShapeable_shapeAppearance,
                0
            )
            shapeableView.shapeAppearanceModel = if (shapeAppearanceResId != 0) {
                ShapeAppearanceModel
                    .builder(context, shapeAppearanceResId, 0)
                    .build()
            } else {
                val cornerRadius = getDimension(shapeableRes.cornerRadiusRes, 0F)
                shapeableView.shapeAppearanceModel
                    .toBuilder()
                    .setAllCorners(CornerFamily.ROUNDED, cornerRadius)
                    .build()
            }
            bgColor = getColor(shapeableRes.bgColorRes, context.color(R.color.transparent))
            getColor(R.styleable.CommonShapeable_outline, 0).let { outlineColor ->
                if (outlineColor == 0) {
                    borderColor = getColor(
                        shapeableRes.borderColorRes,
                        context.color(R.color.transparent)
                    )
                    borderWidth = getDimension(shapeableRes.borderWidthRes, 0F)
                } else {
                    borderColor = outlineColor
                    borderWidth = context.resources.getDimension(R.dimen.outlineBorderWidth)
                }
            }
            refreshBackground()
        }
    }

    fun bgColor(@ColorInt color: Int) {
        bgColor = color
        refreshBackground()
    }

    open fun border(@ColorInt color: Int, width: Float) {
        borderColor = color
        borderWidth = width
        refreshBackground()
    }

    open fun corners(radius: Float, @CornerFamily cornerFamily: Int) {
        shapeableView.apply {
            shapeAppearanceModel = shapeAppearanceModel
                .toBuilder()
                .setAllCorners(cornerFamily, radius)
                .build()
        }
    }

    private fun refreshBackground() {
        val background = MaterialShapeDrawable(shapeableView.shapeAppearanceModel).apply {
            setStroke(borderWidth, borderColor)
            setTint(bgColor)
        }
        shapeableView.background = background
    }
}
