package com.trx.consumer.screens.groupplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.trx.consumer.R
import com.trx.consumer.databinding.ActivityGroupPlayerBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.managers.AnalyticsManager
import com.trx.consumer.managers.LogManager
import com.trx.consumer.models.common.AnalyticsPageModel.GROUP_PLAYER
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class GroupPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupPlayerBinding
    lateinit var participants: ArrayList<String>
    var participantsCounter: Int = 0
    lateinit var tabLayout: TabLayout
    lateinit var tabLayoutMediator: TabLayoutMediator

    @Inject
    lateinit var analyticsManager: AnalyticsManager

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
            btnCamera.onChecked { isChecked -> handleTapCamera(isChecked) }
            btnClock.onChecked { isChecked -> handleTapClock(isChecked) }
            btnMicrophone.onChecked { isChecked -> handleTapMicrophone(isChecked) }
            btnShare.onChecked { isChecked -> handleTapShare(isChecked) }
            btnCamera.onChecked { isChecked -> handleTapCamera(isChecked) }
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
            //do nothing, we're using dots
            tab.text = ""
        }
        tabLayoutMediator.attach()
    }

    private fun handleTapCamera(isChecked: Boolean) {
        LogManager.log("handleTapCamera $isChecked ")
    }

    private fun handleTapClock(isChecked: Boolean) {
        LogManager.log("handleTapClock $isChecked ")
    }

    private fun handleTapMicrophone(isChecked: Boolean) {
        LogManager.log("handleTapMicrophone $isChecked ")
    }


    private fun handleTapShare(isChecked: Boolean) {
        LogManager.log("handleTapShare $isChecked ")
        participants.add("${participantsCounter++}")
        smallGroupViewFragmentAdapter.setParticipants(participants)
    }

    private fun handleTapEnd() {
        finish()
    }

    fun doTrackPageView() {
        analyticsManager.trackPageView(GROUP_PLAYER)
    }
}
