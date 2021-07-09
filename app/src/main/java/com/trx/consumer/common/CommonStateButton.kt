package com.trx.consumer.common

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import com.trx.consumer.R
import com.trx.consumer.common.shapeable.CommonShapeable
import com.trx.consumer.common.shapeable.CommonShapeableHandler
import com.trx.consumer.extensions.action

class CommonStateButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), CommonShapeable<CommonStateButton> {

    private var checkedStateDrawable: Drawable? = null
    private var plainStateDrawable: Drawable? = null
    var isChecked: Boolean = false
    set(value) {
        field = value
        setButtonDrawable()}
    override val shapeableHandler = CommonShapeableHandler { this }

    init {
        shapeableHandler.init(attrs)

        context.withStyledAttributes(attrs, R.styleable.CommonStateButton) {
            checkedStateDrawable = getDrawable(R.styleable.CommonStateButton_checkedState)
            plainStateDrawable = getDrawable(R.styleable.CommonStateButton_plainState)
            isChecked = getBoolean(R.styleable.CommonStateButton_checkedState, false)
            setButtonDrawable()
        }
    }

    private fun setButtonDrawable() {
        background = if (isChecked) checkedStateDrawable else plainStateDrawable
    }

    fun image(drawable: Int, state: Int) {
        when (state) {
            ACTIVE -> checkedStateDrawable = ContextCompat.getDrawable(context, drawable)
            PLAIN -> plainStateDrawable = ContextCompat.getDrawable(context, drawable)
        }
        setButtonDrawable()
    }

    fun image(drawable: Int) {
        ContextCompat.getDrawable(context, drawable).let { icon ->
            checkedStateDrawable = icon
            plainStateDrawable = icon
        }
    }

    companion object {
        const val ACTIVE = 1
        const val PLAIN = 0
    }
}
