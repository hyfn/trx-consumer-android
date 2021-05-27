package com.trx.consumer.views.calendar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.core.content.res.use
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.trx.consumer.R
import com.trx.consumer.common.CommonView
import com.trx.consumer.databinding.LayoutCalendarBinding
import com.trx.consumer.extensions.getInputType
import com.trx.consumer.extensions.lifecycleScope
import com.trx.consumer.extensions.monthString
import com.trx.consumer.extensions.yearMonthDayString
import com.trx.consumer.extensions.yearString
import com.trx.consumer.models.common.CalendarModel
import com.trx.consumer.models.common.DaysModel
import com.trx.consumer.models.states.CalendarViewState
import com.trx.consumer.views.calendar.days.DaysAdapter
import com.trx.consumer.views.calendar.days.DaysTapListener
import com.trx.consumer.views.calendar.days.DaysUpdateListener
import java.util.Calendar
import java.util.Date
import kotlin.math.roundToInt

class CalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : CommonView(context, attrs), DaysUpdateListener {

    private val binding = LayoutCalendarBinding
        .inflate(LayoutInflater.from(context), this, true)

    private var totalDays: Int = 0
    private lateinit var daysAdapter: DaysAdapter
    private val calendarModel: CalendarModel = CalendarModel()

    init {
        context.obtainStyledAttributes(attrs, R.styleable.CalendarView).use {
            totalDays = it.getInt(R.styleable.CalendarView_totalDays, 7)
            calendarModel.state = it.getInputType(
                R.styleable.CalendarView_calendarViewState,
                CalendarViewState.DISPLAY
            )
        }

        rvDays.layoutManager = object : LinearLayoutManager(context, HORIZONTAL, false) {
            override fun checkLayoutParams(lp: RecyclerView.LayoutParams): Boolean {
                //  (Screen width) / (Number of days to show)
                lp.width = (width / totalDays.toFloat()).roundToInt()
                return true
            }
        }

        update()
    }

    private fun update() {
        daysAdapter = DaysAdapter(this) { lifecycleScope }
        doUpdateDate(calendarModel.selectedDate)
        rvDays.adapter = daysAdapter
        setDays()
    }

    //  When calendarModel.state == CalendarViewState.PICKER, call this
    //  in BaseFragment's bind() function.
    fun setTapListener(listener: DaysTapListener) {
        daysAdapter.updateTapListener(listener)
    }

    fun setDays(days: Int? = null) {
        days?.let { totalDays = it }

        if (calendarModel.listDays.isEmpty()) {
            calendarModel.listDays = mutableListOf<DaysModel>().apply {
                val calendar = Calendar.getInstance()
                for (i in 0 until totalDays) {
                    add(DaysModel(calendar.time))
                    calendar.add(Calendar.DAY_OF_YEAR, 1)
                }
            }
        }
        calendarModel.listDays.firstOrNull()?.state?.isSelected = true

        daysAdapter.update(calendarModel.listDays)
        rvDays.suppressLayout(calendarModel.state == CalendarViewState.DISPLAY)
    }

    fun loadCalendarModel(model: CalendarModel) {
        calendarModel.apply {
            state = model.state
            listEvents = model.listEvents
        }
        rvDays.suppressLayout(false)
        rvDays.suppressLayout(calendarModel.state == CalendarViewState.DISPLAY)
    }

    override fun doUpdateDate(date: Date) {
        lblLeft.apply { if (isVisible) text = date.monthString() }
        lblRight.apply { if (isVisible) text = date.yearString() }
        if (calendarModel.selectedDate.yearMonthDayString() != date.yearMonthDayString()) {
            daysAdapter.updateSelected(date)
            calendarModel.selectedDate = date
        }
    }

    fun showLabels(show: Boolean) {
        lblLeft.isVisible = show
        lblRight.isVisible = show
    }

    private val lblLeft
        get() = binding.lblLeft
    private val lblRight
        get() = binding.lblRight
    private val rvDays
        get() = binding.rvDays
}
