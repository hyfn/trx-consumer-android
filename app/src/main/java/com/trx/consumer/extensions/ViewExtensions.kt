package com.trx.consumer.extensions

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.trx.consumer.common.CommonActionListener
import com.trx.consumer.common.CommonCheckBox
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

inline var View.isHidden: Boolean
    get() = visibility == View.GONE
    set(value) {
        visibility = if (value) View.GONE else View.VISIBLE
    }

inline val View.lifecycleScope: CoroutineScope
    get() = findViewTreeLifecycleOwner()?.lifecycleScope ?: MainScope()

fun View.margin(
    left: Float? = null,
    top: Float? = null,
    right: Float? = null,
    bottom: Float? = null
) {
    layoutParams<ViewGroup.MarginLayoutParams> {
        left?.let { leftMargin = dpToPx(it) }
        top?.let { topMargin = dpToPx(it) }
        right?.let { rightMargin = dpToPx(it) }
        bottom?.let { bottomMargin = dpToPx(it) }
    }
}

fun View.dimensions(width: Float? = null, height: Float? = null) {
    layoutParams<ViewGroup.LayoutParams> {
        width?.let { this.width = dpToPx(width) }
        height?.let { this.height = dpToPx(height) }
    }
}

fun View.containsOnScreen(x: Float, y: Float): Boolean {
    return Rect().also { this.getGlobalVisibleRect(it) }.contains(x.toInt(), y.toInt())
}

fun View.color(@ColorRes color: Int): Int = context.color(color)

inline fun View.action(timeIntervalMillis: Long = 500, crossinline action: (view: View) -> Unit) {
    val actionListener = CommonActionListener(timeIntervalMillis, lifecycleScope) { action(it) }
    setOnClickListener(actionListener)
}

inline fun <reified T : ViewGroup.LayoutParams> View.layoutParams(block: T.() -> Unit) {
    updateLayoutParams(block)
}
