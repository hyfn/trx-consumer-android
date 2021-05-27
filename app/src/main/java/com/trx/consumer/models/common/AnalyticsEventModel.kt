package com.trx.consumer.models.common

enum class AnalyticsEventModel {

    VIEW_VIDEO_DETAIL_ID;

    val amplitudeEventName: String
        get() {
            return when (this) {
                VIEW_VIDEO_DETAIL_ID -> "View Video Detail"
            }
        }

    val amplitudePropertyName: String
        get() {
            return when (this) {
                VIEW_VIDEO_DETAIL_ID -> "Video ID"
            }
        }

    val amplitudePropertyType: AmplitudePropertyModel
        get() {
            return when (this) {
                VIEW_VIDEO_DETAIL_ID -> AmplitudePropertyModel.EVENT
            }
        }
}
