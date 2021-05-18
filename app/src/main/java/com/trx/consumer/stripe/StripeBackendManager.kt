package com.trx.consumer.stripe

import com.trx.consumer.base.BaseApi
import com.trx.consumer.managers.CacheManager
import com.trx.consumer.managers.LogManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StripeBackendManager(private val api: BaseApi, private val cacheManager: CacheManager) {

    suspend fun call(request: StripeRequestModel): StripeResponseModel {
        return withContext(Dispatchers.IO) {
            val endpoint = request.stripeEndpoint
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
            val stripeResponseModel = StripeResponseModel.parse(
                when (endpoint.type) {
                    StripeEndpointModel.Type.POST -> api.post(url, token, params)
                    StripeEndpointModel.Type.GET -> api.get(url, token, params)
                    StripeEndpointModel.Type.PUT -> api.put(url, token, params)
                    StripeEndpointModel.Type.DELETE -> api.delete(url, token, params)
                    StripeEndpointModel.Type.PATCH -> api.patch(url, token, params)
                }
            )
            LogManager.log("Response: [${endpoint.type.name}] $queryPath \n${stripeResponseModel.responseString}")
            stripeResponseModel
        }
    }

    suspend fun createPaymentMethod(): StripeResponseModel {
        val path = StripeEndpointModel.CREATE_PAYMENT_METHOD.path
        return call(
            StripeRequestModel(
                stripeEndpoint = StripeEndpointModel.CREATE_PAYMENT_METHOD,
                path = path,
                params = null
            )
        )
    }
}
