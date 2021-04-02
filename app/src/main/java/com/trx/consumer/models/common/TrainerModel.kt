package com.trx.consumer.models.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class TrainerModel(
    var bio: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var gender: String = "",
    var liveTrainer: Boolean = false,
    var mantra: String = "",
    var profilePhoto: String = "",
    var virtualTrainerProfileId: String = "",
    var key: String = ""
) : Parcelable {

    val firstNameAndLastInitial: String
        get() = "$firstName ${lastName.first()}."

    companion object {

        fun test(): TrainerModel {
            return TrainerModel(
                firstName = "Jaime",
                lastName = "Neneses",
                profilePhoto = "https://virtual-training-assets-production.s3.us-west-2.amazonaws.com/uploads/trainer_profiles/0872893d8f6d4074a9f31ecd9478fb9a/STEPHANIE_HS2.png"
            )
        }

        fun testList(count: Int): List<TrainerModel> {
            return (0 until count).map { test() }
        }
    }
}
