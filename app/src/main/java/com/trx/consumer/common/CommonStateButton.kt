package com.trx.consumer.common

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import com.trx.consumer.R
import com.trx.consumer.common.shapeable.CommonShapeable
import com.trx.consumer.common.shapeable.CommonShapeableHandler

@RequiresApi(Build.VERSION_CODES.M)
class CommonStateButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int = 0
) : AppCompatCheckBox(context, attrs), CommonShapeable<CommonStateButton> {

    private var checkedStateDrawable: Drawable? = null
    private var plainStateDrawable: Drawable? = null
    private var defaultStateDrawable: Drawable? = null
    override val shapeableHandler = CommonShapeableHandler { this }

    init {
        shapeableHandler.init(attrs)

        context.withStyledAttributes(attrs, R.styleable.CommonStateButton) {
            checkedStateDrawable = getDrawable(R.styleable.CommonStateButton_checkedState)
            plainStateDrawable = getDrawable(R.styleable.CommonStateButton_plainState)
            defaultStateDrawable = checkedStateDrawable ?: plainStateDrawable ?: buttonDrawable
            buttonDrawable = null
            setButtonDrawable()
        }
    }

    private fun setButtonDrawable() {
        buttonDrawable = if (checkedStateDrawable != null && plainStateDrawable != null) {
            if (isChecked) checkedStateDrawable else plainStateDrawable
        } else defaultStateDrawable
    }

    fun onChecked(action: (isChecked: Boolean) -> Unit) {
        setOnCheckedChangeListener { _, isChecked ->
            setButtonDrawable()
            action(isChecked)
        }
    }

    fun image(drawable: Int, state: Int?) {
        defaultStateDrawable = ContextCompat.getDrawable(context, drawable)
        when (state) {
            ACTIVE -> checkedStateDrawable = defaultStateDrawable
            PLAIN -> plainStateDrawable = defaultStateDrawable
        }

        buttonDrawable = null
        setButtonDrawable()
    }

    companion object {
        const val ACTIVE = 1
        const val PLAIN = 0
    }
}
