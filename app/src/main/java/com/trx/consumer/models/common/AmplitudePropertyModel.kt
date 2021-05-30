package com.trx.consumer.models.common

enum class AmplitudePropertyModel {
    ATTENDED_LIVE_CLASS,
    COLLECTION_ID,
    DAYS_ELAPSED_IN_TRIAL,
    DAYS_ELAPSED_SINCE_SUBSCRIPTION_DATE,
    DURATION,
    OFFER,
    ON_DEMAND_FILTER,
    PAGE,
    PAGE_TITLE,
    PLATFORM,
    PROGRAM_ID,
    REGISTRATION_TYPE,
    SOCIAL_NETWORK,
    SUBSCRIPTION_ID,
    SUBSCRIPTION_PRICE,
    TRAINER_ID,
    TRAINER_NAME,
    VIDEO_ID,
    VIDEO_NAME;

    val amplitudePropertyName: Any
        get() {
            return when (this) {
                ATTENDED_LIVE_CLASS -> "Attended Live Class"
                COLLECTION_ID -> "Collection ID"
                DAYS_ELAPSED_IN_TRIAL -> "Days Elapsed In Trial"
                DAYS_ELAPSED_SINCE_SUBSCRIPTION_DATE -> "Days Elapsed Since Subscription Date"
                DURATION -> "Duration"
                OFFER -> "Offer"
                ON_DEMAND_FILTER -> "On Demand Filter"
                PAGE -> "Page"
                PAGE_TITLE -> "Page Title"
                PLATFORM -> "Platform"
                PROGRAM_ID -> "Program ID"
                REGISTRATION_TYPE -> "Registration Type"
                SOCIAL_NETWORK -> "Social Network"
                SUBSCRIPTION_ID -> "Subscription ID"
                SUBSCRIPTION_PRICE -> "Subscription Price"
                TRAINER_ID -> "Trainer ID"
                TRAINER_NAME -> "Trainer Name"
                VIDEO_ID -> "Video ID"
                VIDEO_NAME -> "Video Name"
            }
        }

    val amplitudePropertyType: String
        get() {
            return when (this) {
                ATTENDED_LIVE_CLASS -> "User"
                COLLECTION_ID -> "Event"
                DAYS_ELAPSED_IN_TRIAL -> "User"
                DAYS_ELAPSED_SINCE_SUBSCRIPTION_DATE -> "User"
                DURATION -> "Event"
                OFFER -> "User"
                ON_DEMAND_FILTER -> "Event"
                PAGE -> "Event"
                PAGE_TITLE -> "Event"
                PLATFORM -> "User"
                PROGRAM_ID -> "Event"
                REGISTRATION_TYPE -> "User"
                SOCIAL_NETWORK -> "User"
                SUBSCRIPTION_ID -> "User"
                SUBSCRIPTION_PRICE -> "User"
                TRAINER_ID -> "Event"
                TRAINER_NAME -> "Event"
                VIDEO_ID -> "Event"
                VIDEO_NAME -> "Event"
            }
        }
}
