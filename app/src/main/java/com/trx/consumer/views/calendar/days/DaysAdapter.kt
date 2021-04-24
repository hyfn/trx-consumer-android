package com.trx.consumer.views.calendar.days

import android.view.LayoutInflater
import android.view.ViewGroup
import com.trx.consumer.R
import com.trx.consumer.common.CommonRecyclerViewAdapter
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.extensions.yearMonthDayString
import com.trx.consumer.models.common.DaysModel
import kotlinx.coroutines.CoroutineScope
import java.util.Date

class DaysAdapter(
    private val updateListener: DaysUpdateListener? = null,
    scopeProvider: () -> CoroutineScope
) : CommonRecyclerViewAdapter(scopeProvider) {

    private var tapListener: DaysTapListener? = null

    private val items: MutableList<DaysModel> = mutableListOf()

    override fun createCommonViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder =
        DaysViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.row_calendar_days, parent, false)
        )

    override fun onBindViewHolder(holder: CommonViewHolder, position: Int) {
        val item = items[position]
        (holder as DaysViewHolder).setup(item, updateListener, tapListener)
    }

    override fun getItemCount(): Int = items.size

    // Temporary update method for now.
    fun update(newItems: List<DaysModel>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    // Temporary update method for now. Will be replaced with DiffUtils. Not efficient.
    fun updateSelected(current: Date) {
        this.items.indexOfFirst { it.state.isSelected }.let {
            if (it != -1) {
                items.get(it).state.isSelected = false
            }
        }

        this.items.indexOfFirst {
            it.date.yearMonthDayString() == current.yearMonthDayString()
        }.let {
            if (it != -1) {
                items[it].state.isSelected = true
            }
        }

        notifyDataSetChanged()
    }

    fun updateTapListener(listener: DaysTapListener) {
        tapListener = listener
        notifyDataSetChanged()
    }
}
