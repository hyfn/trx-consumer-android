package com.trx.consumer.managers

import com.trx.consumer.base.BaseApi
import com.trx.consumer.models.common.EndpointModel
import com.trx.consumer.models.core.RequestModel
import com.trx.consumer.models.core.ResponseModel
import com.trx.consumer.models.responses.UserResponseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BackendManager(private val api: BaseApi, private val cacheManager: CacheManager) {

    suspend fun updateBeforeLogout() {
        cacheManager.accessToken(null)
        cacheManager.user(null)
    }

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
            LogManager.log(
                "Response: [${endpoint.type.name}] $queryPath \n${responseModel.responseString}"
            )
            responseModel
        }
    }

    suspend fun forgot(email: String): ResponseModel {
        val params = hashMapOf<String, Any>("email" to email)
        val path = EndpointModel.FORGOT.path
        return call(RequestModel(endpoint = EndpointModel.FORGOT, path = path, params = params))
    }

    suspend fun banner(): ResponseModel {
        val path = EndpointModel.BANNER.path
        return call(RequestModel(endpoint = EndpointModel.BANNER, path = path, params = null))
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

    suspend fun plans(): ResponseModel {
        val path = EndpointModel.PLANS.path
        return call(RequestModel(endpoint = EndpointModel.PLANS, path = path, params = null))
    }

    suspend fun paymentAdd(id: String): ResponseModel {
        val path = EndpointModel.PAYMENT_ADD.path + "/" + id
        return call(RequestModel(endpoint = EndpointModel.PAYMENT_ADD, path, params = null))
    }

    suspend fun paymentDelete(id: String): ResponseModel {
        val path = EndpointModel.PAYMENT_DELETE.path + "/" + id
        return call(RequestModel(endpoint = EndpointModel.PAYMENT_DELETE, path, params = null))
    }

    suspend fun promos(): ResponseModel {
        val path = EndpointModel.PROMOS.path
        return call(RequestModel(endpoint = EndpointModel.PROMOS, path = path, params = null))
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

    suspend fun planAdd(id: String, country: String = "US"): ResponseModel {
        val path = EndpointModel.PLAN_ADD.path
        val params = hashMapOf<String, Any>(
            "subscriptionType" to id,
            "country" to country
        )
        return call(
            RequestModel(
                endpoint = EndpointModel.PLAN_ADD,
                path = path,
                params = params
            )
        )
    }

    suspend fun planDelete(id: String): ResponseModel {
        val path = EndpointModel.PLAN_DELETE.path + "/$id"
        return call(
            RequestModel(
                endpoint = EndpointModel.PLAN_DELETE,
                path = path,
                params = null
            )
        )
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

    suspend fun logout(): ResponseModel {
        updateBeforeLogout()
        val path = EndpointModel.LOGOUT.path
        return call(RequestModel(endpoint = EndpointModel.LOGOUT, path = path, params = null))
    }

    suspend fun workouts(): ResponseModel {
        val path = EndpointModel.WORKOUTS.path
        return call(RequestModel(endpoint = EndpointModel.WORKOUTS, path = path, params = null))
    }

    suspend fun update(params: HashMap<String, Any>): ResponseModel {
        val path = EndpointModel.UPDATE.path
        return call(RequestModel(endpoint = EndpointModel.UPDATE, path = path, params = params))
    }

    suspend fun videos(params: HashMap<String, Any>? = null): ResponseModel {
        val path = EndpointModel.VIDEOS.path
        return call(RequestModel(endpoint = EndpointModel.VIDEOS, path = path, params = params))
    }

    suspend fun bookings(): ResponseModel {
        val path = EndpointModel.BOOKINGS.path
        return call(RequestModel(endpoint = EndpointModel.BOOKINGS, path = path, params = null))
    }
}
