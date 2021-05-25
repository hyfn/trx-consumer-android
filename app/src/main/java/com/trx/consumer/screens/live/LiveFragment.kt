package com.trx.consumer.screens.live

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentLiveBinding
import com.trx.consumer.extensions.isHidden
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.managers.UtilityManager
import com.trx.consumer.models.common.PromoModel
import com.trx.consumer.models.common.TrainerModel
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.screens.liveworkout.LiveWorkoutAdapter
import com.trx.consumer.screens.promotion.PromoAdapter
import com.trx.consumer.screens.trainerprofile.TrainerProfileAdapter

class LiveFragment : BaseFragment(R.layout.fragment_live) {

    //region Objects
    private val viewModel: LiveViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentLiveBinding::bind)

    private lateinit var todayAdapter: LiveWorkoutAdapter
    private lateinit var tomorrowAdapter: LiveWorkoutAdapter
    private lateinit var recommendedAdapter: LiveWorkoutAdapter
    private lateinit var upcomingAdapter: LiveWorkoutAdapter
    private lateinit var trainerAdapter: TrainerProfileAdapter
    private lateinit var promoAdapter: PromoAdapter

    override fun bind() {
        todayAdapter = LiveWorkoutAdapter(viewModel) { lifecycleScope }
        tomorrowAdapter = LiveWorkoutAdapter(viewModel) { lifecycleScope }
        recommendedAdapter = LiveWorkoutAdapter(viewModel) { lifecycleScope }
        upcomingAdapter = LiveWorkoutAdapter(viewModel) { lifecycleScope }
        trainerAdapter = TrainerProfileAdapter(viewModel) { lifecycleScope }
        promoAdapter = PromoAdapter(viewModel) { lifecycleScope }

        viewBinding.apply {
            viewToday.rvCollection.adapter = todayAdapter
            viewTomorrow.rvCollection.adapter = tomorrowAdapter
            viewUpcoming.rvCollection.adapter = upcomingAdapter
            viewRecommended.rvCollection.adapter = recommendedAdapter
            viewTrainers.rvCollection.adapter = trainerAdapter
            viewPromotions.rvCollection.adapter = promoAdapter
        }

        viewModel.apply {
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            eventLoadPromos.observe(viewLifecycleOwner, handleLoadPromotions)
            eventLoadTrainers.observe(viewLifecycleOwner, handleLoadTrainers)
            eventLoadWorkoutsToday.observe(viewLifecycleOwner, handleLoadWorkoutsToday)
            eventLoadWorkoutsTomorrow.observe(viewLifecycleOwner, handleLoadWorkoutsTomorrow)
            eventLoadWorkoutsRecommended.observe(viewLifecycleOwner, handleLoadWorkoutsRecommended)
            eventLoadWorkoutsUpcoming.observe(viewLifecycleOwner, handleLoadWorkoutsUpcoming)
            eventLoadViewAllUpcoming.observe(viewLifecycleOwner, handleLoadViewAllUpcoming)

            eventShowHud.observe(viewLifecycleOwner, handleShowHud)
            eventShowBooking.observe(viewLifecycleOwner, handleShowBooking)
            eventShowPromo.observe(viewLifecycleOwner, handleShowPromo)
            eventShowTrainer.observe(viewLifecycleOwner, handleShowTrainer)
            eventShowWorkout.observe(viewLifecycleOwner, handleShowWorkout)

            eventTapScheduleToday.observe(viewLifecycleOwner, handleTapScheduleToday)
            eventTapScheduleTomorrow.observe(viewLifecycleOwner, handleTapScheduleTomorrow)

            doLoadView()
        }
    }

    //endregion

    //region Handlers
    private val handleLoadView = Observer<Void> {
    }

    private val handleLoadPromotions = Observer<List<PromoModel>> { promotions ->
        loadPromotions(promotions)
    }

    private val handleLoadTrainers = Observer<List<TrainerModel>> { trainers ->
        loadTrainers(trainers)
    }

    private val handleLoadWorkoutsToday = Observer<List<WorkoutModel>> { workouts ->
        loadWorkoutsToday(workouts)
    }

    private val handleLoadWorkoutsTomorrow = Observer<List<WorkoutModel>> { workouts ->
        loadWorkoutsTomorrow(workouts)
    }

    private val handleLoadWorkoutsRecommended = Observer<List<WorkoutModel>> { workouts ->
        loadWorkoutsRecommended(workouts)
    }

    private val handleLoadWorkoutsUpcoming = Observer<List<WorkoutModel>> { workouts ->
        loadWorkoutsUpcoming(workouts)
    }

    private val handleShowHud = Observer<Boolean> { show ->
        viewBinding.hudView.isVisible = show
    }
    //endregion

    //region Functions

    private fun loadPromotions(promos: List<PromoModel>) {
        val hide = promos.isEmpty()
        promoAdapter.update(promos)
        viewBinding.apply {
            imgLinePromotions.isHidden = hide
            with(viewPromotions) {
                lblTitle.text = getString(R.string.promotions_top_title_label)
                viewMain.isHidden = hide
            }
        }
    }

    private fun loadTrainers(trainers: List<TrainerModel>) {
        val hide = trainers.isEmpty()
        trainerAdapter.update(trainers)
        viewBinding.apply {
            imgLineTrainers.isHidden = hide
            with(viewTrainers) {
                viewMain.isHidden = hide
                lblTitle.text = getString(R.string.live_trainers_title_label)
                btnView.isHidden = true
            }
        }
    }

    private fun loadWorkoutsToday(workouts: List<WorkoutModel>) {
        todayAdapter.update(workouts)
        viewBinding.viewToday.apply {
            viewMain.isHidden = workouts.isEmpty()
            lblTitle.text = getString(R.string.live_today_title_label)
            btnView.text = getString(R.string.live_button_schedule_label)
        }
    }

    private fun loadWorkoutsTomorrow(workouts: List<WorkoutModel>) {
        tomorrowAdapter.update(workouts)
        viewBinding.viewTomorrow.apply {
            viewMain.isHidden = workouts.isEmpty()
            lblTitle.text = getString(R.string.live_tomorrow_title_label)
            btnView.text = getString(R.string.live_button_schedule_label)
        }
    }

    private fun loadWorkoutsRecommended(workouts: List<WorkoutModel>) {
        recommendedAdapter.update(workouts)
        viewBinding.viewRecommended.apply {
            viewMain.isHidden = workouts.isEmpty()
            lblTitle.text = getString(R.string.live_recommended_title_label)
            btnView.isHidden = true
        }
    }

    private fun loadWorkoutsUpcoming(workouts: List<WorkoutModel>) {
        upcomingAdapter.update(workouts)
        viewBinding.viewUpcoming.apply {
            viewMain.isHidden = workouts.isEmpty()
            lblTitle.text = getString(R.string.live_upcoming_title_label)
            btnView.text = getString(R.string.live_button_schedule_label)
        }
    }

    private val handleShowBooking = Observer<WorkoutModel> {
        LogManager.log("handleShowPromo")
        // TODO: Display BookingAlert once implemented
    }

    private val handleShowPromo = Observer<PromoModel> { promo ->
        LogManager.log("handleShowPromo")
        val url = promo.ctaHref
        UtilityManager.shared.openUrl(requireContext(), url)
    }

    private val handleShowTrainer = Observer<TrainerModel> {
        LogManager.log("handleShowPromo")
        NavigationManager.shared.present(this, R.id.trainer_fragment, it)
    }

    private val handleShowWorkout = Observer<WorkoutModel> { workout ->
        LogManager.log("handleShowPromo")
        NavigationManager.shared.present(this, R.id.workout_fragment, workout)
    }

    private val handleLoadViewAllUpcoming = Observer<Void> {
        LogManager.log("handleShowPromo")
        // TODO: Display user Schedule once implemented
    }

    private val handleTapScheduleToday = Observer<Void> {
        LogManager.log("handleShowPromo")
        // TODO: Display Live Schedule once implemented
    }

    private val handleTapScheduleTomorrow = Observer<Void> {
        LogManager.log("handleShowPromo")
        // TODO: Display Live Schedule once implemented
    }
    //endregion
}
