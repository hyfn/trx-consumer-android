package com.trx.consumer.models.common

import android.os.Parcelable
import com.trx.consumer.extensions.map
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
class TrainerModel(
    var bio: String = "",
    var isFeatured: Boolean = false,
    val isLive: Boolean = false,
    var key: String = "",
    var mantra: String = "",
    var profilePhoto: String = "",
    val trainerCertifications: List<String> = emptyList(),
    val trainerCoachingStyle: String = "",
    var virtualTrainerProfileId: String = "",
    var firstName: String = "",
    var lastName: String = ""
) : Parcelable {

    val firstNameAndLastInitial: String
        get() = "$firstName ${lastName.first()}."

    val fullName: String
        get() = "$firstName $lastName"

    val displayName: String
        get() = "with $firstName ${lastName.first()}."

    companion object {

        fun parse(jsonObject: JSONObject?): TrainerModel? {
            return jsonObject?.let { jsonObj ->
                TrainerModel(
                    bio = jsonObj.optString("bio"),
                    isFeatured = jsonObj.optBoolean("featuredTrainer"),
                    isLive = jsonObj.optBoolean("liveTrainer"),
                    key = jsonObj.optString("key"),
                    mantra = jsonObj.optString("mantra"),
                    profilePhoto = jsonObj.optString("profilePhoto"),
                    trainerCertifications = jsonObj.optJSONArray("trainerCertifications").map(),
                    trainerCoachingStyle = jsonObj.optString("trainerCoachingStyle"),
                    virtualTrainerProfileId = jsonObj.optString("virtualTrainerProfileId"),
                    firstName = jsonObj.optString("firstName"),
                    lastName = jsonObj.optString("lastName")
                )
            }
        }

        fun test(): TrainerModel {
            return TrainerModel(
                firstName = "Jaime",
                lastName = "Meneses",
                bio = "This session will teach you how to push your limits and weight lift using the TRX Bands. Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                profilePhoto = "https://virtual-training-assets-production.s3.us-west-2.amazonaws.com/uploads/trainer_profiles/0872893d8f6d4074a9f31ecd9478fb9a/STEPHANIE_HS2.png"
            )
        }

        fun testList(count: Int): List<TrainerModel> {
            return (0 until count).map { test() }
        }
    }
}
