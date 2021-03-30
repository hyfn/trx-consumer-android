package com.trx.consumer.core

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.trx.consumer.R
import com.trx.consumer.managers.NavigationManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        start()
    }

    private fun start() {
        findViewById<BottomNavigationView>(R.id.tabBar).itemIconTintList = null
        NavigationManager.shared.launch(this, true)
    }
}
