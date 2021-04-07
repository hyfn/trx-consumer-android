package com.trx.consumer.screens.virtualworkout

import com.trx.consumer.models.common.VirtualWorkoutModel

interface VirtualWorkoutListener {

    fun doTapPrimary(model: VirtualWorkoutModel)

    fun doTapSelect(model: VirtualWorkoutModel)
}
