package com.trx.consumer.models.responses

import com.trx.consumer.extensions.forEach
import com.trx.consumer.extensions.iterator
import com.trx.consumer.models.common.FilterModel
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.common.VideosModel
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

                data?.optJSONObject("workouts")?.optJSONArray("videos")?.forEach {
                    workouts.add(VideoModel.parse(it))
                }

                data?.optJSONArray("programs")?.forEach {
                    programs.add(VideosModel.parse(it))
                }

                data?.optJSONArray("collections")?.forEach {
                    collections.add(VideosModel.parse(it))
                }

                filters = FilterModel.filters((data?.optJSONObject("filters") as JSONObject))
            }
        }
    }
}
