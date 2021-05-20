package com.trx.consumer.screens.workout

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentWorkoutBinding
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.BookingViewState

class WorkoutFragment : BaseFragment(R.layout.fragment_workout) {

    private val viewModel: WorkoutViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentWorkoutBinding::bind)

    override fun bind() {
        viewModel.apply {
            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            doLoadView()
        }

        viewBinding.btnBack.setOnClickListener { viewModel.doTapBack() }
    }

    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    private val handleLoadView = Observer<BookingViewState> {
        viewBinding.apply {
            btnPrimary.bgColor(it.buttonBackgroundColor)
            btnPrimary.textColor(it.buttonTitleColor)
        }
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }
}
