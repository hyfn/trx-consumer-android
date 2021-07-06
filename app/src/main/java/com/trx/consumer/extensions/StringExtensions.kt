package com.trx.consumer.extensions

import android.content.Context
import android.text.Layout
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.AlignmentSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.LineHeightSpan
import android.text.style.UnderlineSpan
import com.trx.consumer.common.CommonTypefaceSpan
import java.util.Locale
import kotlin.math.min
import kotlin.math.roundToInt

fun Context.spannableString(
    fullString: String,
    highlightedStrings: List<String> = emptyList(),
    fullColor: Int? = null,
    highlightedColor: Int? = null,
    fullFont: Int? = null,
    highlightedFont: Int? = null,
    fullUnderline: Boolean = false,
    highlightedUnderline: Boolean = false,
    fullSize: Int? = null,
    highlightedSize: Int? = null,
    textAlignment: Layout.Alignment? = null,
    lineSpacingMultiplier: Float? = null,
    clickableSpans: List<ClickableSpan> = emptyList()
): SpannableString {

    require(clickableSpans.isEmpty() || clickableSpans.size == highlightedStrings.size) { "Invalid number of clickableSpans" }

    val spannableString = SpannableString(fullString)
    val length = spannableString.length

    if (fullString.isEmpty()) {
        return spannableString
    }

    fullColor?.let {
        spannableString.setSpan(ForegroundColorSpan(it), 0, length, 0)
    }
    fullFont?.let {
        spannableString.setSpan(
            CommonTypefaceSpan(
                "",
                getTypefaceSafely(it)
            ),
            0,
            length,
            0
        )
    }
    fullSize?.let {
        spannableString.setSpan(AbsoluteSizeSpan(it, true), 0, length, 0)
    }
    if (fullUnderline) {
        spannableString.setSpan(UnderlineSpan(), 0, length, 0)
    }
    textAlignment?.let {
        spannableString.setSpan(AlignmentSpan.Standard(it), 0, length, 0)
    }

    lineSpacingMultiplier?.let {
        val lineHeightSpan = LineHeightSpan { _, _, _, _, _, fm ->
            fm?.let { fontMetrics ->
                val originalHeight = fontMetrics.descent - fontMetrics.ascent
                if (originalHeight > 0) {
                    val newDescent = fontMetrics.descent * lineSpacingMultiplier
                    val newAscent = fontMetrics.ascent * lineSpacingMultiplier
                    fontMetrics.descent = newDescent.roundToInt()
                    fontMetrics.ascent = newAscent.toInt()
                }
            }
        }
        spannableString.setSpan(lineHeightSpan, 0, length, 0)
    }

    var start = 0
    highlightedStrings.forEachIndexed { i, highlightedString ->
        val clickableSpan = clickableSpans.getOrNull(i)
        if (highlightedString.isNotBlank()) {
            fullString.substring(start).lowerCased()
                .indexOf(highlightedString.lowerCased()).takeIf { it != -1 }
                ?.let { index ->
                    start += index
                    val end = min(
                        start + highlightedString.length,
                        fullString.length
                    )
                    clickableSpan?.let {
                        spannableString.setSpan(it, start, end, 0)
                    }
                    highlightedColor?.let {
                        spannableString.setSpan(
                            ForegroundColorSpan(
                                it
                            ),
                            start, end, 0
                        )
                    }
                    highlightedFont?.let {
                        spannableString.setSpan(
                            CommonTypefaceSpan(
                                "",
                                getTypefaceSafely(it)
                            ),
                            start,
                            end,
                            0
                        )
                    }
                    highlightedSize?.let {
                        spannableString.setSpan(
                            AbsoluteSizeSpan(
                                it,
                                true
                            ),
                            start, end, 0
                        )
                    }
                    if (highlightedUnderline) {
                        spannableString.setSpan(UnderlineSpan(), start, end, 0)
                    }
                    start += highlightedString.length
                }
        }
    }

    return spannableString
}

fun String.capitalized(): String = capitalize(Locale.US)
fun String.upperCased(): String = toUpperCase(Locale.US)
fun String.lowerCased(): String = toLowerCase(Locale.US)
