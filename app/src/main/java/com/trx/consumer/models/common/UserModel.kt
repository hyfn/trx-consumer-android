package com.trx.consumer.models.common

import org.json.JSONObject

class UserModel(
    val uid: String = "",
    var birthday: String = "",
    val card: CardModel? = null,
    val cardPaymentMethodId: String? = null,
    var email: String = "",
    var password: String = "",
    val memberships: HashMap<String, UserMembershipModel> = hashMapOf(),
    var zipCode: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var iap: String = "",
    val entitlements: EntitlementsModel = EntitlementsModel()
) {

    val fullName: String
        get() = "$firstName $lastName"

    val activeMemberships: Map<String, UserMembershipModel>
        get() = memberships.filter { it.value.isActive && it.key != "CORE" }

    companion object {

        fun parse(jsonObject: JSONObject): UserModel {
            val memberships = hashMapOf<String, UserMembershipModel>()
            jsonObject.optJSONObject("subscriptions")?.let { membershipsJson ->
                membershipsJson.keys().forEach { key ->
                    memberships[key] = UserMembershipModel.parse(membershipsJson.getJSONObject(key))
                }
            }
            return UserModel(
                uid = jsonObject.optString("uid"),
                birthday = jsonObject.optString("birthday"),
                email = jsonObject.optString("email"),
                zipCode = jsonObject.optString("postalCode"),
                firstName = jsonObject.optString("firstName"),
                lastName = jsonObject.optString("lastName"),
                iap = jsonObject.optString("iap"),
                memberships = memberships,
                entitlements = jsonObject.optJSONObject("permissions")?.let {
                    EntitlementsModel.parse(it)
                } ?: EntitlementsModel()
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
