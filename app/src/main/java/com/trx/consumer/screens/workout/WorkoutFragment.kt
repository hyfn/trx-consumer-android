package com.trx.consumer.screens.workout

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentWorkoutBinding
import com.trx.consumer.extensions.isHidden
import com.trx.consumer.extensions.load
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.BookingState
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.common.WorkoutModel

class WorkoutFragment : BaseFragment(R.layout.fragment_workout) {

    private val viewModel: WorkoutViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentWorkoutBinding::bind)

    override fun bind() {
        val model = NavigationManager.shared.params(this)
        viewModel.model = if (model is VideoModel) {
            WorkoutModel().apply {
                mode = ""
                state = BookingState.VIDEO
                video = model
            }
        } else model as WorkoutModel

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

    private val handleLoadView = Observer<WorkoutModel> { model ->

        viewBinding.apply {
            imgHeader.load(model.video.poster)
            lblTitle.text = model.video.name
            lblSummary.text = model.video.description
            lblSubtitle.text = model.video.videoDuration

            viewTrainer.isHidden = false
            imgTrainerProfile.load(model.video.trainer.profilePhoto)
            lblTrainerName.text = model.video.trainer.fullName
            btnPrimary.bgColor(model.state.buttonBackgroundColor)
            btnPrimary.textColor(model.state.buttonTitleColor)
            btnPrimary.apply { text = context.getString(model.state.buttonTitle) }
        }
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }
}
