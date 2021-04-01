package com.trx.consumer.screens.testutility

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentTestUtilityBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.LiveWorkoutModel
import com.trx.consumer.screens.liveworkout.LiveWorkoutAdapter

class TestUtilityFragment : BaseFragment(R.layout.fragment_test_utility) {

    //region Objects
    private val viewModel: TestUtilityViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentTestUtilityBinding::bind)

    private lateinit var liveWorkoutAdapter: LiveWorkoutAdapter

    //endregion

    //region Setup
    override fun bind() {
        liveWorkoutAdapter = LiveWorkoutAdapter(viewModel) { lifecycleScope }

        viewBinding.apply {
            btnBack.action { viewModel.doTapBack() }
            rvLiveWorkouts.adapter = liveWorkoutAdapter
        }

        viewModel.apply {
            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
            eventLoadLiveWorkouts.observe(viewLifecycleOwner, handleLoadLiveWorkouts)

            doLoadView()
        }
    }

    //endregion

    //region Handlers
    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    private val handleLoadLiveWorkouts = Observer<List<LiveWorkoutModel>> { liveWorkouts ->
        liveWorkoutAdapter.update(liveWorkouts)
    }

    //endregion

    //region Functions
    override fun onBackPressed() {
        viewModel.doTapBack()
    }

    //endregion
}
