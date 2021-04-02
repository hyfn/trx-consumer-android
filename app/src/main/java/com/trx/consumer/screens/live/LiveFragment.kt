package com.trx.consumer.screens.live

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentLiveBinding
import com.trx.consumer.extensions.isHidden
import com.trx.consumer.models.common.LiveWorkoutModel
import com.trx.consumer.models.common.PromotionModel
import com.trx.consumer.models.common.TrainerModel
import com.trx.consumer.screens.liveworkout.LiveWorkoutAdapter
import com.trx.consumer.screens.promotion.PromotionAdapter
import com.trx.consumer.screens.trainerprofile.TrainerProfileAdapter

class LiveFragment : BaseFragment(R.layout.fragment_live) {

    //region Objects
    private val viewModel: LiveViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentLiveBinding::bind)

    private lateinit var todayAdapter: LiveWorkoutAdapter
    private lateinit var tomorrowAdapter: LiveWorkoutAdapter
    private lateinit var upcomingAdapter: LiveWorkoutAdapter
    private lateinit var trainerAdapter: TrainerProfileAdapter
    private lateinit var promotionAdapter: PromotionAdapter

    override fun bind() {
        todayAdapter = LiveWorkoutAdapter(viewModel) { lifecycleScope }
        tomorrowAdapter = LiveWorkoutAdapter(viewModel) { lifecycleScope }
        upcomingAdapter = LiveWorkoutAdapter(viewModel) { lifecycleScope }
        trainerAdapter = TrainerProfileAdapter(viewModel) { lifecycleScope }
        promotionAdapter = PromotionAdapter(viewModel) { lifecycleScope }

        viewBinding.apply {
            viewToday.rvLiveWorkouts.adapter = todayAdapter
            viewTomorrow.rvLiveWorkouts.adapter = todayAdapter
            viewUpcoming.rvLiveWorkouts.adapter = todayAdapter
            rvTrainerProfiles.adapter = trainerAdapter
            rvPromotions.adapter = promotionAdapter
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
    private val handleLoadView = Observer<Void> {
        viewBinding.apply {
            viewToday.lblTitle.text(getString(R.string.live_today_title_label))
            viewTomorrow.lblTitle.text(getString(R.string.live_tomorrow_title_label))
            viewUpcoming.apply {
                lblTitle.text(getString(R.string.live_upcoming_title_label))
                btnSchedule.isHidden = true
            }
        }
    }

    private val handleLoadPromotions = Observer<List<PromotionModel>> { promotions ->
        promotionAdapter.update(promotions)
    }

    private val handleLoadTrainers = Observer<List<TrainerModel>> { trainers ->
        trainerAdapter.update(trainers)
    }

    private val handleLoadWorkoutsToday = Observer<List<LiveWorkoutModel>> { liveWorkouts ->
        todayAdapter.update(liveWorkouts)
    }

    private val handleLoadWorkoutsTomorrow = Observer<List<LiveWorkoutModel>> { liveWorkouts ->
        tomorrowAdapter.update(liveWorkouts)
    }

    private val handleLoadWorkoutsRecommended = Observer<List<LiveWorkoutModel>> { liveWorkouts ->
        upcomingAdapter.update(liveWorkouts)
    }
    //endregion
}
