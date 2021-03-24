package com.trx.consumer.extensions

fun Float.format(digits: Int) = "%.${digits}f".format(this)

fun Float.convertedMiles() = this * 0.000621371192F

fun Float.convertedMeters() = this * 1609.344F
