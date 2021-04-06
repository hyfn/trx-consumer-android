package com.trx.consumer.views.calendar.days

import android.view.View
import com.trx.consumer.R
import com.trx.consumer.common.CommonLabel
import com.trx.consumer.common.CommonView
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.models.common.DaysModel

class DaysViewHolder(view: View) : CommonViewHolder(view) {

    private val lblTop: CommonLabel = view.findViewById(R.id.lblTop)
    private val lblBottom: CommonLabel = view.findViewById(R.id.lblBottom)
    private val viewDays: CommonView = view.findViewById(R.id.viewDays)

    fun setup(
        model: DaysModel,
        updateListener: DaysUpdateListener?,
        tapListener: DaysTapListener?
    ) {

        lblTop.apply {
            text(model.weekDayInitial)
        }

        lblBottom.apply {
            text(model.dayNumber)
            setBackgroundResource(model.state.bgDrawable)
        }

        viewDays.action {
            updateListener?.doUpdateDate(model.date)
            tapListener?.doTapDate(model.date)
        }
    }
}
