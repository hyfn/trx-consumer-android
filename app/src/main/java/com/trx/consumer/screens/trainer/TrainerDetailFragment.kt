package com.trx.consumer.screens.trainer

import android.text.SpannableStringBuilder
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.trx.consumer.BuildConfig
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentTrainerDetailBinding
import com.trx.consumer.extensions.isHidden
import com.trx.consumer.extensions.load
import com.trx.consumer.extensions.spannableString
import com.trx.consumer.extensions.underConstruction
import com.trx.consumer.extensions.upperCased
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.managers.UtilityManager
import com.trx.consumer.models.common.BookingAlertModel
import com.trx.consumer.models.common.ContentModel
import com.trx.consumer.models.common.TrainerModel
import com.trx.consumer.models.common.TrainerProgramModel
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.screens.banner.BannerAdapter
import com.trx.consumer.screens.liveworkout.LiveWorkoutAdapter
import com.trx.consumer.screens.photos.PhotoAdapter
import com.trx.consumer.screens.player.PlayerActivity
import com.trx.consumer.screens.promotion.PromoAdapter
import com.trx.consumer.screens.trainerprogram.TrainerProgramAdapter
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
            eventShowRecommendationsForyou.observe(
                viewLifecycleOwner,
                handleShowRecommendationsForyou
            )
            eventShowService.observe(viewLifecycleOwner, handleShowService)
            eventShowUpcomingSchedule.observe(viewLifecycleOwner, handleShowUpcomingSchedule)
            eventShowWorkout.observe(viewLifecycleOwner, handleShowWorkout)
            eventTapAboutMe.observe(viewLifecycleOwner, handleTapAboutMe)

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
            requireActivity(), PlayerActivity::class.java, model
        )
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

    private fun loadBanner(banners: List<String>) {
        val hide = banners.isEmpty()
        viewBinding.rvBanner.isHidden = hide
        bannerAdapter.update(banners)
    }

    private fun loadOndemand(workouts: List<VideoModel>) {
        onDemandClassesAdapter.update(workouts)
    }

    private fun loadPhotos(photos: List<String>) {
        val hide = photos.isEmpty()
        photoAdapter.update(photos)
        viewBinding.apply {
            rvPhotos.isHidden = hide
            lblPhotos.isHidden = hide
            viewLineBottomPhotos.isHidden = hide
        }
    }

    private fun loadTrainer(model: TrainerModel) {
        viewBinding.apply {
            imgHeaderTrainer.load(model.profilePhoto)
            lblTrainerName.text = model.fullName
            lblTrainerTagLine.text = model.mantra
            viewTrainer.lblRecommendationsForyou.text = "${model.firstName}'s \nProduct\nRecommendations".upperCased()
        }
        loadAboutme(model.bio)
    }

    private fun loadAboutme(aboutMe: String) {
        viewBinding.lblAboutmeDetails.apply {
            text =
                SpannableStringBuilder(
                    context.getString(R.string.trainer_bio_label, aboutMe.substring(0, 180))
                ).append(
                    context.spannableString(
                        context.getString(R.string.trainer_read_more_label), fullUnderline = true
                    )
                )
        }
    }

    private fun loadServices(services: List<TrainerProgramModel>) {
        val hide = services.isEmpty()
        viewBinding.apply {
            lblServices.isHidden = hide
            rvServices.isHidden = hide
            viewLineBottomServices.isHidden = hide
        }
        serviceAdapter.update(services)
    }

    private fun loadWorkoutsUpcoming(workouts: List<WorkoutModel>) {
        upComingClassesAdapter.update(workouts)
    }
}
