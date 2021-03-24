package com.trx.consumer.common

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.Animation
import androidx.constraintlayout.widget.ConstraintLayout
import com.trx.consumer.common.shapeable.CommonShapeable
import com.trx.consumer.common.shapeable.CommonShapeableRes
import com.trx.consumer.common.shapeable.CommonShapeableViewGroupHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class CommonView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    commonShapeableRes: CommonShapeableRes = CommonShapeableRes()
) : ConstraintLayout(context, attrs), CommonShapeable<CommonView> {

    final override val shapeableHandler = CommonShapeableViewGroupHandler { this }
    private var touchEventListener: CommonTouchEventListener? = null

    init {
        shapeableHandler.init(attrs, commonShapeableRes)
    }

    fun setTouchEventListener(listener: CommonTouchEventListener) {
        touchEventListener = listener
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        touchEventListener?.onTouch(ev)
        return super.dispatchTouchEvent(ev)
    }

    fun startInAnimation(inAnimation: Animation) {
        startAnimation(inAnimation)
    }

    fun startOutAnimation(outAnimation: Animation, method: (() -> Unit)) {
        outAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                CoroutineScope(Dispatchers.Main).launch {
                    method()
                }
            }

            override fun onAnimationStart(animation: Animation?) {}
        })
        startAnimation(outAnimation)
    }
}
