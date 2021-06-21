package com.trx.consumer.models.common

enum class AnalyticsPropertyModel {
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

    val propertyName: String
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

    val propertyType: String
        get() {
            return when (this) {
                ATTENDED_LIVE_CLASS, DAYS_ELAPSED_IN_TRIAL, DAYS_ELAPSED_SINCE_SUBSCRIPTION_DATE,
                REGISTRATION_TYPE, SOCIAL_NETWORK, SUBSCRIPTION_ID, SUBSCRIPTION_PRICE, PLATFORM,
                OFFER -> "User"

                COLLECTION_ID, DURATION, ON_DEMAND_FILTER, PAGE, PAGE_TITLE, PROGRAM_ID, TRAINER_ID,
                TRAINER_NAME, VIDEO_ID, VIDEO_NAME -> "Event"
            }
        }
}
