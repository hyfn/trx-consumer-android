package com.trx.consumer.views.input

import android.text.InputType
import android.util.Patterns
import com.google.android.material.textfield.TextInputEditText
import com.trx.consumer.BuildConfig.kCVCLimit
import com.trx.consumer.BuildConfig.kCreditCardLengthAmex
import com.trx.consumer.BuildConfig.kCreditCardLengthStandard
import com.trx.consumer.BuildConfig.kDefaultTextLimit
import com.trx.consumer.BuildConfig.kExpirationDateLimit
import com.trx.consumer.BuildConfig.kPasswordMinLength
import com.trx.consumer.BuildConfig.kVerificationCodeLimit
import com.trx.consumer.BuildConfig.kZipcodeMinLength
import com.trx.consumer.R
import java.util.Calendar
import java.util.Date
import java.util.regex.Pattern

enum class InputViewState(val placeholder: Int, vararg val type: Int) {

    BIRTHDAY(
        R.string.inputview_placeholder_birthday,
        InputType.TYPE_NULL,
    ),
    CARD_NUMBER(
        R.string.inputview_placeholder_card_number,
        InputType.TYPE_CLASS_PHONE
    ),
    CODE(
        R.string.inputview_placeholder_code,
        InputType.TYPE_CLASS_NUMBER
    ),
    CVC(
        R.string.inputview_placeholder_cvc,
        InputType.TYPE_CLASS_NUMBER
    ),
    EMAIL(
        R.string.inputview_placeholder_email,
        InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
    ),
    EXPIRATION(
        R.string.inputview_placeholder_expiration,
        InputType.TYPE_CLASS_NUMBER
    ),
    FIRST(
        R.string.inputview_placeholder_first,
        InputType.TYPE_TEXT_VARIATION_PERSON_NAME,
        InputType.TYPE_TEXT_FLAG_CAP_WORDS
    ),
    LAST(
        R.string.inputview_placeholder_last,
        InputType.TYPE_TEXT_VARIATION_PERSON_NAME,
        InputType.TYPE_TEXT_FLAG_CAP_WORDS
    ),
    PASSWORD(
        R.string.inputview_placeholder_password,
        InputType.TYPE_TEXT_VARIATION_PASSWORD
    ),
    PHONE(
        R.string.inputview_placeholder_phone,
        InputType.TYPE_CLASS_PHONE
    ),
    PROMO(
        R.string.inputview_placeholder_promo,
        InputType.TYPE_CLASS_TEXT
    ),
    ZIPCODE(
        R.string.inputview_placeholder_zip,
        InputType.TYPE_CLASS_NUMBER
    );

    val errorMessage: Int
        get() = when (this) {
            BIRTHDAY -> R.string.inputview_error_message_birthday
            FIRST -> R.string.inputview_error_message_first_name
            CARD_NUMBER -> R.string.inputview_error_message_card_number
            CODE -> R.string.inputview_error_message_code
            CVC -> R.string.inputview_error_message_cvc
            EMAIL -> R.string.inputview_error_message_email
            EXPIRATION -> R.string.inputview_error_message_expiration
            LAST -> R.string.inputview_error_message_last_name
            PASSWORD -> R.string.inputview_error_message_password
            PHONE -> R.string.inputview_error_message_phone_number
            ZIPCODE -> R.string.inputview_error_message_zip
            else -> R.string.content_blank
        }

    fun shouldChange(text: String): Boolean? {
        return when (this) {
            CARD_NUMBER -> text.count() <= kCreditCardLengthStandard
            CODE -> text.count() <= kVerificationCodeLimit
            CVC -> text.count() <= kCVCLimit
            EMAIL -> !text.contains(" ")
            EXPIRATION -> null
            PASSWORD -> !text.contains(" ")
            ZIPCODE -> text.count() <= kZipcodeMinLength
            else -> text.count() <= kDefaultTextLimit
        }
    }

