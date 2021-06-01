package com.trx.consumer.models.common

class TrainerScheduleModel(
    var program: TrainerProgramModel = TrainerProgramModel(),
    var timestamp: Long = 0
) {
    companion object {

        fun test(): TrainerScheduleModel {
            return TrainerScheduleModel(
                program = TrainerProgramModel.test(),
                timestamp = 3600000
            )
        }

        fun testList(count: Int): List<TrainerScheduleModel> {
            return (0 until count).map { test() }
        }
    }
}
