package com.trx.consumer.screens.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import com.trx.consumer.R
import com.trx.consumer.common.CommonRecyclerViewAdapter
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.managers.LogManager
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.models.states.WorkoutViewState
import com.trx.consumer.screens.liveworkout.LiveWorkoutViewHolder
import com.trx.consumer.screens.liveworkout.LiveWorkoutViewListener
import com.trx.consumer.screens.virtualworkout.VirtualWorkoutViewHolder
import com.trx.consumer.screens.virtualworkout.VirtualWorkoutViewListener
import com.trx.consumer.views.EmptyViewHolder
import kotlinx.coroutines.CoroutineScope

class ProfileAdapter(
    private val liveWorkoutListener: LiveWorkoutViewListener,
    private val virtualWorkoutListener: VirtualWorkoutViewListener,
    scopeProvider: () -> CoroutineScope
) : CommonRecyclerViewAdapter(scopeProvider) {

    companion object {
        private const val TYPE_EMPTY_ROW = -1
        private const val TYPE_LIVE_ROW = 1
        private const val TYPE_VIRTUAL_ROW = 2
    }

    private val items: MutableList<Any> = mutableListOf()

    override fun createCommonViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder {
        return try {
            when (viewType) {
                TYPE_LIVE_ROW -> LiveWorkoutViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.row_live_workout_table, parent, false)
                )
                TYPE_VIRTUAL_ROW -> VirtualWorkoutViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.row_virtual_workout_table, parent, false)
                )

                else -> EmptyViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.row_empty, parent, false)
                )
            }
        } catch (e: Exception) {
            LogManager.log(e)
            return EmptyViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_empty, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is LiveWorkoutViewHolder -> {
                holder.setup(item as WorkoutModel, liveWorkoutListener)
            }
            is VirtualWorkoutViewHolder -> {
                holder.setup(item as WorkoutModel, virtualWorkoutListener)
            }
            is EmptyViewHolder -> {
                holder.setup(true)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (val item = items[position]) {
            is WorkoutModel -> {
                when (item.workoutState) {
                    WorkoutViewState.LIVE -> TYPE_LIVE_ROW
                    WorkoutViewState.VIRTUAL -> TYPE_VIRTUAL_ROW
                    else -> TYPE_EMPTY_ROW
                }
            }
            else -> TYPE_EMPTY_ROW
        }
    }

    override fun getItemCount(): Int = items.size

    fun update(newItems: List<Any>) {
        items.apply {
            clear()
            addAll(newItems)
        }
        notifyDataSetChanged()
    }
}
