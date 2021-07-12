package com.trx.consumer.screens.schedule

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentScheduleBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.CalendarModel
import com.trx.consumer.models.common.ScheduleModel
import com.trx.consumer.models.common.WorkoutModel

class ScheduleFragment : BaseFragment(R.layout.fragment_schedule) {

    private val viewModel: ScheduleViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentScheduleBinding::bind)

    private lateinit var scheduleAdapter: ScheduleAdapter

    override fun bind() {

        val model = NavigationManager.shared.params(this) as ScheduleModel

        scheduleAdapter = ScheduleAdapter(viewModel, viewModel, viewModel) { lifecycleScope }

        viewBinding.apply {
            rvWorkout.adapter = scheduleAdapter
            btnBack.action { viewModel.doTapBack() }
        }

        viewModel.apply {
            state = model.state
            key = model.key
            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            eventLoadCalendarView.observe(viewLifecycleOwner, handleLoadCalendarView)
            eventLoadLiveWorkouts.observe(viewLifecycleOwner, handleLoadLiveWorkout)
            doTrackPageView()
            doLoadView()
        }
    }

    private val handleLoadView = Observer<Void> {
        LogManager.log("handleLoadView")
    }

    private val handleTapBack = Observer<Void> {
        LogManager.log("handleTapBack")
        NavigationManager.shared.dismiss(this)
    }

    private val handleLoadCalendarView = Observer<CalendarModel> { item ->
        LogManager.log("handleLoadCalendarView")
    }

    private val handleLoadLiveWorkout = Observer<List<WorkoutModel>> { items ->
        LogManager.log("handleLoadLiveWorkout")
        scheduleAdapter.update(items)
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }
}
