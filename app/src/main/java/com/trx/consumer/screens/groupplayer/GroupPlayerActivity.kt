package com.trx.consumer.screens.groupplayer

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.trx.consumer.R
import com.trx.consumer.databinding.ActivityGroupPlayerBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.extensions.checkLivePermission
import com.trx.consumer.managers.AnalyticsManager
import com.trx.consumer.managers.LogManager
import com.trx.consumer.models.common.AnalyticsPageModel.GROUP_PLAYER
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.models.responses.LiveResponseModel
import dagger.hilt.android.AndroidEntryPoint
import fm.liveswitch.IAction0
import fm.liveswitch.Promise
import javax.inject.Inject

@AndroidEntryPoint
class GroupPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupPlayerBinding
    lateinit var participants: ArrayList<String>
    var participantsCounter: Int = 0
    lateinit var tabLayout: TabLayout
    lateinit var tabLayoutMediator: TabLayoutMediator

    private var localMediaStarted: Boolean = false

    private val viewModel: GroupPlayerViewModel by viewModels()

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    @Inject
    lateinit var groupPlayerHandler: GroupPlayerHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        participants = ArrayList<String>()
        participants.add("${participantsCounter++}")
        participants.add("${participantsCounter++}")
        participants.add("${participantsCounter++}")
        binding = ActivityGroupPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        doTrackPageView()

        binding.apply {
            btnCamera.action { handleTapCamera() }
            btnClock.action { handleTapClock() }
            btnMicrophone.action { handleTapMicrophone() }
            btnShare.action { handleTapShare() }
            btnCamera.action { handleTapCamera() }
            btnEnd.action { handleTapEnd() }
        }

        onViewCreated(this.findViewById(R.id.groupPlayerSmallGroupView), savedInstanceState)
    }

    private lateinit var smallGroupViewFragmentAdapter: SmallGroupViewFragmentStateAdapter
    private lateinit var viewPager: ViewPager2

    fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        smallGroupViewFragmentAdapter = SmallGroupViewFragmentStateAdapter(this)
        smallGroupViewFragmentAdapter.setParticipants(participants)
        viewPager = view.findViewById(R.id.groupPlayerSmallGroupPager)
        viewPager.adapter = smallGroupViewFragmentAdapter

        tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)
        tabLayoutMediator = TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            // do nothing, we're using dots
            tab.text = ""
        }
        tabLayoutMediator.attach()

        val liveResponseModel = LiveResponseModel.test()
/*
        viewModel.apply {
            model = workout
            eventLoadVideo.observe(this@GroupPlayerActivity, handleLoadVideo)
        }
        viewModel.doLoadVideo()
        */
        playTRXlive(liveResponseModel)
    }

    private fun handleTapCamera() {
        LogManager.log("handleTapCamera")
    }

    private fun handleTapClock() {
        LogManager.log("handleTapClock")
    }

    private fun handleTapMicrophone() {
        LogManager.log("handleTapMicrophone")
    }

    private fun handleTapShare() {
        LogManager.log("handleTapShare")
    }

    private fun handleTapEnd() {
        groupPlayerHandler.stopLocalMedia()
        finish()
    }

    //region Handlers

    private val handleLoadVideo = Observer<WorkoutModel> { model ->
        LogManager.log("handleTapClose")
        playTRXlive(model.live)
    }

    //endregion

    //region Helper Functions

    private fun playTRXlive(value: LiveResponseModel) {
        if (!localMediaStarted) {
            val promise = Promise<Any>()

            val startFn = IAction0 {
                groupPlayerHandler.startTRXLocalMedia().then({ resultStart ->
                    this.runOnUiThread {
                        var localMedia = groupPlayerHandler.getLocalMediaView()
                        var trainerView = findViewById<FrameLayout>(R.id.smallGroupTrainerView)
                        localMedia?.let { lm ->
                            trainerView.addView(lm.view)
                        }
                    }
                    groupPlayerHandler.joinAsync()?.then({ resultJoin ->
                        promise.resolve(null)
                    }) { ex ->
                        promise.reject(ex)
                    }
                }) { ex ->
                    promise.reject(null)
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val requiredPermissions: MutableList<String> = java.util.ArrayList(3)
                if (checkLivePermission(Manifest.permission.RECORD_AUDIO)) {
                    requiredPermissions.add(Manifest.permission.RECORD_AUDIO)
                }
                if (checkLivePermission(Manifest.permission.CAMERA)) {
                    requiredPermissions.add(Manifest.permission.CAMERA)
                }
                if (checkLivePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    requiredPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
                if (checkLivePermission(Manifest.permission.READ_PHONE_STATE)) {
                    requiredPermissions.add(Manifest.permission.READ_PHONE_STATE)
                }
                if (requiredPermissions.size == 0) {
                    startFn.invoke()
                } else {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) || shouldShowRequestPermissionRationale(
                            Manifest.permission.CAMERA
                        ) ||
                        shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                        shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)
                    ) {
                        Toast.makeText(
                            this,
                            "Access to camera, microphone, storage, and phone call state is required",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    requestPermissions(requiredPermissions.toTypedArray(), 1)
                }
            } else {
                startFn.invoke()
            }
        }

        localMediaStarted = true
    }

    fun doTrackPageView() {
        analyticsManager.trackPageView(GROUP_PLAYER)
    }
}
