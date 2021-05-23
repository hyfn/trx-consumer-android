package com.trx.consumer.models.responses

import com.trx.consumer.models.common.FilterModel
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.common.VideosModel
import org.json.JSONArray
import org.json.JSONObject

data class VideoResponseModel(
    val workouts: MutableList<VideoModel> = mutableListOf(),
    val collections: MutableList<VideosModel> = mutableListOf(),
    val programs: MutableList<VideosModel> = mutableListOf(),
    var filters: List<FilterModel> = listOf()
) {

    companion object {
        fun parse(json: String): VideoResponseModel {
            return VideoResponseModel().apply {
                val jsonObject = JSONObject(json)
                val data = jsonObject.optJSONObject("data")

                data?.optJSONObject("workouts")?.optJSONArray("videos")?.let { safeJson ->
                    for (index in 0 until safeJson.length()) {
                        safeJson.get(index)?.let {
                            if (it is JSONObject) {
                                workouts.add(VideoModel.parse(it))
                            }
                        }
                    }
                }

                data.optJSONArray("programs")?.let { safeJson ->
                    for (index in 0 until safeJson.length()) {
                        safeJson.get(index)?.let {
                            if (it is JSONObject) {
                                programs.add(VideosModel.parse(it))
                            }
                        }
                    }
                }

                data?.optJSONArray("collections")?.let { safeJson ->
                    for (index in 0 until safeJson.length()) {
                        safeJson.get(index)?.let {
                            if (it is JSONObject) {
                                collections.add(VideosModel.parse(it))
                            }
                        }
                    }
                }

            }
        }
    }
}
