package com.trx.consumer.screens.datepicker

import android.widget.DatePicker
import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import java.util.Calendar
import java.util.Date

class DatePickerViewModel : BaseViewModel(), DatePicker.OnDateChangedListener {

    //region Objects
    lateinit var model: DatePickerModel

    //endregion

    //region Events
    val eventLoadView = CommonLiveEvent<DatePickerModel>()
    val eventTapBack = CommonLiveEvent<Void>()

    //endregion

    //region Functions
    fun doLoadView(model: DatePickerModel) {
        this.model = model
        eventLoadView.postValue(model)
    }

    fun doOnDateSelected(date: Date) {
        model.onDateSelected?.invoke(date)
    }

    fun doTapDone() {
        eventTapBack.call()
    }

    fun doOnViewDismissed() {
        model.onDismissed?.invoke()
    }

    override fun onDateChanged(view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance().apply { set(year, monthOfYear, dayOfMonth) }
        doOnDateSelected(calendar.time)
    }

    //endregion
}
