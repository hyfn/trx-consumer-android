package com.trx.consumer.extensions

import java.text.DecimalFormat

fun Double.toPrice(): String {
    val pattern = "#,###.##"
    val decimalFormat = DecimalFormat(pattern)
    decimalFormat.groupingSize = 3

    return "$" + decimalFormat.format(this)
}
