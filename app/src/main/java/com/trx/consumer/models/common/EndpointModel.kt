package com.trx.consumer.models.common

import com.trx.consumer.BuildConfig.kBaseUrl

enum class EndpointModel {

    BANNER,
    BOOK_CANCEL,
    BOOKINGS,
    BOOK_PROGRAM_CONFIRM,
    BOOK_PROGRAM_INTENT,
    BOOK_SESSION_CONFIRM,
    BOOK_SESSION_INTENT,
    LOGIN,
    LOGOUT,
    PAYMENT_ADD,
    PAYMENT_DELETE,
    PLANS,
    PROGRAM_AVAILABILITY,
    PROMOTIONS,
    REGISTER,
    SUBSCRIPTION_ADD,
    SUBSCRIPTION_DELETE,
    TRAINER,
    TRAINERS,
    TRAINER_PROGRAMS,
    TRAINER_SESSIONS,
    UPDATE,
    USER,
    VIDEOS,
    WORKOUTS;

    enum class Type {
        GET,
        POST,
        PUT,
        DELETE,
        PATCH
    }

    val type: Type
        get() {
            return when (this) {
                BOOK_CANCEL, LOGOUT, PAYMENT_DELETE, SUBSCRIPTION_DELETE -> {
                    Type.DELETE
                }
                UPDATE -> {
                    Type.PATCH
                }
                BOOK_PROGRAM_CONFIRM,
                BOOK_PROGRAM_INTENT,
                BOOK_SESSION_CONFIRM,
                BOOK_SESSION_INTENT,
                PAYMENT_ADD,
                REGISTER,
                SUBSCRIPTION_ADD -> {
                    Type.POST
                }
                LOGIN -> {
                    Type.PUT
                }
                else -> {
                    Type.GET
                }
            }
        }

    val isAuthenticated: Boolean
        get() {
            return when (this) {
                BOOK_CANCEL,
                BOOKINGS,
                BOOK_PROGRAM_CONFIRM,
                BOOK_PROGRAM_INTENT,
                BOOK_SESSION_CONFIRM,
                BOOK_SESSION_INTENT,
                LOGOUT,
                PAYMENT_ADD,
                PAYMENT_DELETE,
                SUBSCRIPTION_ADD,
                SUBSCRIPTION_DELETE,
                UPDATE,
                USER -> true
                else -> false
            }
        }

    val path: String
        get() {
            val prefix = kBaseUrl
            return when (this) {
                BANNER -> "copy/mobile-modal"
                BOOK_CANCEL -> prefix + "bookings/cancel"
                BOOKINGS -> prefix + "bookings"
                BOOK_PROGRAM_CONFIRM -> prefix + "bookings/program/confirm"
                BOOK_PROGRAM_INTENT -> prefix + "bookings/program"
                BOOK_SESSION_CONFIRM -> prefix + "bookings/session/confirm"
                BOOK_SESSION_INTENT -> prefix + "bookings/session"
                LOGIN, LOGOUT, REGISTER -> prefix + "auth"
                PAYMENT_ADD, PAYMENT_DELETE -> prefix + "stripe/payment-method"
                PLANS -> prefix + "subscriptions"
                PROGRAM_AVAILABILITY -> prefix + "programs"
                PROMOTIONS -> prefix + "copy/ctas"
                SUBSCRIPTION_ADD, SUBSCRIPTION_DELETE -> prefix + "subscriptions"
                TRAINER, TRAINERS, TRAINER_PROGRAMS, TRAINER_SESSIONS -> prefix + "trainers"
                UPDATE, USER -> prefix + "user"
                VIDEOS -> prefix + "videos"
                WORKOUTS -> prefix + "sessions"
            }
        }
}
