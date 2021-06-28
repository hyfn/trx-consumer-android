package com.trx.consumer.screens.schedule

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentScheduleBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.models.states.ScheduleViewState

class ScheduleFragment : BaseFragment(R.layout.fragment_schedule) {

    private val viewModel: ScheduleViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentScheduleBinding::bind)

    private lateinit var scheduleAdapter: ScheduleAdapter

    override fun bind() {
        scheduleAdapter = ScheduleAdapter(viewModel, viewModel, viewModel) { lifecycleScope }

        viewBinding.apply {
            rvWorkout.adapter = scheduleAdapter
            btnBack.action { viewModel.doTapBack() }
        }

        viewModel.apply {
            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)

            doTrackPageView()
            doLoadView()
        }
    }

    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    private val handleLoadView = Observer<ScheduleViewState> {
        viewBinding.apply {
            lblTitle.text = "May"
            scheduleAdapter.update(WorkoutModel.testListLive(5))
        }
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }
}
