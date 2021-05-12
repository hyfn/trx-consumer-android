package com.trx.consumer.managers

import com.trx.consumer.base.BaseApi
import com.trx.consumer.models.common.EndpointModel
import com.trx.consumer.models.core.RequestModel
import com.trx.consumer.models.core.ResponseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BackendManager(
    private val api: BaseApi,
    private val cacheManager: CacheManager
) {

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
}
