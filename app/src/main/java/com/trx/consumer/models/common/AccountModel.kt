package com.trx.consumer.models.common

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
class AccountModel(
    var identifier: Int = 0,
    var email: String = "",
    var locationId: Int = 0,
    var locationName: String = "",
    var brandName: String = "",
    var brandSlug: String = "",
    var selected: Boolean = false
) : Parcelable {

    val title: String
        get() = "$brandName • $locationName"

    companion object {

        fun parse(jsonObject: JSONObject): AccountModel {
            return AccountModel().apply {
                identifier = jsonObject.optInt("id")
                email = jsonObject.optString("email")
                locationId = jsonObject.optInt("location_id")
                locationName = jsonObject.optString("location_name")
                brandName = jsonObject.optString("brand_name")
                brandSlug = jsonObject.optString("brand_slug")
            }
        }

        fun test(): AccountModel {
            return AccountModel().apply {
                brandSlug = "cyclebar"
                brandName = "Cycle Bar"
                locationName = "Santa Monica"
                email = "jane@sprintfwd.com"
            }
        }

        fun testList(count: Int, selected: Boolean = false): List<AccountModel> {
            return mutableListOf<AccountModel>().apply {
                for (i in 0 until count) {
                    val test = test().apply { this.selected = selected }
                    add(test)
                }
            }
        }
    }
}
