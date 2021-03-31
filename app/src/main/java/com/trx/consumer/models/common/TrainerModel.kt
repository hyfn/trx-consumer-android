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
): Parcelable