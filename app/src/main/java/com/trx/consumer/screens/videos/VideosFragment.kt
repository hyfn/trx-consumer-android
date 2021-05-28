package com.trx.consumer.screens.videos

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
import com.trx.consumer.screens.player.PlayerActivity

class VideosFragment : BaseFragment(R.layout.fragment_videos) {

    private val viewModel: VideosViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentVideosBinding::bind)

    private lateinit var adapter: DiscoverAdapter

    override fun bind() {
        val model = NavigationManager.shared.params(this) as VideosModel

        viewModel.apply {
            this.model = model

            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            eventTapVideo.observe(viewLifecycleOwner, handleTapVideo)
            eventTapStartWorkout.observe(viewLifecycleOwner, handleTapStartWorkout)
            eventTapProfile.observe(viewLifecycleOwner, handleTapProfile)

            doLoadView()
        }

        viewBinding.apply {
            adapter = DiscoverAdapter(viewModel) { lifecycleScope }
            rvRelatedVideos.adapter = adapter
            btnPrimary.action { viewModel.doTapPrimary() }
            btnBack.action { viewModel.doTapBack() }
        }
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }

    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    private val handleLoadView = Observer<VideosModel> { model ->
        viewBinding.apply {
            imgHeader.load(model.poster)
            lblTitle.text = model.title
            lblSubtitle.text = model.numberOfVideosDisplay
            lblTrainerName.text = model.trainer.fullName
            lblSummary.text = model.videos.firstOrNull()?.description
            imgTrainerProfile.load(model.trainer.profilePhoto)
            adapter.updateVideos(model.videos)
        }
    }

    private val handleTapVideo = Observer<VideoModel> { model ->
        NavigationManager.shared.present(this, R.id.workout_fragment, model)
    }

    private val handleTapStartWorkout = Observer<VideoModel> { video ->
        NavigationManager.shared.presentActivity(
            requireActivity(),
            PlayerActivity::class.java,
            video
        )
    }

    private val handleTapProfile = Observer<TrainerModel> { model ->
        NavigationManager.shared.present(this, R.id.trainer_fragment, model)
    }
}
