package com.trx.consumer.models.core

import com.trx.consumer.models.common.EndpointModel

data class RequestModel(
    val endpoint: EndpointModel,
    val path: String,
    val params: Map<String, Any>?
)
