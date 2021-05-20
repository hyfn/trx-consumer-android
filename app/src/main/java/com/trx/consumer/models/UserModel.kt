package com.trx.consumer.models

import com.trx.consumer.models.common.CardModel
import org.json.JSONObject

class UserModel(
    val uid: String = "",
    var birthday: String = "",
    val card: CardModel? = null,
    val cardPaymentMethod: String? = null,
    var email: String = "",
    var password: String = "",
    var zipCode: String = "",
    var firstName: String = "",
    var lastName: String = "",
) {

    val fullName: String
        get() = "$firstName $lastName"

    companion object {

        fun parse(jsonObject: JSONObject): UserModel {
            return UserModel(
                uid = jsonObject.optString("uid"),
                birthday = jsonObject.optString("birthday"),
                email = jsonObject.optString("email"),
                zipCode = jsonObject.optString("postalCode"),
                firstName = jsonObject.optString("firstName"),
                lastName = jsonObject.optString("lastName")
            )
        }

        fun test(): UserModel {
            return UserModel(
                email = "myo+xpdemoreview1@sprintfwd.com",
                firstName = "Myo",
                lastName = "Kyaw",
                zipCode = "90036",
            )
        }
    }
}
