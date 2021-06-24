package com.trx.consumer.common

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.content.ContextCompat
import androidx.core.content.res.TypedArrayUtils.getResourceId
import androidx.core.content.withStyledAttributes
import com.trx.consumer.R
import com.trx.consumer.common.shapeable.CommonShapeable
import com.trx.consumer.common.shapeable.CommonShapeableHandler

class CommonStateButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : AppCompatCheckBox(context, attrs), CommonShapeable<CommonStateButton> {

    private var checkedStateDrawable: Int = 0
    private var plainStateDrawable: Int = 0
    override val shapeableHandler = CommonShapeableHandler { this }

    init {
        shapeableHandler.init(attrs)
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.CommonStateButton, 0, 0)
        try {
            checkedStateDrawable = typeArray.getResourceId(R.styleable.CommonStateButton_checkedState, 0)
            plainStateDrawable = typeArray.getResourceId(R.styleable.CommonStateButton_plainState, 0)
        } finally {
            typeArray.recycle()
        }
        initTypeface(context)
    }

    private fun initTypeface(context: Context) {
        buttonDrawable = ContextCompat.getDrawable(
            context, if (isChecked) checkedStateDrawable else plainStateDrawable
        )
        /*background = ContextCompat.getDrawable(
            context, if (isChecked) checkedStateDrawable else plainStateDrawable
        )*/
    }

    fun onChecked(action: (isChecked: Boolean) -> Unit) {
        setOnCheckedChangeListener { _, isChecked ->
            initTypeface(context)
            action(isChecked)
        }
    }
}
