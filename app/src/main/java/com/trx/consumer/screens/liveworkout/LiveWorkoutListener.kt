package com.trx.consumer.screens.liveworkout

import com.trx.consumer.models.common.WorkoutModel

interface LiveWorkoutListener {

    fun doTapBook(model: WorkoutModel)

    fun doTapSelect(model: WorkoutModel)
}
