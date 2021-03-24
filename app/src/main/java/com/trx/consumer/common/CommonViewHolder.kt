package com.trx.consumer.common

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope

abstract class CommonViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private lateinit var scopeProvider: () -> CoroutineScope

    protected val scope: CoroutineScope
        get() = scopeProvider()

    fun setScopeProvider(scopeProvider: () -> CoroutineScope) {
        this.scopeProvider = scopeProvider
    }

    protected inline fun View.action(
        timeIntervalMillis: Long = 500,
        crossinline action: (view: View) -> Unit
    ) {
        val actionListener = CommonActionListener(timeIntervalMillis, scope) { action(it) }
        setOnClickListener(actionListener)
    }
}
