package com.trx.consumer.extensions

import android.content.res.ColorStateList
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.trx.consumer.R
import com.trx.consumer.common.CommonImageView

fun CommonImageView.load(
    url: String,
    usePlaceholder: Boolean = true
) {
    Glide.with(context)
        .load(url)
        .fitCenter()
        .let { requestBuilder ->
            if (usePlaceholder) {
                requestBuilder.apply(
                    RequestOptions().apply {
                        placeholder(R.drawable.ic_img_placeholder)
                        fallback(R.drawable.ic_img_placeholder)
                    }
                )
            } else {
                requestBuilder.into(this)
            }
        }
}

fun CommonImageView.setTint(@ColorRes colorId: Int) {
    val color = ContextCompat.getColor(context, colorId)
    val colorStateList = ColorStateList.valueOf(color)
    ImageViewCompat.setImageTintList(this, colorStateList)
}

fun CommonImageView.border(@ColorInt color: Int, width: Float) {
    strokeColor = ColorStateList.valueOf(color)
    strokeWidth = width * 2 // ShapeableImageView scales down stroke width by 1/2 before drawing
}

fun CommonImageView.applySkeleton(
    isSkeleton: Boolean = false,
    @ColorRes color: Int = R.color.greyLight,
    @DrawableRes image: Int? = null,
    urlString: String? = null
) {
    when {
        isSkeleton -> {
            setImageResource(android.R.color.transparent)
            bgColor(color)
        }
        image != null -> setImageResource(image)
        urlString != null -> load(urlString, false)
    }
}
