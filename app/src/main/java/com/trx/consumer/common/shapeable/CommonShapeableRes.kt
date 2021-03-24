package com.trx.consumer.common.shapeable

import androidx.annotation.StyleableRes
import com.trx.consumer.R

class CommonShapeableRes(
    @StyleableRes val styleableRes: IntArray = R.styleable.CommonShapeable,
    @StyleableRes val bgColorRes: Int = R.styleable.CommonShapeable_backgroundColor,
    @StyleableRes val borderWidthRes: Int = R.styleable.CommonShapeable_strokeWidth,
    @StyleableRes val borderColorRes: Int = R.styleable.CommonShapeable_strokeColor,
    @StyleableRes val cornerRadiusRes: Int = R.styleable.CommonShapeable_cornerRadius,
)
