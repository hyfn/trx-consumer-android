package com.trx.consumer.models.responses

import com.trx.consumer.extensions.forEach
import com.trx.consumer.models.common.FilterModel
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.common.VideosModel
import org.json.JSONObject

class VideosResponseModel(
    val workouts: List<VideoModel> = listOf(),
    val collections: List<VideosModel> = listOf(),
    val programs: List<VideosModel> = listOf(),
    val filters: List<FilterModel> = listOf(),
    val results: List<VideoModel> = listOf() // Used when filtering
) {

    companion object {
        fun parse(json: String): VideosResponseModel {
            val jsonObject = JSONObject(json)
            val data = jsonObject.optJSONObject("data")

            val workouts = mutableListOf<VideoModel>()
            data?.optJSONObject("workouts")?.optJSONArray("videos")?.forEach {
                workouts.add(VideoModel.parse(it))
            }

            val collections = mutableListOf<VideosModel>()
            data?.optJSONArray("collections")?.forEach {
                collections.add(VideosModel.parse(it))
            }

            val programs = mutableListOf<VideosModel>()
            data?.optJSONArray("programs")?.forEach {
                programs.add(VideosModel.parse(it))
            }

            val filters = data?.optJSONObject("filters")?.let {
                FilterModel.filters(it)
            } ?: emptyList()

            val results = mutableListOf<VideoModel>()
            jsonObject.optJSONArray("data")?.forEach { results.add(VideoModel.parse(it)) }

            return VideosResponseModel(workouts, collections, programs, filters, results)
        }
    }
}
