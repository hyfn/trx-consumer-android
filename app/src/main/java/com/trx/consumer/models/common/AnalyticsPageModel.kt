package com.trx.consumer.models.common

enum class AnalyticsPageModel(val pageName: String) {
    ADD_CARD("Add Payment Method"),
    CARDS("Payment Methods"),
    DISCOVER("On-Demand Videos"),
    EMAIL_CODE("Enter Code"),
    EMAIL_FORGOT("Forgot Password"),
    GROUP_PLAYER("Small Group Session Player"),
    HOME("Home"),
    LIVE("Live Training"),
    LIVE_PLAYER("Large Group Session Player"),
    LOGIN("Log In"),
    MEMBERSHIPS("Memberships"),
    ONBOARDING("Onboarding"),
    PRIVATE_PLAYER("One-on-One Session Player"),
    PROFILE("Profile"),
    REGISTER("Sign Up"),
    RESTORE("Restore"),
    SCHEDULE_TRAINER_LIVE("Trainer Live Sessions Schedule"),
    SCHEDULE_TRAINER_VIRTUAL("Trainer Virtual Sessions Schedule"),
    SCHEDULE_USER_LIVE("User Live Sessions Schedule"),
    SCHEDULE_USER_VIRTUAL("User Virtual Sessions Schedule"),
    SETTINGS("Sessions"),
    TRAINER_DETAIL("Trainer"),
    VIDEO_PLAYER("On-Demand Video Player"),
    VIDEOS("On-Demand Playlist"),
    VIRTUAL("Virtual Training"),
    WORKOUT("Workout");

    val trackingFormat: String
        get() = "TRX Training Club | ${this.pageName}"
}
