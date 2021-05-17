package com.trx.consumer.models.params

import android.os.Parcelable
import com.trx.consumer.models.common.WorkoutModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoParamsModel(
    val workoutModel: WorkoutModel
) : Parcelable
