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

        fun parse(jsonObject: JSONObject): TrainerModel {
            return with(jsonObject) {
                TrainerModel(
                    bio = optString("bio"),
                    isFeatured = optBoolean("featuredTrainer"),
                    isLive = optBoolean("liveTrainer"),
                    key = optString("key"),
                    mantra = optString("mantra"),
                    profilePhoto = optString("profilePhoto"),
                    trainerCertifications = optJSONArray("trainerCertifications").map(),
                    trainerCoachingStyle = optString("trainerCoachingStyle"),
                    virtualTrainerProfileId = optString("virtualTrainerProfileId"),
                    firstName = optString("firstName"),
                    lastName = optString("lastName")
                )
            }
        }

        fun test(): TrainerModel {
            return TrainerModel(
                firstName = "Jaime",
                lastName = "Meneses",
                profilePhoto = "https://virtual-training-assets-production.s3.us-west-2.amazonaws.com/uploads/trainer_profiles/0872893d8f6d4074a9f31ecd9478fb9a/STEPHANIE_HS2.png"
            )
        }

        fun testList(count: Int): List<TrainerModel> {
            return (0 until count).map { test() }
        }
    }
}
