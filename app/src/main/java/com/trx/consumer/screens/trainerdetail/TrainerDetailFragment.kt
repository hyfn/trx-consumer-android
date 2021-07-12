package com.trx.consumer.screens.trainerdetail

import android.text.SpannableStringBuilder
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.trx.consumer.BuildConfig
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentTrainerDetailBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.extensions.isHidden
import com.trx.consumer.extensions.load
import com.trx.consumer.extensions.spannableString
import com.trx.consumer.extensions.underConstruction
import com.trx.consumer.extensions.upperCased
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.managers.UtilityManager
import com.trx.consumer.models.common.BookingAlertModel
import com.trx.consumer.models.common.ContentModel
import com.trx.consumer.models.common.ScheduleModel
import com.trx.consumer.models.common.TrainerModel
import com.trx.consumer.models.common.TrainerProgramModel
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.models.params.ContentParamsModel
import com.trx.consumer.models.states.ScheduleViewState
import com.trx.consumer.screens.banner.BannerAdapter
import com.trx.consumer.screens.liveworkout.LiveWorkoutAdapter
import com.trx.consumer.screens.photos.PhotoAdapter
import com.trx.consumer.screens.trainerprogram.TrainerProgramAdapter
import com.trx.consumer.screens.video.VideoPlayerActivity
import com.trx.consumer.screens.videoworkout.VideoWorkoutAdapter

class TrainerDetailFragment : BaseFragment(R.layout.fragment_trainer_detail) {

