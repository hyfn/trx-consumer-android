package com.trx.consumer.common

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatCheckBox

class CommonCheckBox @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatCheckBox(context, attrs, defStyleAttr) {

    fun action(action: (isChecked: Boolean) -> Unit) {
        setOnCheckedChangeListener { _, isChecked -> action(isChecked) }
    }

}