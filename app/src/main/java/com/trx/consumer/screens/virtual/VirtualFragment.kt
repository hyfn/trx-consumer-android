package com.trx.consumer.screens.virtual

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.trx.consumer.BuildConfig.kMatchMeUrl
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentVirtualBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.extensions.isHidden
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.managers.UtilityManager
import com.trx.consumer.models.common.CalendarModel
import com.trx.consumer.models.common.PromoModel
import com.trx.consumer.models.common.TrainerModel
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.screens.promotion.PromoAdapter
import com.trx.consumer.screens.trainerprofile.TrainerProfileAdapter
import com.trx.consumer.screens.virtualworkout.VirtualWorkoutAdapter
import java.util.Locale

class VirtualFragment : BaseFragment(R.layout.fragment_virtual) {

    //region Objects
    private val viewModel: VirtualViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentVirtualBinding::bind)

    private lateinit var virtualWorkoutAdapter: VirtualWorkoutAdapter
    private lateinit var trainerAdapter: TrainerProfileAdapter
    private lateinit var promoAdapter: PromoAdapter

    //endregion

    //region Setup

    override fun bind() {
        virtualWorkoutAdapter = VirtualWorkoutAdapter(viewModel) { lifecycleScope }
        trainerAdapter = TrainerProfileAdapter(viewModel) { lifecycleScope }
        promoAdapter = PromoAdapter(viewModel) { lifecycleScope }

        viewBinding.apply {
            viewUpcoming.rvCollection.adapter = virtualWorkoutAdapter
            viewTrainers.rvCollection.adapter = trainerAdapter
            viewPromos.rvCollection.adapter = promoAdapter

            viewMatchMe.btnMatchMe.action { viewModel.doTapMatchMe() }
            viewSchedule.btnSchedule.action { viewModel.doTapSchedule() }
            viewUpcoming.btnView.action { viewModel.doTapViewAllBookings() }
            viewBookMatchMe.action { viewModel.doTapMatchMe() }
            viewBookWithTrainer.action { viewModel.doTapViewTrainerProfile() }

            viewModel.apply {
                eventLoadView.observe(viewLifecycleOwner, handleLoadView)
                eventLoadBookWith.observe(viewLifecycleOwner, handleLoadBookWith)
                eventLoadCalendar.observe(viewLifecycleOwner, handleLoadCalendar)
                eventLoadMatchMe.observe(viewLifecycleOwner, handleLoadMatchMe)
                eventLoadPromos.observe(viewLifecycleOwner, handleLoadPromos)
                eventLoadTrainers.observe(viewLifecycleOwner, handleLoadTrainers)
                eventLoadWorkouts.observe(viewLifecycleOwner, handleLoadWorkouts)

                eventShowMatchMe.observe(viewLifecycleOwner, handleTapMatchMe)
                eventShowPromo.observe(viewLifecycleOwner, handleShowPromo)
                eventShowUserSchedule.observe(viewLifecycleOwner, handleShowUserSchedule)
                eventShowTrainer.observe(viewLifecycleOwner, handleShowTrainer)
                eventShowTrainerSchedule.observe(viewLifecycleOwner, handleShowTrainerSchedule)
                eventShowWorkout.observe(viewLifecycleOwner, handleShowWorkout)
                eventShowHud.observe(viewLifecycleOwner, handleShowHud)

                viewModel.doLoadView()
            }
        }
    }

    //endregion

    //region Handlers

    private val handleLoadView = Observer<Void> {
        LogManager.log("handleLoadView")
        viewBinding.viewSchedule.viewCalendar.showLabels(false)
    }

    private val handleLoadPromos = Observer<List<PromoModel>> { promos ->
        loadPromos(promos)
    }

    private val handleLoadCalendar = Observer<CalendarModel?> { model ->
        loadCalendar(model)
    }

    private val handleLoadTrainers = Observer<List<TrainerModel>> { trainers ->
        loadTrainers(trainers)
    }

    private val handleLoadBookWith = Observer<TrainerModel?> { trainer ->
        loadBookWith(trainer)
    }

    private val handleLoadMatchMe = Observer<Boolean> { hide ->
        viewBinding.viewMatchMe.viewMain.isHidden = hide
    }

    private val handleTapMatchMe = Observer<Void> {
        UtilityManager.shared.openUrl(requireContext(), kMatchMeUrl)
    }

    private val handleShowPromo = Observer<PromoModel> { promo ->
        LogManager.log("handleShowPromo: ${promo.ctaHref}")
        promo.ctaHref.let { url ->
            if (url.isNotEmpty()) UtilityManager.shared.openUrl(requireContext(), url)
        }
    }

    private val handleShowUserSchedule = Observer<Void> {
        // TODO: Display ScheduleFragment when implemented
    }

    private val handleShowTrainer = Observer<TrainerModel> { trainer ->
        NavigationManager.shared.present(this, R.id.trainer_fragment, trainer)
    }

    private val handleShowTrainerSchedule = Observer<TrainerModel> { trainer ->
        // TODO: Display ScheduleFragment when implemented
    }

    private val handleShowWorkout = Observer<WorkoutModel> { workout ->
        NavigationManager.shared.present(this, R.id.workout_fragment, workout)
    }

    private val handleLoadWorkouts = Observer<List<WorkoutModel>> { workouts ->
        loadWorkouts(workouts)
    }

    private val handleShowHud = Observer<Boolean> { show ->
        viewBinding.hudView.isVisible = show
    }
    //endregion

    //region Functions

    private fun loadWorkouts(workouts: List<WorkoutModel>) {
        val hide = workouts.isEmpty()
        val context = requireContext()
        viewBinding.apply {
            with(viewUpcoming) {
                viewMain.isHidden = hide
                if (!hide) {
                    btnView.text = context.getString(R.string.virtual_upcoming_button_label)
                    lblTitle.text = context.getString(R.string.virtual_upcoming_title_label)
                    virtualWorkoutAdapter.update(workouts)
                    viewBinding.viewMatched
                }
            }
            viewMatched.isHidden = hide
        }
    }

    private fun loadPromos(promos: List<PromoModel>) {
        val hide = promos.isEmpty()
        viewBinding.apply {
            imgLinePromos.isHidden = hide
            with(viewPromos) {
                viewPromos.viewMain.isHidden = hide
                btnView.isHidden = true
                lblTitle.text = requireContext().getString(R.string.promos_top_title_label)
                promoAdapter.update(promos)
            }
        }
    }

    private fun loadBookWith(trainer: TrainerModel?) {
        val hide = trainer == null
        viewBinding.apply {
            trainer?.let {
                lblBookWith.text = requireContext().getString(
                    R.string.virtual_book_with_label_title,
                    trainer.firstName
                ).toUpperCase(Locale.getDefault())
                viewBookWithTrainer.loadView(TrainerModel.test())
                viewBookMatchMe.loadViewMatchMe()
            }
            viewBookWithTrainer.isHidden = hide
            imgLineBookWith.isHidden = hide
            viewBookMatchMe.isHidden = hide
        }
    }

    private fun loadTrainers(trainers: List<TrainerModel>) {
        val hide = trainers.isEmpty()
        viewBinding.viewTrainers.apply {
            viewMain.isHidden = hide
            if (!hide) {
                trainerAdapter.update(trainers)
                btnView.isHidden = true
                lblTitle.text = requireContext().getString(R.string.virtual_trainers_title_label)
            }
        }
    }

    private fun loadCalendar(model: CalendarModel?) {
        val context = requireContext()
        viewBinding.viewSchedule.apply {
            model?.let { safeModel ->
                viewCalendar.loadCalendarModel(safeModel)
                lblTitle.text = context.getString(R.string.virtual_calendar_title_label)
                btnSchedule.text = context.getString(R.string.virtual_calendar_button_label)
            } ?: run {
                viewMain.isHidden = true
                viewBinding.imgLineCalendar.isHidden = true
            }
        }
    }
    //endregion
}
