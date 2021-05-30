package com.trx.consumer.screens.subscriptions.list

import android.view.LayoutInflater
import android.view.ViewGroup
import com.trx.consumer.R
import com.trx.consumer.common.CommonRecyclerViewAdapter
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.managers.LogManager
import com.trx.consumer.models.common.SubscriptionModel
import com.trx.consumer.views.EmptyViewHolder
import kotlinx.coroutines.CoroutineScope

class SubscriptionsAdapter(
    private val listener: SubscriptionsListener,
    scopeProvider: () -> CoroutineScope
) : CommonRecyclerViewAdapter(scopeProvider) {

    companion object {
        private const val TYPE_EMPTY = -1
        private const val TYPE_SUBSCRIPTION_LIST = 1
    }

    private var state: SubscriptionsViewState = SubscriptionsViewState.OTHER
    private var items: MutableList<Any> = mutableListOf()

    override fun createCommonViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        return try {
            when (viewType) {
                TYPE_SUBSCRIPTION_LIST -> SubscriptionsViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.row_subscriptions, parent, false)
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
            is SubscriptionsViewHolder -> {
                holder.setup(item as SubscriptionModel, state, listener)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is SubscriptionModel -> TYPE_SUBSCRIPTION_LIST
            else -> TYPE_EMPTY
        }
    }

    override fun getItemCount(): Int = items.size

    fun update(items: List<SubscriptionModel>) {
        this.items.clear()
        this.items.addAll(items)
        this.notifyDataSetChanged()
    }
}
