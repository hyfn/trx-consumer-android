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
        IAPManager.shared.reset()
    }

    private suspend fun getHeaders(isAuthenticated: Boolean): HashMap<String, String> {
        return withContext(Dispatchers.IO) {
            val headers = hashMapOf<String, String>()
            val accessToken = cacheManager.accessToken()

            headers["X-Platform"] = "android"
            if (isAuthenticated && !accessToken.isNullOrEmpty()) {
                headers["Authorization"] = "Bearer $accessToken"
            }
            headers
        }
    }

    suspend fun call(request: RequestModel): ResponseModel {
        return withContext(Dispatchers.IO) {
            val endpoint = request.endpoint
            val url = request.path
            val params = request.params ?: hashMapOf()
            val accessToken = cacheManager.accessToken()
            val isAuthenticated = endpoint.isAuthenticated

            val headers = getHeaders(isAuthenticated)
            var queryPath = request.path
            if (params.keys.isNotEmpty() || isAuthenticated) queryPath += "?"
            accessToken?.let { aToken ->
                if (isAuthenticated) {
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
                    EndpointModel.Type.POST -> api.post(url, headers, params)
                    EndpointModel.Type.GET -> api.get(url, headers, params)
                    EndpointModel.Type.PUT -> api.put(url, headers, params)
                    EndpointModel.Type.DELETE -> api.delete(url, headers, params)
                    EndpointModel.Type.PATCH -> api.patch(url, headers, params)
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

    //  Joining a Live Session, used in LivePlayerViewModel
    suspend fun join(sessionKey: String): ResponseModel {
        val path = EndpointModel.JOIN.path + "/customer/" + sessionKey
        return call(RequestModel(endpoint = EndpointModel.JOIN, path = path, params = null))
    }

    //  Joining a Live Session, used in LivePlayerViewModel
    suspend fun live(liveAccessKey: String): ResponseModel {
        val path = EndpointModel.LIVE.path + "/" + liveAccessKey
        return call(RequestModel(endpoint = EndpointModel.LIVE, path = path, params = null))
    }

    suspend fun banner(): ResponseModel {
        val path = EndpointModel.BANNER.path
        return call(RequestModel(endpoint = EndpointModel.BANNER, path = path, params = null))
    }

    suspend fun bookCancel(bookingKey: String): ResponseModel {
        val path = EndpointModel.BOOK_CANCEL.path + "/$bookingKey"
        return call(RequestModel(endpoint = EndpointModel.BOOK_CANCEL, path = path, params = null))
    }

    suspend fun bookings(): ResponseModel {
        val path = EndpointModel.BOOKINGS.path
        return call(RequestModel(endpoint = EndpointModel.BOOKINGS, path = path, params = null))
    }

    suspend fun bookProgramConfirm(params: HashMap<String, Any>): ResponseModel {
        val path = EndpointModel.BOOK_PROGRAM_CONFIRM.path
        return call(
            RequestModel(
                endpoint = EndpointModel.BOOK_PROGRAM_CONFIRM,
                path = path,
                params = params
            )
        )
    }

    suspend fun bookProgramIntent(params: HashMap<String, Any>): ResponseModel {
        val path = EndpointModel.BOOK_PROGRAM_INTENT.path
        return call(
            RequestModel(
                endpoint = EndpointModel.BOOK_PROGRAM_INTENT,
                path = path,
                params = params
            )
        )
    }

    suspend fun bookSessionConfirm(params: HashMap<String, Any>): ResponseModel {
        val path = EndpointModel.BOOK_SESSION_CONFIRM.path
        return call(
            RequestModel(
                endpoint = EndpointModel.BOOK_SESSION_CONFIRM,
                path = path,
                params = params
            )
        )
    }

    suspend fun bookSessionIntent(params: HashMap<String, Any>): ResponseModel {
        val path = EndpointModel.BOOK_SESSION_INTENT.path
        return call(
            RequestModel(
                endpoint = EndpointModel.BOOK_SESSION_INTENT,
                path = path,
                params = params
            )
        )
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
        return handleAuthResponse(response)
    }

    suspend fun register(params: HashMap<String, Any>): ResponseModel {
        val path = EndpointModel.REGISTER.path
        val response = call(
            RequestModel(endpoint = EndpointModel.REGISTER, path = path, params = params)
        )
        return handleAuthResponse(response)
    }

    suspend fun auth(): ResponseModel {
        val path = EndpointModel.AUTH.path
        val response = call(RequestModel(endpoint = EndpointModel.AUTH, path = path, params = null))
        return handleAuthResponse(response)
    }

    private suspend fun handleAuthResponse(response: ResponseModel): ResponseModel {
        if (!response.isSuccess) return response
        return try {
            val model = UserResponseModel.parse(response.responseString)
            cacheManager.accessToken(model.jwt)
            user()
        } catch (e: Exception) {
            LogManager.log(e)
            return ResponseModel(isSuccess = false, errorMessage = ResponseModel.parseErrorMessage)
        }
    }

    suspend fun user(): ResponseModel {
        val path = EndpointModel.USER.path
        val response = call(RequestModel(endpoint = EndpointModel.USER, path = path, params = null))
        if (response.isSuccess) {
            val user = UserResponseModel.parse(response.responseString).user
            cacheManager.user()?.let { cachedUser ->
                user.iap = cachedUser.iap
            }
            cacheManager.user(user)
            IAPManager.shared.identify(user.uid)
        }
        return response
    }

    suspend fun memberships(): ResponseModel {
        val path = EndpointModel.MEMBERSHIPS.path
        return call(RequestModel(endpoint = EndpointModel.MEMBERSHIPS, path = path, params = null))
    }

    suspend fun paymentAdd(id: String): ResponseModel {
        val path = EndpointModel.PAYMENT_ADD.path + "/" + id
        return call(RequestModel(endpoint = EndpointModel.PAYMENT_ADD, path, params = null))
    }

    suspend fun paymentDelete(id: String): ResponseModel {
        val path = EndpointModel.PAYMENT_DELETE.path + "/" + id
        return call(RequestModel(endpoint = EndpointModel.PAYMENT_DELETE, path, params = null))
    }

    suspend fun programAvailability(
        programKey: String,
        params: HashMap<String, Any>
    ): ResponseModel {
        val path = EndpointModel.PROGRAM_AVAILABILITY.path + "/" + programKey + "/availability"
        return call(
            RequestModel(
                endpoint = EndpointModel.PROGRAM_AVAILABILITY,
                path = path,
                params = params
            )
        )
    }

    suspend fun promos(): ResponseModel {
        val path = EndpointModel.PROMOS.path
        return call(RequestModel(endpoint = EndpointModel.PROMOS, path = path, params = null))
    }

    suspend fun membershipAdd(params: HashMap<String, Any>): ResponseModel {
        val path = EndpointModel.MEMBERSHIP_ADD.path
        return call(
            RequestModel(
                endpoint = EndpointModel.MEMBERSHIP_ADD,
                path = path,
                params = params
            )
        )
    }

    suspend fun membershipDelete(id: String): ResponseModel {
        val path = EndpointModel.MEMBERSHIP_DELETE.path + "/$id"
        return call(
            RequestModel(
                endpoint = EndpointModel.MEMBERSHIP_DELETE,
                path = path,
                params = null
            )
        )
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
        val response = call(
            RequestModel(
                endpoint = EndpointModel.UPDATE,
                path = path,
                params = params
            )
        )
        return if (!response.isSuccess) response else user()
    }

    suspend fun videos(params: HashMap<String, Any>? = null): ResponseModel {
        val path = EndpointModel.VIDEOS.path
        return call(RequestModel(endpoint = EndpointModel.VIDEOS, path = path, params = params))
    }

    suspend fun trainers(): ResponseModel {
        val path = EndpointModel.TRAINERS.path
        return call(RequestModel(endpoint = EndpointModel.TRAINERS, path = path, params = null))
    }

    suspend fun trainer(key: String): ResponseModel {
        val path = "${EndpointModel.TRAINER.path}/$key"
        return call(RequestModel(endpoint = EndpointModel.TRAINER, path = path, params = null))
    }

    suspend fun trainerSessions(trainerKey: String): ResponseModel {
        val path = "${EndpointModel.TRAINER_SESSIONS.path}/$trainerKey/sessions"
        return call(
            RequestModel(endpoint = EndpointModel.TRAINER_SESSIONS, path = path, params = null)
        )
    }

    suspend fun trainerPrograms(trainerKey: String): ResponseModel {
        val path = "${EndpointModel.TRAINER_PROGRAMS.path}/$trainerKey/programs"
        return call(
            RequestModel(endpoint = EndpointModel.TRAINER_PROGRAMS, path = path, params = null)
        )
    }
}
