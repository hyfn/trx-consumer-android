package com.trx.consumer.views.calendar

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.core.content.res.use
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.trx.consumer.R
import com.trx.consumer.common.CommonView
import com.trx.consumer.databinding.LayoutCalendarBinding
import com.trx.consumer.extensions.dp
import com.trx.consumer.extensions.getInputType
import com.trx.consumer.extensions.lifecycleScope
import com.trx.consumer.extensions.monthString
import com.trx.consumer.extensions.yearString
import com.trx.consumer.models.common.CalendarModel
import com.trx.consumer.models.common.DaysModel
import com.trx.consumer.models.states.CalendarViewState
import com.trx.consumer.views.calendar.days.DaysAdapter
import com.trx.consumer.views.calendar.days.DaysTapListener
import com.trx.consumer.views.calendar.days.DaysUpdateListener
import java.util.Calendar
import java.util.Date

class CalendarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : CommonView(context, attrs), DaysUpdateListener {

    private val binding = LayoutCalendarBinding.inflate(LayoutInflater.from(context), this, true)

    private var state: CalendarViewState = CalendarViewState.DISPLAY
    private var tapListener: DaysTapListener? = null
    private var totalDays: Int = 0
    private lateinit var daysAdapter: DaysAdapter
    private var currentSelectedDate: Date = Date()
    private val calendarModel: CalendarModel = CalendarModel()

    init {
        context.obtainStyledAttributes(attrs, R.styleable.CalendarView).use {
            totalDays = it.getInt(R.styleable.CalendarView_totalDays, 7)
            state = it.getInputType(
                R.styleable.CalendarView_calendarViewState,
                CalendarViewState.DISPLAY
            )
        }

        rvDays.layoutManager = object : LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        ) {
            override fun checkLayoutParams(lp: RecyclerView.LayoutParams): Boolean {
                // Screen width - (Margin Start + Margin End) - (Width of Days) / (Spaces between days)
                lp.width = ((width - (2 * 15.dp) - (45.dp * 7)) / 6.0).toInt()
                return true
            }
        }

        update()
    }

    private fun update() {
        when (state) {
            CalendarViewState.DISPLAY -> {
                lblLeft.text = context.getString(R.string.calendarview_this_week)
                kotlin.run {
                    daysAdapter = DaysAdapter { lifecycleScope }
                }.also {
                    rvDays.adapter = daysAdapter
                }
            }
            CalendarViewState.PICKER -> {
                kotlin.run {
                    daysAdapter = DaysAdapter(this, tapListener) { lifecycleScope }
                }.also {
                    rvDays.adapter = daysAdapter
                }
                doUpdateDate(Date())
            }
        }
        setDays()
    }

    fun setTapListener(listener: DaysTapListener) {
        tapListener = listener
    }

    //  Only called once during initialization
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
        calendarModel.listEvents = model.listEvents
        rvDays.suppressLayout(false)
        rvDays.suppressLayout(calendarModel.state == CalendarViewState.DISPLAY)
    }

    override fun doUpdateDate(date: Date) {
        lblLeft.text = date.monthString()
        lblRight.text = date.yearString()
        if (currentSelectedDate != date) {
            daysAdapter.updateSelected(date)
        }
        currentSelectedDate = date
    }

    private val lblLeft
        get() = binding.lblLeft
    private val lblRight
        get() = binding.lblRight
    private val rvDays
        get() = binding.rvDays
}