    private val viewModel: TrainerDetailViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentTrainerDetailBinding::bind)

    private lateinit var bannerAdapter: BannerAdapter
    private lateinit var serviceAdapter: TrainerProgramAdapter
    private lateinit var upComingClassesAdapter: LiveWorkoutAdapter
    private lateinit var onDemandClassesAdapter: VideoWorkoutAdapter
    private lateinit var photoAdapter: PhotoAdapter

    override fun bind() {
        val model = NavigationManager.shared.params(this) as TrainerModel
        viewModel.trainer = model

        upComingClassesAdapter = LiveWorkoutAdapter(viewModel) { lifecycleScope }
        onDemandClassesAdapter = VideoWorkoutAdapter(viewModel) { lifecycleScope }
        bannerAdapter = BannerAdapter(viewModel) { lifecycleScope }
        serviceAdapter = TrainerProgramAdapter(viewModel) { lifecycleScope }
        photoAdapter = PhotoAdapter(viewModel) { lifecycleScope }

        viewBinding.apply {
            rvBanner.adapter = bannerAdapter
            rvServices.adapter = serviceAdapter
            rvUpcomingClasses.adapter = upComingClassesAdapter
            rvOnDemandClasses.adapter = onDemandClassesAdapter
            rvPhotos.adapter = photoAdapter

            lblAboutmeDetails.action { viewModel.doTapAboutMe() }
            btnOndemandSeeAll.action { viewModel.doTapOndemandSeeAll() }
            viewRecommendationsForyou.viewMain.action { viewModel.doTapRecommendationsForyou() }
            btnViewSchedule.action { viewModel.doTapUpcomingSchedule() }
            btnBack.action { viewModel.doTapBack() }
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
            eventShowOndemandSeeAll.observe(viewLifecycleOwner, handleShowOndemandSeeAll)
            eventShowPhoto.observe(viewLifecycleOwner, handleShowPhoto)
            eventShowRecommendationsForyou.observe(
                viewLifecycleOwner,
                handleShowRecommendationsForyou
            )
            eventShowService.observe(viewLifecycleOwner, handleShowService)
            eventShowUpcomingSchedule.observe(viewLifecycleOwner, handleShowUpcomingSchedule)
            eventShowWorkout.observe(viewLifecycleOwner, handleShowWorkout)
            eventTapAboutMe.observe(viewLifecycleOwner, handleTapAboutMe)
            eventShowHud.observe(viewLifecycleOwner, handleShowHud)

            doTrackPageView()
            doLoadView()
        }
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }

    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    private val handleLoadView = Observer<Void> {
        viewModel.doLoadData()
        viewModel.doLoadOndemand()
        viewModel.doLoadWorkoutsUpcoming()
        viewModel.doLoadTrainerServices()
        viewModel.doLoadPhotos()
    }

    private val handleShowHud = Observer<Boolean> { show ->
        viewBinding.hudView.isVisible = show
    }

    private val handleLoadBanner = Observer<List<String>> { banners ->
        LogManager.log("handleLoadBanner")
        loadBanner(banners)
    }

    private val handleLoadOndemand = Observer<List<VideoModel>> { workouts ->
        LogManager.log("handleLoadOndemand")
        loadOndemand(workouts)
    }

    private val handleLoadPhotos = Observer<List<String>> { photos ->
        LogManager.log("handleLoadPhotos")
        loadPhotos(photos)
    }

    private val handleLoadTrainer = Observer<TrainerModel> { model ->
        LogManager.log("handleLoadTrainer")
        loadTrainer(model)
    }

    private val handleLoadServices = Observer<List<TrainerProgramModel>> { services ->
        LogManager.log("handleLoadServices")
        loadServices(services)
    }

    private val handleLoadWorkoutsUpcoming = Observer<List<WorkoutModel>> { workouts ->
        LogManager.log("handleLoadWorkoutsUpcoming")
        loadWorkoutsUpcoming(workouts)
    }

    private val handleShowBooking = Observer<BookingAlertModel> { model ->
        LogManager.log("handleShowBooking")
        NavigationManager.shared.present(this, R.id.booking_alert_fragment, model)
    }

    private val handleShowOndemand = Observer<VideoModel> { model ->
        LogManager.log("handleShowOndemand")
        NavigationManager.shared.presentActivity(
            requireActivity(), VideoPlayerActivity::class.java, model
        )
    }

    private val handleShowOndemandSeeAll = Observer<Void> {
        LogManager.log("handleShowOndemandSeeAll")
        requireContext().underConstruction()
    }

    private val handleShowPhoto = Observer<String> { photoUrl ->
        LogManager.log("handleShowPhoto")
        requireContext().underConstruction()
    }

    private val handleShowRecommendationsForyou = Observer<Void> {
        LogManager.log("handleShowRecommendationsForyou")
        UtilityManager.shared.openUrl(requireContext(), BuildConfig.kProductsUrl)
    }

    private val handleShowService = Observer<TrainerProgramModel> { model ->
        LogManager.log("handleShowService")
        val scheduleModel = ScheduleModel(ScheduleViewState.TRAINER_LIVE, model.key, model)
        NavigationManager.shared.present(this, R.id.schedule_fragment, scheduleModel)
    }

    private val handleShowUpcomingSchedule = Observer<String> { trainerKey ->
        LogManager.log("handleShowUpcomingSchedule")
        val model = ScheduleModel(ScheduleViewState.TRAINER_LIVE, trainerKey, null)
        NavigationManager.shared.present(this, R.id.schedule_fragment, model)
    }

    private val handleShowWorkout = Observer<WorkoutModel> { model ->
        LogManager.log("handleShowWorkout")
        NavigationManager.shared.present(this, R.id.workout_fragment, model)
    }

    private val handleTapAboutMe = Observer<ContentModel> { model ->
        LogManager.log("handleTapAboutMe")
        val paramsModel = ContentParamsModel(model = model)
        NavigationManager.shared.present(this, R.id.content_fragment, paramsModel)
    }

    private fun loadBanner(banners: List<String>) {
        val hide = banners.isEmpty()
        viewBinding.rvBanner.isHidden = hide
        bannerAdapter.update(banners)
    }

    private fun loadOndemand(workouts: List<VideoModel>) {
        val hide = workouts.isEmpty()
        onDemandClassesAdapter.update(workouts)
        viewBinding.viewOndemand.isHidden = hide
    }

    private fun loadPhotos(photos: List<String>) {
        val hide = photos.isEmpty()
        photoAdapter.update(photos)
        viewBinding.viewPhotos.isHidden = hide
    }

    private fun loadTrainer(model: TrainerModel) {
        viewBinding.apply {
            imgHeaderTrainer.load(model.profilePhoto)
            lblTrainerName.text = model.fullName
            lblTrainerTagLine.text = model.mantra
            viewRecommendationsForyou.lblRecommendationsForyou.apply {
                text = context.getString(R.string.trainer_recommendation_label, model.fullName)
                    .upperCased()
            }
        }

        loadAboutme(model.bio)
    }

    private fun loadAboutme(aboutMe: String) {
        val hide = aboutMe.isEmpty()
        viewBinding.apply {
            lblAboutmeDetails.apply {
                text = if (aboutMe.length > 180) {
                    SpannableStringBuilder(
                        context.getString(R.string.trainer_bio_label, aboutMe.substring(0, 180))
                    ).append(
                        context.spannableString(
                            context.getString(R.string.trainer_read_more_label),
                            fullUnderline = true
                        )
                    )
                } else aboutMe
            }
            viewAboutMe.isHidden = hide
        }
    }

    private fun loadServices(services: List<TrainerProgramModel>) {
        val hide = services.isEmpty()
        viewBinding.viewService.isHidden = hide
        serviceAdapter.update(services)
    }

    private fun loadWorkoutsUpcoming(workouts: List<WorkoutModel>) {
        val hide = workouts.isEmpty()
        viewBinding.viewUpcoming.isHidden = hide
        upComingClassesAdapter.update(workouts)
    }
}
