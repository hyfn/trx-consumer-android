package com.trx.consumer.common

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope

abstract class CommonRecyclerViewAdapter(
    private val scopeProvider: () -> CoroutineScope
) : RecyclerView.Adapter<CommonViewHolder>() {

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        return createCommonViewHolder(parent, viewType).apply { setScopeProvider(scopeProvider) }
    }

    abstract fun createCommonViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder
}
