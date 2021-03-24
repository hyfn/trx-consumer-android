package com.trx.consumer.extensions

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import timber.log.Timber

fun Context.getTypefaceSafely(fontId: Int): Typeface {
    return try {
        ResourcesCompat.getFont(this, fontId)
            ?: Typeface.DEFAULT
    } catch (e: Exception) {
        Timber.w(e)
        Typeface.DEFAULT
    }
}
