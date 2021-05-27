package com.trx.consumer.screens.liveworkout

import com.trx.consumer.models.common.WorkoutModel

interface LiveWorkoutViewListener {

    fun doTapBookLiveWorkout(model: WorkoutModel)

    fun doTapSelectLiveWorkout(model: WorkoutModel)
}
