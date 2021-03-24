package com.trx.consumer.managers

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.trx.consumer.BuildConfig

class UtilityManager {

    fun appVersion(): String {
        return BuildConfig.VERSION_NAME
    }

    fun buildVersion(): String {
        return BuildConfig.VERSION_CODE.toString()
    }

    fun buildVersionInt(): Int {
        return BuildConfig.VERSION_CODE
    }

    fun getTest(fileName: String): String {
        val text = ClassLoader.getSystemResource("$fileName.json").readText()
        val jsonObject = JsonParser().parse(text).asJsonObject
        return Gson().toJson(jsonObject)
    }

    fun versionDisplay(): String {
        return "v" + appVersion() + "." + buildVersion()
    }

    companion object {

        val shared: UtilityManager = getInstance()

        @Volatile
        private var instance: UtilityManager? = null

        private fun init(): UtilityManager = instance ?: synchronized(this) {
            instance ?: UtilityManager().also { instance = it }
        }

        private fun getInstance(): UtilityManager =
            instance ?: init()
    }
}
