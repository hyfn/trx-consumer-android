package com.trx.consumer.stripe

enum class StripeEndpointModel {

    CREATE_PAYMENT_METHOD;

    enum class Type {
        GET,
        POST,
        PUT,
        DELETE,
        PATCH
    }

    val isAuthenticated: Boolean
        get() =
            when (this) {
                CREATE_PAYMENT_METHOD -> true
            }

    val path: String
        get() =
            when (this) {
                CREATE_PAYMENT_METHOD -> "https://api.stripe.com/v1/payment_methods"
            }

    val showsHud: Boolean
        get() =
            when (this) {
                CREATE_PAYMENT_METHOD -> true
            }

    val type: Type
        get() =
            when (this) {
                CREATE_PAYMENT_METHOD -> Type.POST
            }

    val testSuccessFilename: String
        get() =
            when (this) {
                CREATE_PAYMENT_METHOD -> "stripe_test_response_createPayment"
            }
}
