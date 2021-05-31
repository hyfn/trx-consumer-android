package com.trx.consumer.screens.trainer

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.trx.consumer.BuildConfig
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.databinding.FragmentTrainerDetailBinding
import com.trx.consumer.extensions.load
import com.trx.consumer.extensions.underConstruction
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.managers.UtilityManager
import com.trx.consumer.models.common.BookingAlertModel
import com.trx.consumer.models.common.ContentModel
import com.trx.consumer.models.common.TrainerModel
import com.trx.consumer.models.common.TrainerProgramModel
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.screens.liveworkout.LiveWorkoutAdapter
import com.trx.consumer.screens.player.PlayerActivity
import com.trx.consumer.screens.videoworkout.VideoWorkoutAdapter

class TrainerDetailFragment : BaseFragment(R.layout.fragment_trainer_detail) {

    private val viewModel: TrainerDetailViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentTrainerDetailBinding::bind)

    private lateinit var upComingClassesAdapter: LiveWorkoutAdapter
    private lateinit var onDemandClassesAdapter: VideoWorkoutAdapter

    override fun bind() {
        upComingClassesAdapter = LiveWorkoutAdapter(viewModel) { lifecycleScope }
        onDemandClassesAdapter = VideoWorkoutAdapter(viewModel) { lifecycleScope }

        viewBinding.apply {
            rvUpcomingClasses.adapter = upComingClassesAdapter
            rvOnDemandClasses.adapter = onDemandClassesAdapter

            btnBack.setOnClickListener { viewModel.doTapBack() }
        }

        viewModel.apply {
            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            eventLoadWorkoutUpcoming.observe(viewLifecycleOwner, handleLoadWorkoutsUpcoming)
            eventLoadServices.observe(viewLifecycleOwner, handleLoadServices)
            eventLoadBanner.observe(viewLifecycleOwner, handleLoadBanner)
            eventLoadPhotos.observe(viewLifecycleOwner, handleLoadPhotos)
            eventLoadOndemand.observe(viewLifecycleOwner, handleLoadOndemand)
            eventLoadTrainer.observe(viewLifecycleOwner, handleLoadTrainer)
            eventShowBooking.observe(viewLifecycleOwner, handleShowBooking)
            eventShowOndemand.observe(viewLifecycleOwner, handleShowOndemand)
            eventShowOndemandSeeAll.observe(viewLifecycleOwner, handleTapBack)
            eventShowPhoto.observe(viewLifecycleOwner, handleShowPhoto)
            eventShowRecommendationsForyou.observe(viewLifecycleOwner, handleShowRecommendationsForyou)
            eventShowService.observe(viewLifecycleOwner, handleShowService)
            eventShowUpcomingSchedule.observe(viewLifecycleOwner, handleShowUpcomingSchedule)
            eventShowWorkout.observe(viewLifecycleOwner, handleShowWorkout)
            eventTapAboutMe.observe(viewLifecycleOwner, handleTapAboutMe)

            doLoadView()
            loadUpComingClasses(WorkoutModel.testListLive(5))
            loadOnDemandClasses(VideoModel.testList(5))
        }
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }

    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    private val handleLoadView = Observer<Void> {

    }

    private val handleLoadBanner = Observer<List<String>> { banners ->
        loadBanner(banners)
    }


    private val handleLoadOndemand = Observer<List<VideoModel>> { workouts ->
        loadOndemand(workouts)
    }


    private val handleLoadPhotos = Observer<List<String>> { photos ->
        loadPhotos(photos)
    }


    private val handleLoadTrainer = Observer<TrainerModel> { model ->
        loadTrainer(model)
    }


    private val handleLoadServices = Observer<List<TrainerProgramModel>> { services ->
        loadServices(services)
    }


    private val handleLoadWorkoutsUpcoming = Observer<List<WorkoutModel>> { workouts ->
        loadWorkoutsUpcoming(workouts)
    }

    private val handleShowBooking = Observer<BookingAlertModel> { model ->
        NavigationManager.shared.present(this, R.id.booking_alert_fragment, model)
    }

    private val handleShowOndemand = Observer<VideoModel> { model ->
        NavigationManager.shared.presentActivity(
            requireActivity(), PlayerActivity::class.java, model)
    }

    private val handleShowOndemandSeeAll = Observer<Void> {
        requireContext().underConstruction()
    }

    private val handleShowPhoto = Observer<String> { photoUrl ->
        requireContext().underConstruction()
    }

    private val handleShowRecommendationsForyou = Observer<Void> {
        UtilityManager.shared.openUrl(requireContext(), BuildConfig.kProductsUrl)
    }

    private val handleShowService = Observer<TrainerProgramModel> { model ->
        // TODO: NavigationManager.shared.present(this, R.id.schedule_fragment, model)
    }

    private val handleShowUpcomingSchedule = Observer<String> { value ->
        // TODO: NavigationManager.shared.present(this, R.id.schedule_fragment, model)
    }

    private val handleShowWorkout = Observer<WorkoutModel> { model ->
        NavigationManager.shared.present(this, R.id.workout_fragment, model)
    }

    private val handleTapAboutMe = Observer<ContentModel> { model ->
        NavigationManager.shared.present(this, R.id.content_fragment, model)
    }

    private fun loadUpComingClasses(workouts: List<WorkoutModel>) {
        upComingClassesAdapter.update(workouts)
    }

    private fun loadOnDemandClasses(videos: List<VideoModel>) {
        onDemandClassesAdapter.update(videos)
    }

    private fun loadBanner(banners: List<String>?) {

    }

    private fun loadOndemand(workouts: List<VideoModel>?) {

    }

    private fun loadPhotos(photos: List<String>?) {

    }

    private fun loadTrainer(model: TrainerModel?) {

    }

    private fun loadServices(services: List<TrainerProgramModel>?) {

    }

    private fun loadWorkoutsUpcoming(workouts: List<WorkoutModel>?) {

    }
}
