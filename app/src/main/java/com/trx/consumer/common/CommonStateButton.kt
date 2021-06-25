package com.trx.consumer.common

import android.content.Context
import android.graphics.drawable.Drawable
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

    private var checkedStateDrawable: Drawable? = null
    private var plainStateDrawable: Drawable? = null
    override val shapeableHandler = CommonShapeableHandler { this }

    init {
        shapeableHandler.init(attrs)
        val typeArray = context.obtainStyledAttributes(attrs, R.styleable.CommonStateButton, 0, 0)
        try {
            checkedStateDrawable =
                typeArray.getDrawable(R.styleable.CommonStateButton_checkedState)
            plainStateDrawable =
                typeArray.getDrawable(R.styleable.CommonStateButton_plainState)
        } finally {
            typeArray.recycle()
        }
        buttonDrawable = null
        initTypeface()
    }

    private fun initTypeface() {
        buttonDrawable = if (isChecked) checkedStateDrawable else plainStateDrawable
    }

    fun onChecked(action: (isChecked: Boolean) -> Unit) {
        setOnCheckedChangeListener { _, isChecked ->
            initTypeface()
            action(isChecked)
        }
    }
}
