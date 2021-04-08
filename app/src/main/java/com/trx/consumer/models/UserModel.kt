package com.trx.consumer.models

import com.trx.consumer.models.common.AccountModel
import com.trx.consumer.models.common.BalanceModel
import com.trx.consumer.models.common.ClassModel
import com.trx.consumer.models.common.PurchaseModel

class UserModel(
    var identifier: String = "",
    var email: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var accessToken: String = "",
    var zipCode: String = "",
    var birthday: String = "",
    var phoneNumber: String = "",
    var password: String = "",
    var accessTokenExpiry: Int = 0,
    var accounts: List<AccountModel> = listOf(),
    var balance: BalanceModel = BalanceModel(),
    var purchases: List<PurchaseModel> = listOf(),
    var bookings: List<ClassModel> = listOf(),
    var freeClasses: List<ClassModel> = listOf()
) {

    val fullName: String
        get() = "$firstName $lastName"

    companion object {

        fun test(): UserModel {
            return UserModel(
                identifier = "75396008",
                email = "myo+xpdemoreview1@sprintfwd.com",
                firstName = "Myo",
                lastName = "Kyaw",
                accessToken = "f67526a73b0c439c617bbcca3380df9d",
                accessTokenExpiry = 1605803719,
                zipCode = "90036",
                freeClasses = listOf(ClassModel(), ClassModel())
            )
        }
    }
}
