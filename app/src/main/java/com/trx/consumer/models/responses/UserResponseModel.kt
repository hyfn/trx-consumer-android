package com.trx.consumer.models.responses

import com.trx.consumer.models.common.UserModel
import org.json.JSONObject

class UserResponseModel(val user: UserModel, val jwt: String = "") {

    companion object {

        fun parse(json: String): UserResponseModel {
            val jsonObject = JSONObject(json)
            val userObject = jsonObject.optJSONObject("user")
            val user = userObject?.let { UserModel.parse(it) } ?: UserModel()
            val jwt = jsonObject.optString("jwt")
            return UserResponseModel(user, jwt)
        }
    }
}
