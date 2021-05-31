package com.trx.consumer.screens.trainerschedule

import com.trx.consumer.models.common.TrainerScheduleModel

interface TrainerScheduleListener {
    fun doTapClass(trainerScheduleModel: TrainerScheduleModel)
}
