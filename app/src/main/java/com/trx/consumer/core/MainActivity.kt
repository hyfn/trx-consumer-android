package com.trx.consumer.core

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.trx.consumer.R
import com.trx.consumer.module.splash.SplashFragment
import timber.log.Timber

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            if (savedInstanceState == null) {
                val fragment = SplashFragment()
                show(fragment)
            }
        } catch (e: IllegalStateException) {
            Timber.w(e)
        }
    }

    fun show(fragment: BaseFragment) {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.mainContainer, fragment)
            .addToBackStack(fragment.javaClass.simpleName)
            .commit()
    }

    fun dismiss() {
        supportFragmentManager.popBackStack()
    }

    companion object {
        private const val TAG = "MainActivity"
        var instance: MainActivity? = null
    }
}
