package com.trx.consumer.common

import android.content.Context
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import androidx.annotation.ColorRes
import androidx.annotation.FontRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

class CommonLabel @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    fun text(text: Any) {
        when (text) {
            is String -> this.text = text
            is SpannableString -> this.text = text
            is SpannableStringBuilder -> this.text = text
            else -> this.text = ""
        }
    }

    fun textColor(@ColorRes color: Int) {
        this.setTextColor(ContextCompat.getColor(this.context, color))
    }

    fun font(@FontRes font: Int) {
        typeface = ResourcesCompat.getFont(context, font)
    }
}
