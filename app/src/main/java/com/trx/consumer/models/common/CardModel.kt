package com.trx.consumer.models.common

import android.os.Parcelable
import com.trx.consumer.R
import com.trx.consumer.extensions.lowerCased
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Parcelize
class CardModel(
    var number: String = "",
    var creditCardType: String = "",
    var type: String = "",
    var securityCode: String = "",
    var expMonth: String = "",
    var expYear: String = "",
    var zip: String = ""
) : Parcelable {

    val typeText: Int
        get() {
            return when (creditCardType.lowerCased()) {
                "mc" -> {
                    R.string.cards_type_text_master_card
                }
                "visa" -> {
                    R.string.cards_type_text_visa
                }
                "amex" -> {
                    R.string.cards_type_text_amex
                }
                else -> {
                    R.string.cards_type_text_credit_card
                }
            }
        }

    val imageName: Int
        get() {
            return when (creditCardType.lowerCased()) {
                "mc" -> {
                    R.drawable.ic_img_card_mastercard
                }
                "visa" -> {
                    R.drawable.ic_img_card_visa
                }
                "amex" -> {
                    R.drawable.ic_img_card_amex
                }
                else -> {
                    R.drawable.ic_img_card_stripe
                }
            }
        }

    val params: HashMap<String, Any>
        get() =
            hashMapOf(
                "type" to "card",
                "card" to hashMapOf<String, Any>(
                    "number" to number,
                    "exp_month" to expMonth,
                    "exp_year" to expYear,
                    "cvc" to securityCode
                )
            )

    companion object {

        fun parse(jsonObject: JSONObject): CardModel {
            return CardModel().apply {
                number = jsonObject.optString("cc_last4")
                creditCardType = jsonObject.optString("cc_type")
                type = jsonObject.optString("type")
            }
        }

        fun test(): CardModel {
            return CardModel().apply {
                number = "4242"
                creditCardType = "mc"
                zip = "2222"
                expMonth = "09"
                expYear = "2023"
            }
        }
    }
}
