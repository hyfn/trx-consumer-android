package com.trx.consumer.models.common

import android.os.Parcelable
import com.trx.consumer.models.states.ScheduleViewState
import kotlinx.parcelize.Parcelize

@Parcelize
class ScheduleModel(
    val state: ScheduleViewState,
    val key: String? = null,
    val trainerProgram: TrainerProgramModel? = null
) : Parcelable
