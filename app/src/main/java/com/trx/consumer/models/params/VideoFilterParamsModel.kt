package com.trx.consumer.models.params

import android.os.Parcelable
import com.trx.consumer.models.common.VideoFilterModel
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoFilterParamsModel(
    var list: List<VideoFilterModel> = listOf()
) : Parcelable
