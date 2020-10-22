package com.trx.consumer.services.user

import javax.inject.Inject

class UserRepository @Inject constructor() : UserDataSource {

    override fun isUserLoggedIn(): Boolean {
        return false
    }
}
