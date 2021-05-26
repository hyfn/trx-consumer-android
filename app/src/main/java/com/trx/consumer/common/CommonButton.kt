package com.trx.consumer.common

import android.content.Context
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.trx.consumer.R
import com.trx.consumer.common.shapeable.CommonShapeable
import com.trx.consumer.common.shapeable.CommonShapeableHandler

class CommonButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatButton(context, attrs, defStyleAttr), CommonShapeable<CommonButton> {

    override val shapeableHandler = CommonShapeableHandler { this }

    init {
        shapeableHandler.init(attrs)
    }

    @Deprecated(
        message = "This method is deprecated",
        replaceWith = ReplaceWith(
            expression = "view.action() {}",
            imports = ["com.trx.consumer.extensions"]
        )
    )
    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
    }

    fun text(text: Any) {
        when (text) {
            is String -> this.text = text
            is SpannableString -> this.text = text
            is SpannableStringBuilder -> this.text = text
            else -> this.text = ""
        }
    }

    fun textColor(color: Int) {
        this.setTextColor(ContextCompat.getColor(this.context, color))
    }

    fun setPrimaryEnabled(isEnabled: Boolean) {
        if (isEnabled != isEnabled()) {
            bgColor(if (isEnabled) R.color.black else R.color.greyDark)
            setEnabled(isEnabled)
        }
    }
}
