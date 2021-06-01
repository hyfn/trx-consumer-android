package com.trx.consumer.models.common

import android.content.Context
import android.os.Parcelable
import com.trx.consumer.R
import com.trx.consumer.extensions.getPluralString
import com.trx.consumer.extensions.map
import kotlinx.parcelize.Parcelize
import org.json.JSONObject
import java.util.Locale

@Parcelize
class VideosModel(
    val title: String = "",
    val description: String = "",
    val thumbnail: String = "",
    val poster: String = "",
    val trainer: TrainerModel = TrainerModel(),
    val videos: List<VideoModel> = emptyList(),
    val isSkeleton: Boolean = false
) : Parcelable {

    val numberOfVideosDisplay: String
        get() {
            val size = videos.size
            return "$size workout${if (size == 1) "" else "s"}".toUpperCase(Locale.ROOT)
        }

    companion object {
        fun parse(jsonObject: JSONObject): VideosModel {
            return VideosModel(
                title = jsonObject.optString("title"),
                description = jsonObject.optString("description"),
                thumbnail = jsonObject.optString("thumbnail"),
                poster = jsonObject.optString("poster"),
                trainer = jsonObject.optJSONObject("firstTrainer")?.let {
                    TrainerModel.parse(it)
                } ?: TrainerModel(),
                videos = jsonObject.optJSONArray("videos").map { VideoModel.parse(it) }
            )
        }

        fun skeletonList(size: Int): List<VideosModel> {
            return (0 until size).map { VideosModel(isSkeleton = true) }
        }
    }
}
