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

class MembershipAdapter(
    private val listener: MembershipListener,
    scopeProvider: () -> CoroutineScope
) : CommonRecyclerViewAdapter(scopeProvider) {

    companion object {
        private const val TYPE_EMPTY = -1
        private const val TYPE_MEMBERSHIP_ROW = 1
        private const val TYPE_MEMBERSHIP_HEADER = 2
    }

    private var items: MutableList<Any> = mutableListOf()

    override fun createCommonViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        return try {
            when (viewType) {
                TYPE_MEMBERSHIP_ROW -> MembershipsViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.row_membership, parent, false)
                )
                TYPE_MEMBERSHIP_HEADER -> MembershipsHeaderViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.row_membership_header, parent, false)
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
            is MembershipsViewHolder -> holder.setup(item as MembershipModel, listener)
            is MembershipsHeaderViewHolder -> holder.setup(item as String)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is MembershipModel -> TYPE_MEMBERSHIP_ROW
            is String -> TYPE_MEMBERSHIP_HEADER
            else -> TYPE_EMPTY
        }
    }

    override fun getItemCount(): Int = items.size

    fun update(items: List<Any>) {
        this.items.clear()
        this.items.addAll(items)
        this.notifyDataSetChanged()
    }
}
