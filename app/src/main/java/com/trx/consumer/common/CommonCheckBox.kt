package com.trx.consumer.common

import android.content.Context
import android.util.AttributeSet
import android.widget.CheckBox
import androidx.appcompat.widget.AppCompatCheckBox

class CommonCheckBox @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CheckBox(context, attrs, defStyleAttr) {

    fun action(action: (isChecked: Boolean) -> Unit) {
        setOnCheckedChangeListener { _, isChecked -> action(isChecked) }
    }

}
