package com.trx.consumer.models.core

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.trx.consumer.managers.LogManager
import retrofit2.Response
import java.net.HttpURLConnection

data class ResponseModel(
    var statusCode: Int = 0,
    var isSuccess: Boolean = false,
    var errorMessage: String = "",
    var responseString: String = ""
) {

    companion object {
        const val parseErrorMessage = "Couldn't parse response!"
        const val genericErrorMessage = "Something went wrong!"

        fun parse(response: Response<Any>): ResponseModel {
            val code = response.code()
            return if (response.isSuccessful) {
                val body = Gson().toJson(response.body())
                if (isSuccess(code, body)) {
                    LogManager.log("Success: $body")
                    ResponseModel(
                        statusCode = code,
                        responseString = body,
                        isSuccess = true
                    )
                } else {
                    parseError(code, body)
                }
            } else {
                val errorBody = response.errorBody()?.charStream()?.readText() ?: ""
                parseError(code, errorBody)
            }
        }

        private fun parseError(code: Int, errorBody: String): ResponseModel {
            return try {
                val json = JsonParser().parse(errorBody).asJsonObject
                val message = json.get("message").asString
                errorHandler(code, message)
            } catch (e: Exception) {
                LogManager.log(e)
                errorHandler(code, parseErrorMessage)
            }
        }

        private fun isSuccess(code: Int, responseString: String): Boolean {
            try {
                val json = JsonParser().parse(responseString).asJsonObject
                val success = json.get("success").asBoolean
                if (!success) return false
            } catch (e: Exception) {
                LogManager.log(e)
            }
            return code == HttpURLConnection.HTTP_OK || code == HttpURLConnection.HTTP_CREATED
        }

        private fun errorHandler(code: Int? = null, message: String? = null): ResponseModel {
            val genericErrorCode = 666
            val failureMessage = message ?: genericErrorMessage
            LogManager.log("Failure: $failureMessage")
            return ResponseModel(
                statusCode = code ?: genericErrorCode,
                isSuccess = false,
                errorMessage = failureMessage,
                responseString = ""
            )
        }
    }
}
