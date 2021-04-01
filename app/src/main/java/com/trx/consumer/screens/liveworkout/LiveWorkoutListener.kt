package com.trx.consumer.screens.liveworkout

import com.trx.consumer.models.common.LiveWorkoutModel

interface LiveWorkoutListener {

    fun doTapBook(model: LiveWorkoutModel)

    fun doTapSelect(model: LiveWorkoutModel)
}
