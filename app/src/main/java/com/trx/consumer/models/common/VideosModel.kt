package com.trx.consumer.models.common

import android.os.Parcelable
import com.trx.consumer.extensions.forEach
import kotlinx.parcelize.Parcelize
import org.json.JSONObject
import java.util.Locale

@Parcelize
class VideosModel(
    var title: String = "",
    var description: String = "",
    var thumbnail: String = "",
    var poster: String = "",
    var trainer: TrainerModel = TrainerModel(),
    val videos: MutableList<VideoModel> = mutableListOf(),
    val isSkeleton: Boolean = false
) : Parcelable {

    val numberOfVideosDisplay: String
        get() {
            val size = videos.size
            return "$size workout${if (size == 1) "" else "s"}".toUpperCase(Locale.ROOT)
        }

    companion object {

        fun parse(jsonObject: JSONObject): VideosModel {
            return VideosModel().apply {
                title = jsonObject.optString("title")
                description = jsonObject.optString("description")
                thumbnail = jsonObject.optString("thumbnail")
                poster = jsonObject.optString("poster")
                trainer = jsonObject.optJSONObject("firstTrainer")?.let { TrainerModel.parse(it) }
                    ?: TrainerModel()
                jsonObject.optJSONArray("videos")?.forEach {
                    videos.add(VideoModel.parse(it))
                }
            }
        }

        fun skeletonList(size: Int): List<VideosModel> {
            return (0 until size).map { VideosModel(isSkeleton = true) }
        }
    }
}
