package com.trx.consumer.screens.videos

import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentVideosBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.extensions.load
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.TrainerModel
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.common.VideosModel
import com.trx.consumer.screens.discover.list.DiscoverAdapter

class VideosFragment : BaseFragment(R.layout.fragment_videos) {

    //region Objects

    private val viewModel: VideosViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentVideosBinding::bind)

    private lateinit var adapter: DiscoverAdapter

    //endregion

    //region Initializers

    override fun bind() {
        val model = NavigationManager.shared.params(this) as VideosModel

        viewBinding.apply {
            adapter = DiscoverAdapter(viewModel) { lifecycleScope }
            viewRelatedWorkout.adapter = adapter
            btnBack.action { viewModel.doTapBack() }
        }

        viewModel.apply {
            this.model = model

            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            eventTapVideo.observe(viewLifecycleOwner, handleTapVideo)
            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
            eventTapProfile.observe(viewLifecycleOwner, handleTapProfile)

            doLoadView()
        }
    }

    //endregion 

    //region Handlers

    private val handleLoadView = Observer<VideosModel> { model ->
        viewBinding.apply {
            imgHeader.load(model.poster)
            lblTitle.text = model.title
            lblSubtitle.text = model.numberOfVideosDisplay
            imgTrainerPhoto.load(model.trainer.profilePhoto)
            lblTrainerName.text = model.trainer.fullName
            btnTrainerProfile.apply {
                isGone = model.trainer.fullName.isEmpty()
                action { viewModel.doTapProfile() }
            }

            model.description.let { description ->
                lblSummary.text = if (description.isNotEmpty()) {
                    description
                } else {
                    model.videos.firstOrNull()?.description
                }
            }

            adapter.updateVideos(model.videos)
        }
    }

    private val handleTapVideo = Observer<VideoModel> { model ->
        NavigationManager.shared.present(this, R.id.workout_fragment, model)
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }

    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    private val handleTapProfile = Observer<TrainerModel> { model ->
        NavigationManager.shared.present(this, R.id.trainer_fragment, model)
    }

    //endregion
}
