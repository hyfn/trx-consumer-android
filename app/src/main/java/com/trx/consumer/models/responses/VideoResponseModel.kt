package com.trx.consumer.models.responses

import com.trx.consumer.models.common.FilterModel
import com.trx.consumer.models.common.VideoModel
import org.json.JSONArray
import org.json.JSONObject

data class VideoResponseModel(
    var workouts: List<VideoModel> = listOf(),
    var collections: List<VideoModel> = listOf(),
    var programs: List<VideoModel> = listOf(),
    var filters: List<FilterModel> = listOf()
) {

    companion object {
        fun parse(json: String): VideoResponseModel {
            return VideoResponseModel().apply {
                val jsonObject = JSONObject(json)
                val data = jsonObject.optJSONObject("data")

                workouts =
                    getVideos(
                        data?.optJSONObject("workouts")?.optJSONArray("videos")
                    )
                collections = getVideos(
                    (data?.optJSONArray("collections")?.get(0) as JSONObject)
                        .optJSONArray("videos")
                )
                programs = getVideos(
                    (data.optJSONArray("programs")?.get(0) as JSONObject)
                        .optJSONArray("videos")
                )
                filters = FilterModel.filters(
                    (data.optJSONObject("filters") as JSONObject)
                )
            }
        }

        private fun getVideos(jsonArray: JSONArray?): ArrayList<VideoModel> {
            return ArrayList<VideoModel>().apply {
                jsonArray?.let { safeJson ->
                    for (index in 0 until safeJson.length()) {
                        safeJson.get(index)?.let {
                            if (it is JSONObject) {
                                it.optJSONObject("video")?.let { videoJson ->
                                    add(VideoModel.parse(videoJson))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
