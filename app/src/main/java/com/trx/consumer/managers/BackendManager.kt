package com.trx.consumer.managers

import com.trx.consumer.base.BaseApi
import com.trx.consumer.models.common.EndpointModel
import com.trx.consumer.models.core.RequestModel
import com.trx.consumer.models.core.ResponseModel
import com.trx.consumer.models.responses.UserResponseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BackendManager(private val api: BaseApi, private val cacheManager: CacheManager) {

    suspend fun call(request: RequestModel): ResponseModel {
        return withContext(Dispatchers.IO) {
            val endpoint = request.endpoint
            val url = request.path
            val params = request.params ?: hashMapOf()
            val accessToken = cacheManager.accessToken()
            val token = if (endpoint.isAuthenticated && !accessToken.isNullOrEmpty()) {
                "Bearer $accessToken"
            } else {
                null
            }
            var queryPath = request.path
            if (params.keys.isNotEmpty() || endpoint.isAuthenticated) queryPath += "?"
            accessToken?.let { aToken ->
                if (endpoint.isAuthenticated) {
                    queryPath += "access_token=$aToken"
                }
            }
            for (key in params.keys) {
                val value = params[key]
                queryPath += "&"
                queryPath += "$key=$value"
            }
            LogManager.log("Request: [${endpoint.type.name}] $queryPath")
            val responseModel = ResponseModel.parse(
                when (endpoint.type) {
                    EndpointModel.Type.POST -> api.post(url, token, params)
                    EndpointModel.Type.GET -> api.get(url, token, params)
                    EndpointModel.Type.PUT -> api.put(url, token, params)
                    EndpointModel.Type.DELETE -> api.delete(url, token, params)
                    EndpointModel.Type.PATCH -> api.patch(url, token, params)
                }
            )
            LogManager.log("Response: [${endpoint.type.name}] $queryPath \n${responseModel.responseString}")
            responseModel
        }
    }

    suspend fun login(email: String, password: String): ResponseModel {
        val params = hashMapOf<String, Any>(
            "email" to email,
            "password" to password
        )
        val path = EndpointModel.LOGIN.path
        val response = call(
            RequestModel(endpoint = EndpointModel.LOGIN, path = path, params = params)
        )
        if (!response.isSuccess) return response
        try {
            val model = UserResponseModel.parse(response.responseString)
            cacheManager.accessToken(model.jwt)
        } catch (e: Exception) {
            LogManager.log(e)
        }
        return user()
    }

    suspend fun paymentAdd(id: String): ResponseModel {
        val path = EndpointModel.PAYMENT_ADD.path + "/" + id
        return call(RequestModel(endpoint = EndpointModel.PAYMENT_ADD, path, params = null))
    }

    suspend fun paymentDelete(id: String): ResponseModel {
        val path = EndpointModel.PAYMENT_DELETE.path + "/" + id
        return call(RequestModel(endpoint = EndpointModel.PAYMENT_DELETE, path, params = null))
    }

    suspend fun register(params: HashMap<String, Any>): ResponseModel {
        val path = EndpointModel.REGISTER.path
        val response = call(
            RequestModel(endpoint = EndpointModel.REGISTER, path = path, params = params)
        )
        if (!response.isSuccess) return response
        try {
            val model = UserResponseModel.parse(response.responseString)
            cacheManager.accessToken(model.jwt)
        } catch (e: Exception) {
            LogManager.log(e)
        }
        return user()
    }

    suspend fun user(): ResponseModel {
        val path = EndpointModel.USER.path
        val response = call(RequestModel(endpoint = EndpointModel.USER, path = path, params = null))
        if (response.isSuccess) {
            val model = UserResponseModel.parse(response.responseString)
            cacheManager.user(model.user)
        }
        return response
    }

    suspend fun workouts(): ResponseModel {
        val path = EndpointModel.WORKOUTS.path
        return call(RequestModel(endpoint = EndpointModel.WORKOUTS, path = path, params = null))
    }

    suspend fun update(params: HashMap<String, Any>): ResponseModel {
        val path = EndpointModel.UPDATE.path
        return call(RequestModel(endpoint = EndpointModel.UPDATE, path = path, params = params))
    }
}
