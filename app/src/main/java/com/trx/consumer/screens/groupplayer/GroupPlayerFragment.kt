package com.trx.consumer.screens.groupplayer

import android.content.pm.ActivityInfo
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.common.CommonButton
import com.trx.consumer.databinding.FragmentGroupPlayerBinding
import com.trx.consumer.databinding.FragmentHomeBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.managers.NavigationManager
import kotlinx.coroutines.Job

class GroupPlayerFragment : BaseFragment(R.layout.fragment_group_player) {

    private val viewBinding by viewBinding(FragmentGroupPlayerBinding::bind)

    private var orientationJob: Job? = null

    private val currentOrientation
        get() = resources.configuration.orientation

    override fun bind() {
        requireActivity().findViewById<CommonButton>(R.id.btnCamera).action {
            if (requireActivity().requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                rotate(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            } else {
                rotate(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
            }
        }
    }

    override fun onBackPressed() {
        NavigationManager.shared.dismiss(this)
    }

    private fun rotate(orientation: Int) {
        requireActivity().requestedOrientation = orientation
    }
}