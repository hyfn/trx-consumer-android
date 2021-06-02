package com.trx.consumer.extensions

import androidx.annotation.ColorRes
import com.trx.consumer.R
import com.trx.consumer.common.CommonButton

fun CommonButton.setPrimaryEnabled(isEnabled: Boolean) {
    if (isEnabled != this.isEnabled) {
        bgColor(if (isEnabled) R.color.black else R.color.greyDark)
        setEnabled(isEnabled)
    }
}

fun CommonButton.applySkeleton(
    isSkeleton: Boolean,
    text: String,
    @ColorRes color: Int = R.color.transparent,
    @ColorRes skeletonColor: Int = R.color.greyLight
) {
    if (isSkeleton) {
        this.text = ""
        bgColor(skeletonColor)
    } else {
        this.text = text
        bgColor(color)
    }
}
