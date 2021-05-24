package com.trx.consumer.screens.liveworkouttable

import com.trx.consumer.models.common.WorkoutModel

interface LiveWorkoutTableListener {

    fun doTapBook(model: WorkoutModel)

    fun doTapSelect(model: WorkoutModel)
}