    fun validate(text: String): Boolean {
        return when (this) {
            BIRTHDAY -> text.isNotEmpty()
            CARD_NUMBER -> text.count() in listOf(kCreditCardLengthAmex, kCreditCardLengthStandard)
            CVC -> text.count() >= kCVCLimit - 1
            EMAIL -> Patterns.EMAIL_ADDRESS.matcher(text).matches()
            EXPIRATION -> text.count() == kExpirationDateLimit
            FIRST -> !Pattern.compile("[^a-zA-Z]").matcher(text).find()
            LAST -> !Pattern.compile("[^a-zA-Z]").matcher(text).find()
            PASSWORD -> text.count() >= kPasswordMinLength
            PHONE -> Patterns.PHONE.matcher(text).matches()
            ZIPCODE -> text.count() == kZipcodeMinLength
            else -> text.isNotEmpty()
        }
    }

    fun format(textView: TextInputEditText, before: Int, count: Int): Boolean? {
        return when (this) {
            EXPIRATION -> expirationFormat(textView, before, count)
            else -> {
                null
            }
        }
    }

    private fun expirationFormat(textView: TextInputEditText, before: Int, count: Int): Boolean? {
        textView.text?.let { userInput ->
            when (userInput.length) {
                1 -> {
                    if (userInput.first().toString().toInt() !in intArrayOf(0, 1) && before < count) {
                        val inputString = userInput.toString().toMutableList()
                        inputString.add(0, '0')
                        inputString.add('/')
                        textView.setText(inputString.joinToString(""))
                        textView.setSelection(inputString.count())
                    }
                    return null
                }
                2 -> {
                    if (userInput[1].toInt() == 0) {
                        textView.text?.clear()
                        textView.setSelection(0)
                    }
                    if (!userInput.contains('/') && before < count) {
                        val inputString = userInput.toString().toMutableList()
                        inputString.add('/')
                        textView.setText(inputString.joinToString(""))
                        textView.setSelection(inputString.count())
                    }
                    return null
                }
                kExpirationDateLimit -> return true
                (kExpirationDateLimit + 1) -> return false
                else -> null
            }
        }
        return null
    }

    fun expirationValidation(date: String): Boolean {
        return if (date.isNotEmpty()) {
            // Check to see if valid month input.
            val monthInt = date.substringBefore("/").toInt()
            val yearInt = date.substringAfter("/").toInt()
            if (monthInt !in 1..12) {
                return false
            }

            // Date range for Calendar is 0 (Jan) - 11 (Dec)
            val inputDate = Calendar.getInstance().apply {
                set(Calendar.MONTH, monthInt - 1)
                set(Calendar.YEAR, yearInt)
            }
            val inputMonth = inputDate.get(Calendar.MONTH)
            val inputYear = inputDate.get(Calendar.YEAR)

            val currentDate = Calendar.getInstance()
            val currentMonth = currentDate.get(Calendar.MONTH)
            val currentYear = currentDate.get(Calendar.YEAR)

            when {
                inputYear > currentYear -> true
                inputYear == currentYear -> (inputMonth > currentMonth)
                else -> false
            }
        } else {
            false
        }
    }

    val date: Date?
        get() {
            return when (this) {
                BIRTHDAY -> {
                    val calendar = Calendar.getInstance()
                    calendar.set(2000, 1, 1)
                    calendar.time
                }
                else -> null
            }
        }

    val maximumDate: Date?
        get() {
            return when (this) {
                BIRTHDAY -> Date()
                else -> null
            }
        }

    val minimumDate: Date?
        get() {
            return when (this) {
                BIRTHDAY -> {
                    val calendar = Calendar.getInstance()
                    calendar.set(1900, 1, 1)
                    calendar.time
                }
                else -> null
            }
        }

    val dateFormat: String?
        get() {
            return when (this) {
                BIRTHDAY -> "yyyy-MM-dd"
                else -> null
            }
        }
}
