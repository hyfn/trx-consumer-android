package com.trx.consumer.models.common

import android.os.Parcelable
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
    val videos: MutableList<VideoModel> = mutableListOf()
) : Parcelable {

    companion object {
        fun parse(jsonObject: JSONObject): VideosModel {
            return VideosModel().apply {
                title = jsonObject.optString("title")
                description = jsonObject.optString("description")
                thumbnail = jsonObject.optString("thumbnail")
                poster = jsonObject.optString("poster")
                trainer = jsonObject.optJSONObject("firstTrainer")?.let {
                    TrainerModel.parse(it)
                } ?: TrainerModel()
                jsonObject.optJSONArray("videos")?.let { safeJson ->
                    for (index in 0 until safeJson.length()) {
                        safeJson.get(index)?.let {
                            if (it is JSONObject) {
                                videos.add(VideoModel.parse(it))
                            }
                        }
                    }
                }
            }
        }
    }

    val numberOfVideosDisplay: String
        get() {
            return "${videos.size} workout${if (videos.size == 1) "" else "s"}".toUpperCase(Locale.ROOT)
        }
}
