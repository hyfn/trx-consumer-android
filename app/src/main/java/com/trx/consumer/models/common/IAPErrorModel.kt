package com.trx.consumer.models.common

enum class IAPErrorModel {

    MEMBERSHIPS,
    USER,
    MEMBERSHIP_ADD,
    MEMBERSHIP_ADD_RESTORE,
    NO_PRODUCT_ID_MATCH,
    NEEDS_UPDATE,
    RESTORE,
    PURCHASE;

    val display: String
        get() = when (this) {
            MEMBERSHIPS -> "Could not load memberships"
            USER -> "Could not load your memberships"
            MEMBERSHIP_ADD -> "There was a purchase error"
            MEMBERSHIP_ADD_RESTORE -> "There was an error while restoring your purchases"
            NO_PRODUCT_ID_MATCH -> "Could not find a matching product identifier"
            PURCHASE -> "There was a Play Store error"
            RESTORE -> "Could not restore your purchases"
            NEEDS_UPDATE -> "Could not find products to restore"
        }
}
