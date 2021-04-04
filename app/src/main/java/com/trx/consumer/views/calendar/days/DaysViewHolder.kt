package com.trx.consumer.views.calendar.days

import android.view.View
import com.trx.consumer.R
import com.trx.consumer.common.CommonImageView
import com.trx.consumer.common.CommonLabel
import com.trx.consumer.common.CommonView
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.extensions.setTint
import com.trx.consumer.models.common.DaysModel

class DaysViewHolder(view: View) : CommonViewHolder(view) {

    private val imgDot: CommonImageView = view.findViewById(R.id.imgDot)
    private val lblTop: CommonLabel = view.findViewById(R.id.lblTop)
    private val lblBottom: CommonLabel = view.findViewById(R.id.lblBottom)
    private val viewDays: CommonView = view.findViewById(R.id.viewDays)

    fun setup(
        model: DaysModel,
        updateListener: DaysUpdateListener?,
        tapListener: DaysTapListener?
    ) {
        imgDot.setTint(model.state.dotColor)

        lblTop.apply {
            text(model.weekDayInitial)
            textColor(model.state.textColor)
        }

        lblBottom.apply {
            text(model.dayNumber)
            textColor(model.state.textColor)
            setBackgroundResource(model.state.bgDrawable)
        }

        viewDays.action {
            updateListener?.doUpdateDate(model.date)
            tapListener?.doTapDate(model.date)
        }
    }
}
