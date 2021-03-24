package com.trx.consumer.common

import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CommonActionListener(
    private val timeInterval: Long,
    private val scope: CoroutineScope,
    private val action: (view: View) -> Unit
) : View.OnClickListener {

    private var clickable = true

    override fun onClick(v: View) {
        if (clickable) {
            action(v)
            scope.launch {
                clickable = false
                delay(timeInterval)
                clickable = true
            }
        }
    }
}
