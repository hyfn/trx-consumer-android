package com.trx.consumer.stripe

import com.stripe.android.Stripe
import com.stripe.android.createPaymentMethod
import com.stripe.android.model.CardParams
import com.stripe.android.model.PaymentMethodCreateParams
import com.trx.consumer.BuildConfig.kStripeApiKey
import com.trx.consumer.base.BaseApi
import com.trx.consumer.managers.CacheManager
import com.trx.consumer.managers.LogManager
import com.trx.consumer.models.common.CardModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StripeBackendManager(
    private val api: BaseApi,
    private val cacheManager: CacheManager,
    private val stripe: Stripe
) {

    suspend fun call(request: StripeRequestModel): StripeResponseModel {
        return withContext(Dispatchers.IO) {
            val endpoint = request.stripeEndpoint
            val url = request.path
            val params = request.params ?: hashMapOf()
            val stripeApiKey = kStripeApiKey
            val token = if (endpoint.isAuthenticated && !stripeApiKey.isNullOrEmpty()) {
                "Bearer $stripeApiKey"
            } else {
                null
            }
            var queryPath = request.path
            if (params.keys.isNotEmpty() || endpoint.isAuthenticated) queryPath += "?"
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

    suspend fun createPaymentMethod(params: HashMap<String, Any>): StripeResponseModel {
        val path = StripeEndpointModel.CREATE_PAYMENT_METHOD.path
        return call(
            StripeRequestModel(
                stripeEndpoint = StripeEndpointModel.CREATE_PAYMENT_METHOD,
                path = path,
                params = params
            )
        )
    }

    suspend fun createStripePaymentMethod(card: CardModel): String? {
        val stripeCard = PaymentMethodCreateParams.createCard(
            CardParams(
                number = card.number,
                expMonth = card.expMonth.replaceFirst("0", "").toInt(),
                expYear = card.expYear.toInt(),
                cvc = card.securityCode
            )
        )
        val paymentMethod = stripe.createPaymentMethod(stripeCard)
        return paymentMethod.id
    }
}
