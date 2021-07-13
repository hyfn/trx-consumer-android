package com.trx.consumer.screens.groupplayer

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
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
import android.content.pm.PackageManager
import android.widget.RelativeLayout
import androidx.activity.viewModels
import androidx.lifecycle.viewModelScope
import com.trx.consumer.extensions.action
import com.trx.consumer.extensions.checkLivePermission
import com.trx.consumer.managers.NavigationManager
import fm.liveswitch.IAction1
import javax.inject.Inject

@AndroidEntryPoint
class GroupPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupPlayerBinding
    lateinit var participants: ArrayList<String>
    var participantsCounter: Int = 0
    lateinit var tabLayout: TabLayout
    lateinit var tabLayoutMediator: TabLayoutMediator

    lateinit var trainerContainer: FrameLayout
    lateinit var localMediaContainer: FrameLayout
    lateinit var smallGroupView: View

    private val viewModel: GroupPlayerViewModel by viewModels()

    //region Objects
    private lateinit var viewBinding: ActivityGroupPlayerBinding

    @Inject
    lateinit var handler: GroupPlayerHandler

    var container: RelativeLayout? = null

    //endregion

    //region Initializers

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

        trainerContainer = findViewById(R.id.smallGroupTrainerView)
        localMediaContainer = findViewById(R.id.groupPlayLocalMediaLayout)
        smallGroupView = findViewById(R.id.groupPlayerSmallGroupView)

        viewBinding = ActivityGroupPlayerBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        if(savedInstanceState != null) {
            this.runOnUiThread {
                var localmedia = groupPlayerHandler.getLocalMediaView()
                localmedia?.let { lm ->
                    lm.start().waitForResult()
                    if(lm.viewSink != null) {
                        lm.viewSink.view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                    }
                    localMediaContainer.addView(lm.view)
                }
                var trainerMedia = groupPlayerHandler.getTrainerMediaView()
                trainerMedia?.let { tm ->
                    trainerContainer.addView(tm.view)
                }
            }
        }
        bind()
    }

    private fun bind() {
        val workout = NavigationManager.shared.params(intent) as? WorkoutModel

        handler.apply {
            if (handlerScope == null) handlerScope = viewModel.viewModelScope
        }

        viewBinding.apply {
            btnCamera.action { viewModel.doTapCamera() }
            btnClock.action { viewModel.doTapClock() }
            btnMic.action { viewModel.doTapMic() }
            btnCast.action { viewModel.doTapCast() }
            btnClose.action { viewModel.doTapClose() }
        }

        viewModel.apply {
            model = workout
            eventLoadVideo.observe(this@GroupPlayerActivity, handleLoadVideo)
            eventLoadError.observe(this@GroupPlayerActivity, handleLoadError)
            eventTapCamera.observe(this@GroupPlayerActivity, handleTapCamera)
            eventTapMic.observe(this@GroupPlayerActivity, handleTapMic)
            eventTapClock.observe(this@GroupPlayerActivity, handleTapClock)
            eventTapCast.observe(this@GroupPlayerActivity, handleTapCast)
            eventTapClose.observe(this@GroupPlayerActivity, handleTapClose)

            doTrackPageView()
            doLoadVideo()
        }


        smallGroupViewFragmentAdapter = SmallGroupViewFragmentStateAdapter(this)
        smallGroupViewFragmentAdapter.setParticipants(participants)
        viewPager = smallGroupView.findViewById(R.id.groupPlayerSmallGroupPager)
        viewPager.adapter = smallGroupViewFragmentAdapter

        tabLayout = smallGroupView.findViewById<TabLayout>(R.id.tab_layout)
        tabLayoutMediator = TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            // do nothing, we're using dots
            tab.text = ""
        }
        tabLayoutMediator.attach()

        groupPlayerHandler.apply {
            eventTrainerLoaded.observe(this@GroupPlayerActivity, addTrainerToLayout)
        }
    }

    private lateinit var smallGroupViewFragmentAdapter: SmallGroupViewFragmentStateAdapter
    private lateinit var viewPager: ViewPager2

    private val addTrainerToLayout = Observer<Boolean> { trainerLoaded ->
        LogManager.log("trainerLoaded")
        this.runOnUiThread {
            var trainerMedia = this.groupPlayerHandler.getTrainerMediaView()
            if (trainerLoaded) {
                trainerMedia?.let { tm ->
                    tm.view.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT, Gravity.CENTER)
                    trainerContainer.addView(tm.view)
                }
            } else {
                trainerContainer.removeAllViewsInLayout()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        var localmedia = groupPlayerHandler.getLocalMediaView()
        localmedia?.let { lm ->
            lm.stop().waitForResult()
        }
        trainerContainer.removeAllViewsInLayout()
        localMediaContainer.removeAllViewsInLayout()
    }

    //endregion

    //region Activity Lifecycle

    override fun onDestroy() {
        super.onDestroy()
        handler.leaveAsync()?.waitForResult()
        container = null
        viewModel.localMediaStarted = true
    }

    //endregion

    //region Activity Helper Overrides

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1) {
            // stream api not used here bc not supported under api 24
            var permissionsGranted = true
            for (grantResult in grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    permissionsGranted = false
                }
            }
            if (permissionsGranted) {
                viewModel.localMediaStarted = false
                viewModel.doLoadVideo()
            } else {
                for (i in grantResults.indices) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        LogManager.log("permission to " + permissions[i] + " not granted")
                    }
                }
                //  TODO: Error Alert fragment implementation
            }
        } else {
            //  TODO: Error Alert fragment implementation
            LogManager.log("Unknown permission requested")
        }
    }

    override fun onBackPressed() {
        stopVideo()
        super.onBackPressed()
    }

    //endregion

    //region Handlers 

    private val handleLoadVideo = Observer<WorkoutModel> { model ->
        LogManager.log("handleLoadView: ${model.identifier}")
        playVideo(model.live)
    }

    private val handleLoadError = Observer<String> { error ->
        LogManager.log("handleLoadError: $error")
        //  TODO: Implement with another ticket.
        // val model = ErrorAlertModel.error(error)
        // NavigationManager.shared.present(this, R.id.error_fragment, model)
    }

    private val handleTapCamera = Observer<Boolean> { isChecked ->
        LogManager.log("handleTapCamera $isChecked ")
    }

    private val handleTapMic = Observer<Boolean> { isChecked ->
        LogManager.log("handleTapMicrophone $isChecked ")
    }

    private val handleTapClock = Observer<Boolean> { isChecked ->
        LogManager.log("handleTapClock $isChecked ")
    }

    private val handleTapCast = Observer<Boolean> { isChecked ->
        LogManager.log("handleTapShare $isChecked ")
    }

    private val handleTapClose = Observer<Void> {
        LogManager.log("handleTapClose")
        stopVideo()
    }

    //endregion

    //region Helper Functions

    //  TODO: Needs to be reworked when permissions screens brought in.
    private fun playVideo(value: LiveResponseModel) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val requiredPermissions: MutableList<String> = ArrayList(3)
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
                handler.start(value)
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO) || shouldShowRequestPermissionRationale(
                        Manifest.permission.CAMERA
                    ) ||
                    shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) ||
                    shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)
                ) {
                    LogManager.log("Error Alert Implementation")
                    //  TODO: Error Alert implementation
                }
                requestPermissions(requiredPermissions.toTypedArray(), 1)
            }
        } else {
            handler.start(value)
        }
    }

    private fun stopVideo() {
        if (viewModel.localMediaStarted) {
            handler.leaveAsync()?.then {
                handler.cleanup().then {
                    container = null
                    finish()
                }?.fail(
                    IAction1 { e ->
                        LogManager.log("Could not stop local media: ${e.message}")
                    }
                )
            }?.fail(
                IAction1 { e ->
                    LogManager.log("Could not leave conference: ${e.message}")
                }
            )
        } else {
            finish()
        }
    }

    //endregion
}
