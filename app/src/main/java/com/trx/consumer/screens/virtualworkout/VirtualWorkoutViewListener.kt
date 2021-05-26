package com.trx.consumer.screens.virtualworkout

import com.trx.consumer.models.common.WorkoutModel

interface VirtualWorkoutViewListener {

    fun doTapPrimaryVirtualWorkout(model: WorkoutModel)

    fun doTapSelectVirtualWorkout(model: WorkoutModel)
}
