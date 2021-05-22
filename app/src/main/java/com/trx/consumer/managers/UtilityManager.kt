package com.trx.consumer.managers

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.trx.consumer.BuildConfig
import com.trx.consumer.BuildConfig.kTRXSupportEmail
import com.trx.consumer.extensions.openBrowser

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
        return "App Version ${buildVersion()}.${appVersion()}"
    }

    fun showSupportEmail(fragment: Fragment) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayListOf(kTRXSupportEmail))
        }
        if (intent.resolveActivity(fragment.requireActivity().packageManager) != null) {
            fragment.startActivity(intent)
        }
    }

    fun openUrl(context: Context, url: String) {
        LogManager.log("openUrl: $url")
        context.openBrowser(url)
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
