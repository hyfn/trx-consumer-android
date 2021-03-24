package com.trx.consumer.common

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.shape.ShapeAppearanceModel
import com.trx.consumer.common.shapeable.CommonShapeable
import com.trx.consumer.common.shapeable.CommonShapeableHandler
import com.trx.consumer.extensions.border
import com.trx.consumer.extensions.color

class CommonImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ShapeableImageView(context, attrs, defStyleAttr), CommonShapeable<CommonImageView> {

    override val shapeableHandler = CommonShapeableHandler { this }

    override fun bgColor(colorRes: Int) {
        setBackgroundColor(color(colorRes))
    }

    override fun border(colorRes: Int, width: Int) {
        border(color(colorRes), width.toFloat())
    }

    override fun getShapeAppearanceModel(): ShapeAppearanceModel {
        return super<ShapeableImageView>.getShapeAppearanceModel()
    }

    override fun setShapeAppearanceModel(shapeAppearanceModel: ShapeAppearanceModel) {
        super<ShapeableImageView>.setShapeAppearanceModel(shapeAppearanceModel)
    }
}
