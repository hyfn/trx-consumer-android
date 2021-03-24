package com.trx.consumer.common

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView

class CommonRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerView(context, attrs, defStyle) {

    init {
        addOnItemTouchListener(object : SimpleOnItemTouchListener() {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                return if (isScrollable) false else rv.scrollState == SCROLL_STATE_DRAGGING
            }
        })
    }

    var isScrollable: Boolean = true

    var listener: ScrollListener? = null
        set(value) {
            offsetY = computeVerticalScrollOffset()
            field = value
        }

    var offsetY = 0
        private set

    interface ScrollListener {
        fun onScroll()
    }

    override fun onScrolled(dx: Int, dy: Int) {
        super.onScrolled(dx, dy)
        offsetY += dy
        listener?.onScroll()
    }
}
