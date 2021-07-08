package com.trx.consumer.models.common

import android.os.Parcelable
import com.trx.consumer.extensions.map
import com.trx.consumer.screens.discover.DiscoverViewState
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
class VideoModel(
    var name: String = "",
    var duration: Int = 0,
    var description: String = "",
    var id: String = "0",
    var poster: String = "",
    var trainer: TrainerModel = TrainerModel(),
    var equipment: List<String> = listOf(),
    var level: String = "",
    var focus: String = "",
    var body: List<String> = listOf(),
    val isSkeleton: Boolean = false,
    var referenceId: String = "",
    var state : DiscoverViewState = DiscoverViewState.WORKOUTS
) : Parcelable {

    val videoDuration: String
        get() = "${duration / 60_000} MIN"

    companion object {

        fun parse(jsonObject: JSONObject): VideoModel {
            return VideoModel().apply {
                jsonObject.optJSONObject("video")?.let { videoJson ->
                    name = videoJson.optString("name")
                    duration = videoJson.optInt("duration")
                    id = videoJson.optString("id")
                    poster = videoJson.optString("poster")
                    description = videoJson.optString("description")
                }
                trainer = jsonObject.optJSONObject("trainer")?.let { TrainerModel.parse(it) }
                    ?: TrainerModel()
                equipment = jsonObject.optJSONArray("equipment").map()
                level = jsonObject.optString("level")
                focus = jsonObject.optString("focus")
                body = jsonObject.optJSONArray("body").map()
            }
        }

        fun test(): VideoModel {
            return VideoModel(
                name = "Full Body Power Pump",
                duration = 3600000,
                poster = "https://cf-images.us-east-1.prod.boltdns.net/v1/jit/6204326362001/9ad5d77c-99f7-4c65-8a2d-40ac2546fd01/main/1280x720/55s189ms/match/image.jpg",
                trainer = TrainerModel.test(),
                id = "6232799349001"
            )
        }

        fun skeletonList(size: Int): List<VideoModel> {
            return (0 until size).map { VideoModel(isSkeleton = true) }
        }

        fun testList(count: Int): List<VideoModel> {
            return (0 until count).map { test() }
        }
    }
}
