package com.trx.consumer.screens.plans.list

import android.view.LayoutInflater
import android.view.ViewGroup
import com.trx.consumer.R
import com.trx.consumer.common.CommonRecyclerViewAdapter
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.managers.LogManager
import com.trx.consumer.models.common.PlansListModel
import com.trx.consumer.views.EmptyViewHolder
import kotlinx.coroutines.CoroutineScope

class PlansListAdapter(
    private val listener: PlansListListener,
    scopeProvider: () -> CoroutineScope
) : CommonRecyclerViewAdapter(scopeProvider) {

    companion object {
        private const val TYPE_EMPTY = -1
        private const val TYPE_PLANS_LIST = 1
    }

    private val items: MutableList<Any> = mutableListOf()

    override fun createCommonViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        return try {
            when (viewType) {
                TYPE_PLANS_LIST -> PlansListViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.row_plans, parent, false)
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
            is PlansListViewHolder -> {
                holder.setup(item as PlansListModel, listener)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is PlansListModel -> TYPE_PLANS_LIST
            else -> TYPE_EMPTY
        }
    }

    override fun getItemCount(): Int = items.size

    // TODO: Replace temporary method
    fun updatePlans(newPlans: List<PlansListModel>) {
        this.items.clear()
        this.items.addAll(newPlans)
        this.notifyDataSetChanged()
    }
}
