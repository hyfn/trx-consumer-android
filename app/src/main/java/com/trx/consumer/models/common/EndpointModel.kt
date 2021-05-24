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
    PLAN_ADD,
    PLAN_DELETE,
    PLANS,
    PROGRAM_AVAILABILITY,
    PROMOS,
    REGISTER,
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
                BOOK_CANCEL, LOGOUT, PAYMENT_DELETE, PLAN_DELETE -> {
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
                PLAN_ADD,
                REGISTER -> {
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
                PLAN_ADD,
                PLAN_DELETE,
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
                PLAN_ADD, PLAN_DELETE, PLANS -> prefix + "subscriptions"
                PROGRAM_AVAILABILITY -> prefix + "programs"
                PROMOS -> prefix + "copy/ctas"
                TRAINER, TRAINERS, TRAINER_PROGRAMS, TRAINER_SESSIONS -> prefix + "trainers"
                UPDATE, USER -> prefix + "user"
                VIDEOS -> prefix + "videos"
                WORKOUTS -> prefix + "sessions"
            }
        }
}
