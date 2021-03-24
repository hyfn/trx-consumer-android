package com.trx.consumer.extensions

import android.content.res.TypedArray

inline fun <reified T : Enum<T>> TypedArray.getInputType(index: Int, default: T) =
    getInt(index, -1).let {
        if (it >= 0) enumValues<T>()[it] else default
    }
