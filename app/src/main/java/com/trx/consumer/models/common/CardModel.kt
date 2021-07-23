package com.trx.consumer.models.common

import android.os.Parcelable
import androidx.annotation.DrawableRes
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

    @get:DrawableRes
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
        get() {
            return HashMap<String, Any>().apply {
                put(
                    "payment_source",
                    HashMap<String, Any>().apply {
                        put("cc_exp_month", expMonth)
                        put("cc_exp_year", expYear)
                        put("cc_number", number.trim())
                        put("cc_security_code", securityCode)
                        put("is_default", true)
                        put("zip", zip)
                    }
                )
            }
        }

    companion object {

        fun parse(jsonObject: JSONObject): CardModel {
            return CardModel().apply {
                number = jsonObject.optString("last4")
                creditCardType = jsonObject.optString("brand")
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
