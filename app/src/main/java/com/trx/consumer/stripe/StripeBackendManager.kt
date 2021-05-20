package com.trx.consumer.stripe

import com.stripe.android.Stripe
import com.stripe.android.createPaymentMethod
import com.stripe.android.model.CardParams
import com.stripe.android.model.PaymentMethodCreateParams
import com.trx.consumer.models.common.CardModel

class StripeBackendManager(private val stripe: Stripe) {

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
