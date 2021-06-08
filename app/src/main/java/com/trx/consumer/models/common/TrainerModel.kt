package com.trx.consumer.models.common

import android.os.Parcelable
import com.trx.consumer.extensions.map
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
class TrainerModel(
    var bio: String = "",
    var isFeatured: Boolean = false,
    var isLive: Boolean = false,
    var key: String = "",
    var mantra: String = "",
    var profilePhoto: String = "",
    var trainerCertifications: List<String> = emptyList(),
    var trainerCoachingStyle: String = "",
    var virtualTrainerProfileId: String = "",
    var firstName: String = "",
    var lastName: String = ""
) : Parcelable {

    val contentModel: ContentModel
        get() = ContentModel().apply {
            title = "About $firstName"
            body = bio
        }

    val firstNameAndLastInitial: String
        get() = "$firstName ${lastName.firstOrNull() ?: ""}."

    val fullName: String
        get() = "$firstName $lastName"

    val displayName: String
        get() = "with $firstName ${lastName.firstOrNull() ?: ""}."

    val lstBadgeUrls: List<String>
        get() = mutableListOf<String>().apply {
            addAll(trainerCertifications)
            add(trainerCoachingStyle)
        }

    companion object {

        fun parse(jsonObject: JSONObject): TrainerModel {
            return TrainerModel(
                bio = jsonObject.optString("bio"),
                isFeatured = jsonObject.optBoolean("featuredTrainer"),
                isLive = jsonObject.optBoolean("liveTrainer"),
                key = jsonObject.optString("key"),
                mantra = jsonObject.optString("mantra"),
                profilePhoto = jsonObject.optString("profilePhoto"),
                trainerCertifications = jsonObject.optJSONArray("trainerCertifications").map(),
                trainerCoachingStyle = jsonObject.optString("trainerCoachingStyle"),
                virtualTrainerProfileId = jsonObject.optString("virtualTrainerProfileId"),
                firstName = jsonObject.optString("firstName"),
                lastName = jsonObject.optString("lastName")
            )
        }

        fun test(): TrainerModel {
            return TrainerModel(
                firstName = "Stephanie",
                lastName = "Warwick",
                key = "26d11c170e194efc9e31da65f006e7bc",
                bio = "This session will teach you how to push your limits and weight lift using the TRX Bands. Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                profilePhoto = "https://virtual-training-assets-production.s3.us-west-2.amazonaws.com/uploads/trainer_profiles/0872893d8f6d4074a9f31ecd9478fb9a/STEPHANIE_HS2.png"
            )
        }

        fun testList(count: Int): List<TrainerModel> {
            return (0 until count).map { test() }
        }
    }
}
