package com.trx.consumer.models.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class VideosModel(
    val title: String = "",
    val poster: String = "",
    val trainer: TrainerModel = TrainerModel(),
    val videos: List<VideoModel> = emptyList()
): Parcelable