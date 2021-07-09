package com.trx.consumer.common

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.res.getResourceIdOrThrow
import androidx.core.content.withStyledAttributes
import com.trx.consumer.R
import com.trx.consumer.common.shapeable.CommonShapeable
import com.trx.consumer.common.shapeable.CommonShapeableHandler
import com.trx.consumer.extensions.color
import com.trx.consumer.managers.LogManager

class CommonStateButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : AppCompatImageButton(context, attrs, defStyleAttr), CommonShapeable<CommonStateButton> {

    private var checkedStateDrawable: Int = 0
    private var plainStateDrawable: Int = 0
    var isChecked: Boolean = false
        set(value) {
            field = value
            setButtonDrawable()
        }

    override val shapeableHandler = CommonShapeableHandler { this }

    init {
        shapeableHandler.init(attrs)

        context.withStyledAttributes(attrs, R.styleable.CommonStateButton) {
            isChecked = getBoolean(R.styleable.CommonStateButton_checkedState, false)
            checkedStateDrawable = getResourceId(R.styleable.CommonStateButton_checkedState, 0)
            plainStateDrawable = getResourceId(R.styleable.CommonStateButton_plainState, 0)
            setButtonDrawable()

            val bgColor = getResourceId(R.styleable.CommonStateButton_bgColor, 0)
            if (bgColor != 0) shapeableHandler.apply { bgColor(shapeableView.color(bgColor)) }
        }
    }

    private fun setButtonDrawable() {
        setImageResource(if (isChecked) checkedStateDrawable else plainStateDrawable)
    }

    fun image(drawable: Int, state: Int) {
        when (state) {
            ACTIVE -> checkedStateDrawable = drawable
            PLAIN -> plainStateDrawable = drawable
        }
        setButtonDrawable()
    }

    fun image(drawable: Int) {
        checkedStateDrawable = drawable
        plainStateDrawable = drawable
    }

    companion object {
        const val ACTIVE = 1
        const val PLAIN = 0
    }
}
