package com.trx.consumer.screens.trainerprofile

import android.view.LayoutInflater
import android.view.ViewGroup
import com.trx.consumer.R
import com.trx.consumer.common.CommonRecyclerViewAdapter
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.managers.LogManager
import com.trx.consumer.models.common.TrainerModel
import com.trx.consumer.views.EmptyViewHolder
import kotlinx.coroutines.CoroutineScope
import java.lang.Exception

class TrainerProfileAdapter(
    private val listener: TrainerProfileListener,
    scopeProvider: () -> CoroutineScope
) : CommonRecyclerViewAdapter(scopeProvider) {

    companion object {
        private const val TYPE_EMPTY = -1
        private const val TYPE_ROW = 1
    }

    private val items: MutableList<Any> = mutableListOf()

    override fun createCommonViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        return try {
            when (viewType) {
                TYPE_ROW -> TrainerProfileViewHolder(
                    LayoutInflater
                        .from(parent.context)
                        .inflate(R.layout.row_trainer_profile, parent, false)
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
            is TrainerProfileViewHolder -> holder.setup(item as TrainerModel, listener)
            is EmptyViewHolder -> holder.setup(true)
        }
    }

    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return if (items[position] is TrainerModel) TYPE_ROW else TYPE_EMPTY
    }

    fun update(newItems: List<TrainerModel>) {
        items.apply {
            clear()
            addAll(newItems)
            notifyDataSetChanged()
        }
    }
}
