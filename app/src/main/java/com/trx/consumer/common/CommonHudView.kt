package com.trx.consumer.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import com.trx.consumer.databinding.LayoutHudBinding

class CommonHudView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : CommonView(context, attrs) {

    private val viewBinding =
        LayoutHudBinding.inflate(LayoutInflater.from(context), this, true)

    inline var isVisible: Boolean
        get() = visible()
        set(value) {
            if (value) show() else hide()
        }

    fun show() {
        viewBinding.viewMain.visibility = View.VISIBLE
        viewBinding.animationView.playAnimation()
    }

    fun hide() {
        viewBinding.animationView.pauseAnimation()
        viewBinding.viewMain.visibility = View.GONE
    }

    fun visible(): Boolean {
        return viewBinding.viewMain.visibility == View.VISIBLE
    }
}
