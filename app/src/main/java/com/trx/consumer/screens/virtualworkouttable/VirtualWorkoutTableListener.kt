package com.trx.consumer.screens.virtualworkouttable

import com.trx.consumer.models.common.VirtualWorkoutModel

interface VirtualWorkoutTableListener {

    fun doTapPrimary(model: VirtualWorkoutModel)

    fun doTapSelect(model: VirtualWorkoutModel)
}
