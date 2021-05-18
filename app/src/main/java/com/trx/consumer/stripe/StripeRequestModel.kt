package com.trx.consumer.stripe

data class StripeRequestModel(
    val stripeEndpoint: StripeEndpointModel,
    val path: String,
    val params: HashMap<String, Any>?
)
