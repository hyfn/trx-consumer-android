package com.trx.consumer.managers

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import com.google.gson.Gson
import com.trx.consumer.models.UserModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.Date

class CacheManager(context: Context) {

    private val dataStore = context.createDataStore("TrxConsumerDataStore")

    companion object {
        val kBackendAccessToken = preferencesKey<String>("BackendAccessToken")
        val kDidLaunchFromNotification = preferencesKey<String>("DidLaunchFromNotification")
        val kLastFetchFirebaseDate = preferencesKey<String>("LastFetchFirebaseDate")
        val kCurrentUser = preferencesKey<String>("CurrentUser")
    }

    suspend fun accessToken(): String? {
        return withContext(Dispatchers.IO) {
            dataStore.data.map {
                Gson().fromJson(it[kBackendAccessToken], String::class.java) ?: null
            }.firstOrNull()
        }
    }

    suspend fun accessToken(value: String?) {
        withContext(Dispatchers.IO) {
            dataStore.edit {
                it[kBackendAccessToken] = Gson().toJson(value)
            }
        }
    }

    suspend fun user(value: UserModel?) {
        withContext(Dispatchers.IO) {
            dataStore.edit {
                it[kCurrentUser] = Gson().toJson(value)
            }
        }
    }

    suspend fun user(): UserModel? {
        return withContext(Dispatchers.IO) {
            dataStore.data.map {
                Gson().fromJson(it[kCurrentUser], UserModel::class.java) ?: null
            }.firstOrNull()
        }
    }

    suspend fun lastFirebaseFetchDate(): Date? {
        return withContext(Dispatchers.IO) {
            dataStore.data.map {
                Gson().fromJson(it[kLastFetchFirebaseDate], Date::class.java)
            }.firstOrNull()
        }
    }

    suspend fun lastFirebaseFetchDate(date: Date) {
        withContext(Dispatchers.IO) {
            dataStore.edit { it[kLastFetchFirebaseDate] = Gson().toJson(date) }
        }
    }

    fun isUserLoggedIn(): Boolean {
        return false
    }
}
