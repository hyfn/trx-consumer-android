package com.trx.consumer.extensions

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.trx.consumer.managers.LogManager
import java.lang.Exception

fun Context.openBrowser(url: String) {
    try {
        startActivity(
            Intent(Intent.ACTION_VIEW)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .apply { data = Uri.parse(url) }
        )
    } catch (e: Exception) {
        LogManager.log(e)
    }
}

fun Context.underConstruction() {
    Toast.makeText(
        this, "Under construction!",
        Toast.LENGTH_LONG
    ).show()
}

fun Context.color(@ColorRes color: Int): Int = ContextCompat.getColor(this, color)

fun Context.hideKeyboard() =
    (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.takeIf { it.isActive }
        ?.toggleSoftInput(
            0,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
