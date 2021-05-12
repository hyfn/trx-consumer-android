package com.trx.consumer.models.common

enum class EndpointModel {

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
}
