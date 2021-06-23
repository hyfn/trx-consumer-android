package com.trx.consumer.common

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatCheckBox
import com.trx.consumer.common.shapeable.CommonShapeable
import com.trx.consumer.common.shapeable.CommonShapeableHandler

class CommonStateButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : AppCompatCheckBox(context, attrs), CommonShapeable<CommonStateButton> {

    override val shapeableHandler = CommonShapeableHandler { this }

    init {
        shapeableHandler.init(attrs)
    }

    fun onChecked(action: (isChecked: Boolean) -> Unit) {
        setOnCheckedChangeListener { _, isChecked -> action(isChecked) }
    }

}
