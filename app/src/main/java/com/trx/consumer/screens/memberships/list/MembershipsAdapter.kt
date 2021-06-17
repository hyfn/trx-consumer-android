package com.trx.consumer.screens.memberships.list

import android.view.LayoutInflater
import android.view.ViewGroup
import com.trx.consumer.R
import com.trx.consumer.common.CommonRecyclerViewAdapter
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.managers.LogManager
import com.trx.consumer.models.common.MembershipModel
import com.trx.consumer.views.EmptyViewHolder
import kotlinx.coroutines.CoroutineScope

class MembershipsAdapter(
    private val listener: MembershipListener,
    scopeProvider: () -> CoroutineScope
) : CommonRecyclerViewAdapter(scopeProvider) {

    companion object {
        private const val TYPE_EMPTY = -1
        private const val TYPE_MEMBERSHIP_LIST = 1
    }

    private var items: MutableList<Any> = mutableListOf()

    override fun createCommonViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        return try {
            when (viewType) {
                TYPE_MEMBERSHIP_LIST -> MembershipsViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.row_memberships, parent, false)
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
            is MembershipsViewHolder -> {
                holder.setup(item as MembershipModel, listener)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is MembershipModel -> TYPE_MEMBERSHIP_LIST
            else -> TYPE_EMPTY
        }
    }

    override fun getItemCount(): Int = items.size

    fun update(items: List<MembershipModel>) {
        this.items.clear()
        this.items.addAll(items)
        this.notifyDataSetChanged()
    }
}
