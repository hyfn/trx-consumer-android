package com.trx.consumer.screens.liveplayer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.trx.consumer.R
import com.trx.consumer.managers.AnalyticsManager
import com.trx.consumer.models.common.AnalyticsPageModel.LIVE_PLAYER
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LivePlayerActivity : AppCompatActivity() {

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_player)
        doTrackPageView()
    }

    fun doTrackPageView() {
        analyticsManager.trackPageView(LIVE_PLAYER)
    }
}
