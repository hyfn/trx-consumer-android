package com.trx.consumer.screens.permissions

import android.view.LayoutInflater
import android.view.ViewGroup
import com.trx.consumer.R
import com.trx.consumer.common.CommonRecyclerViewAdapter
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.managers.LogManager
import com.trx.consumer.models.common.PermissionModel
import com.trx.consumer.views.EmptyViewHolder
import kotlinx.coroutines.CoroutineScope

class PermissionsAdapter(
    private val listener: PermissionsListener,
    scopeProvider: () -> CoroutineScope
) : CommonRecyclerViewAdapter(scopeProvider) {

    companion object {
        private const val TYPE_EMPTY = -1
        private const val TYPE_PERMISSION = 1
    }

    private val items: MutableList<Any> = mutableListOf()

    override fun createCommonViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        return try {
            when (viewType) {
                TYPE_PERMISSION -> PermissionViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.row_permission, parent, false)
                )
                else -> EmptyViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.row_empty, parent, false)
                )
            }
        } catch (e: Exception) {
            LogManager.log(e)
            return EmptyViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.row_empty, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is PermissionViewHolder -> {
                holder.setup(item as PermissionModel, listener)
            }
            is EmptyViewHolder -> {
                holder.setup(true)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is PermissionModel -> TYPE_PERMISSION
            else -> TYPE_EMPTY
        }
    }

    override fun getItemCount(): Int = items.size

    // Temporary update method for now.
    fun update(newItems: List<Any>) {
        this.items.apply {
            clear()
            addAll(newItems)
        }
        notifyDataSetChanged()
    }
}
