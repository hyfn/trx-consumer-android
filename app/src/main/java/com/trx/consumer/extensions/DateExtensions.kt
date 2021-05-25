package com.trx.consumer.extensions

import com.trx.consumer.BuildConfig
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

fun Date.dateAtHour(hour: Int): Date {
    val calendar: Calendar = Calendar.getInstance()
    calendar.time = this
    return calendar.apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time
}

fun Date.date(hour: Int, minute: Int, second: Int): Date {
    val calendar: Calendar = Calendar.getInstance()
    calendar.time = this
    return calendar.apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, second)
    }.time
}

fun Date.minutesAgo(): Int {
    val calendar: Calendar = Calendar.getInstance()
    calendar.time = this
    return calendar.get(Calendar.MINUTE)
}

fun Date.elapsedMin(): Long {
    val dateTime = this.time
    val currentTime = Calendar.getInstance().time.time
    return TimeUnit.MILLISECONDS.toMinutes(currentTime - dateTime)
}

fun Date.minBeforeDate() : Long {
    val dateTime = this.time
    val currentTime = Calendar.getInstance().time.time
    return TimeUnit.MILLISECONDS.toMinutes(currentTime - dateTime)
}

fun Date.format(
    format: String? = BuildConfig.kClassDateTimeFormat,
    locale: Locale = Locale.US,
    zone: TimeZone = TimeZone.getTimeZone("UTC")
): String {
    return SimpleDateFormat(format, locale).apply { this.timeZone = zone }.format(this)
}

fun String.date(
    format: String? = BuildConfig.kClassDateTimeFormat,
    locale: Locale = Locale.US,
    zone: TimeZone = TimeZone.getTimeZone("UTC")
): Date? {
    return SimpleDateFormat(format, locale).apply { this.timeZone = zone }.parse(this)
}

fun Date.weekdayInitialString(
    locale: Locale = Locale.US
): String =
    SimpleDateFormat("E", locale).format(this).first().toString()

fun Date.monthDayString(
    locale: Locale = Locale.US
): String =
    SimpleDateFormat("d", locale).format(this).toString()

fun Date.monthString(
    locale: Locale = Locale.US
): String =
    SimpleDateFormat("MMMM", locale).format(this).toString()

fun Date.yearString(
    locale: Locale = Locale.US
): String =
    SimpleDateFormat("YYY", locale).format(this).toString()

fun Date.yearMonthDayString(
    locale: Locale = Locale.US
): String =
    SimpleDateFormat("yyyMMdd", locale).format(this).toString()
