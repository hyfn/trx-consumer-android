package com.trx.consumer.screens.live

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentLiveBinding
import com.trx.consumer.extensions.isHidden
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
    private lateinit var upcomingAdapter: LiveWorkoutAdapter
    private lateinit var trainerAdapter: TrainerProfileAdapter
    private lateinit var promoAdapter: PromoAdapter

    override fun bind() {
        todayAdapter = LiveWorkoutAdapter(viewModel) { lifecycleScope }
        tomorrowAdapter = LiveWorkoutAdapter(viewModel) { lifecycleScope }
        upcomingAdapter = LiveWorkoutAdapter(viewModel) { lifecycleScope }
        trainerAdapter = TrainerProfileAdapter(viewModel) { lifecycleScope }
        promoAdapter = PromoAdapter(viewModel) { lifecycleScope }

        viewBinding.apply {
            viewToday.rvLiveWorkouts.adapter = todayAdapter
            viewTomorrow.rvLiveWorkouts.adapter = tomorrowAdapter
            viewUpcoming.rvLiveWorkouts.adapter = upcomingAdapter
            rvTrainerProfiles.adapter = trainerAdapter
            rvPromos.adapter = promoAdapter
        }

        viewModel.apply {
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            eventLoadPromotions.observe(viewLifecycleOwner, handleLoadPromotions)
            eventLoadTrainers.observe(viewLifecycleOwner, handleLoadTrainers)
            eventLoadWorkoutsToday.observe(viewLifecycleOwner, handleLoadWorkoutsToday)
            eventLoadWorkoutsTomorrow.observe(viewLifecycleOwner, handleLoadWorkoutsTomorrow)
            eventLoadWorkoutsRecommended.observe(viewLifecycleOwner, handleLoadWorkoutsRecommended)

            doLoadView()
            doLoadSessions()
            doLoadTrainers()
            doLoadPromotions()
        }
    }

    //endregion

    //region Handlers
    private val handleLoadView = Observer<Void> {}

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
    //endregion

    //region Functions

    private fun loadPromotions(promos: List<PromoModel>) {
        val hide = promos.isEmpty()
        promoAdapter.update(promos)
        viewBinding.apply {
            imgLinePromotions.isHidden = hide
            viewPromotions.isHidden = hide
        }
    }

    private fun loadTrainers(trainers: List<TrainerModel>) {
        val hide = trainers.isEmpty()
        trainerAdapter.update(trainers)
        viewBinding.apply {
            imgLineTrainers.isHidden = hide
            viewTrainers.isHidden = hide
        }
    }

    private fun loadWorkoutsToday(workouts: List<WorkoutModel>) {
        todayAdapter.update(workouts)
        viewBinding.apply {
            viewToday.viewMain.isHidden = workouts.isEmpty()
            viewToday.lblTitle.text(getString(R.string.live_today_title_label))
        }
    }

    private fun loadWorkoutsTomorrow(workouts: List<WorkoutModel>) {
        tomorrowAdapter.update(workouts)
        viewBinding.apply {
            viewTomorrow.viewMain.isHidden = workouts.isEmpty()
            viewTomorrow.lblTitle.text(getString(R.string.live_tomorrow_title_label))
        }
    }

    private fun loadWorkoutsRecommended(workouts: List<WorkoutModel>) {
        upcomingAdapter.update(workouts)
        viewBinding.viewUpcoming.apply {
            viewMain.isHidden = workouts.isEmpty()
            lblTitle.text(getString(R.string.live_upcoming_title_label))
            btnSchedule.isHidden = true
        }
    }
    //endregion
}
