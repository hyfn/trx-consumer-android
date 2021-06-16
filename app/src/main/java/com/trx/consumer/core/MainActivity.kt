package com.trx.consumer.core

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.trx.consumer.R
import com.trx.consumer.managers.BackendManager
import com.trx.consumer.managers.CacheManager
import com.trx.consumer.managers.NavigationManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var backendManager: BackendManager

    @Inject
    lateinit var cacheManager: CacheManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        start()
    }

    private fun start() {
        findViewById<BottomNavigationView>(R.id.tabBar).itemIconTintList = null
        val activity = this
        lifecycleScope.launch {
            val isLoggedIn = cacheManager.isLoggedIn()
            NavigationManager.shared.launch(activity, isLoggedIn)
            if (!isLoggedIn) backendManager.updateBeforeLogout()
        }
    }
}
