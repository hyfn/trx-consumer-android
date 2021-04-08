package com.trx.consumer.models.common

class VideoModel(
    var name: String = "",
    var duration: Int = 0,
    var id: Int = 0,
    var poster: String = "",
    var trainer: TrainerModel = TrainerModel(),
    var equipment: List<String> = listOf(),
    var level: String = "",
    var focus: String = "",
    var body: List<String> = listOf()
) {

    val videoDuration: String
        get() = "${duration / 60_000} MINS"

    companion object {

        fun test(): VideoModel {
            return VideoModel(
                name = "Full Body Power Pump",
                duration = 3600000,
                poster = "https://cf-images.us-east-1.prod.boltdns.net/v1/jit/6204326362001/9ad5d77c-99f7-4c65-8a2d-40ac2546fd01/main/1280x720/55s189ms/match/image.jpg",
                trainer = TrainerModel.test()
            )
        }

        fun testList(count: Int): List<VideoModel> {
            return (0 until count).map { test() }
        }
    }
}