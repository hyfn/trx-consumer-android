package com.trx.consumer.common.shapeable

import android.view.View
import androidx.annotation.ColorRes
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.shape.Shapeable
import com.trx.consumer.extensions.color

interface CommonShapeable<out T> : Shapeable where T : View, T : Shapeable {

    val shapeableHandler: CommonShapeableHandler<T>

    fun bgColor(@ColorRes colorRes: Int) {
        shapeableHandler.apply { bgColor(shapeableView.color(colorRes)) }
    }

    fun border(@ColorRes colorRes: Int, width: Int) {
        shapeableHandler.apply { border(shapeableView.color(colorRes), width.toFloat()) }
    }

    fun corners(radius: Int, cornerFamily: Int = CornerFamily.ROUNDED) {
        shapeableHandler.corners(radius.toFloat(), cornerFamily)
    }

    override fun getShapeAppearanceModel(): ShapeAppearanceModel {
        return shapeableHandler.shapeAppearanceModel
    }

    override fun setShapeAppearanceModel(shapeAppearanceModel: ShapeAppearanceModel) {
        shapeableHandler.shapeAppearanceModel = shapeAppearanceModel
    }
}
