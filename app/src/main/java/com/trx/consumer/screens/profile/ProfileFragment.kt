package com.trx.consumer.screens.profile

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentProfileBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.VirtualWorkoutModel
import com.trx.consumer.models.common.WorkoutModel

class ProfileFragment : BaseFragment(R.layout.fragment_profile) {

    private val viewModel: ProfileViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentProfileBinding::bind)

    private lateinit var profileAdapter: ProfileAdapter

    override fun bind() {
        profileAdapter = ProfileAdapter(viewModel, viewModel) { lifecycleScope }

        viewBinding.apply {
            rvWorkouts.adapter = profileAdapter

            btnBack.action { viewModel.doTapBack() }
            btnSettings.action { viewModel.doTapSettings() }
        }

        viewModel.apply {
            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
            eventTapSettings.observe(viewLifecycleOwner, handleTapSettings)
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)

            doLoadView()
        }
    }

    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    private val handleTapSettings = Observer<Void> {
        NavigationManager.shared.present(this, R.id.settings_fragment)
    }
    private val handleLoadView = Observer<Void> {
        viewBinding.apply {
            lblTitle.text = "Kristoph Banders"
            btnLive.text = "5 Live"
            btnVirtual.text = "7 Virtual"
            btnLive.textColor(R.color.greyDark)
            btnVirtual.textColor(R.color.greyLight)

            btnLive.action {
                btnVirtual.textColor(R.color.greyLight)
                btnLive.textColor(R.color.greyDark)
                profileAdapter.update(WorkoutModel.testList(5))
            }

            btnVirtual.action {
                btnLive.textColor(R.color.greyLight)
                btnVirtual.textColor(R.color.greyDark)
                profileAdapter.update(VirtualWorkoutModel.testListToday(7))
            }

            profileAdapter.update(WorkoutModel.testList(5))
        }
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }
}
