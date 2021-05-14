package com.trx.consumer.screens.home

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentHomeBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.extensions.isHidden
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.UserModel
import com.trx.consumer.models.common.PromotionModel
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.common.VirtualWorkoutModel
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.screens.liveworkout.LiveWorkoutAdapter
import com.trx.consumer.screens.promotion.PromotionAdapter
import com.trx.consumer.screens.videoworkout.VideoWorkoutAdapter
import com.trx.consumer.screens.virtualworkout.VirtualWorkoutAdapter

class HomeFragment : BaseFragment(R.layout.fragment_home) {

    //region Objects
    private val viewModel: HomeViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentHomeBinding::bind)

    private lateinit var promotionTopAdapter: PromotionAdapter
    private lateinit var upcomingAdapter: VirtualWorkoutAdapter
    private lateinit var bookWithAdapter: VirtualWorkoutAdapter
    private lateinit var liveAdapter: LiveWorkoutAdapter
    private lateinit var onDemandAdapter: VideoWorkoutAdapter
    private lateinit var promotionBottomAdapter: PromotionAdapter

    //endregion

    //region Setup
    override fun bind() {
        promotionTopAdapter = PromotionAdapter(viewModel) { lifecycleScope }
        upcomingAdapter = VirtualWorkoutAdapter(viewModel) { lifecycleScope }
        bookWithAdapter = VirtualWorkoutAdapter(viewModel) { lifecycleScope }
        liveAdapter = LiveWorkoutAdapter(viewModel) { lifecycleScope }
        onDemandAdapter = VideoWorkoutAdapter(viewModel) { lifecycleScope }
        promotionBottomAdapter = PromotionAdapter(viewModel) { lifecycleScope }

        viewBinding.apply {
            btnTest.action { viewModel.doTapTest() }

            viewForYouTop.rvPromotions.adapter = promotionTopAdapter
            viewUpcoming.rvVirtualWorkouts.adapter = upcomingAdapter
            viewBookWith.rvVirtualWorkouts.adapter = bookWithAdapter
            viewLive.rvLiveWorkouts.adapter = liveAdapter
            viewOnDemand.rvVideoWorkouts.adapter = onDemandAdapter
            viewForYouBottom.rvPromotions.adapter = promotionBottomAdapter
        }

        viewModel.apply {
            eventTapTest.observe(viewLifecycleOwner, handleTapTest)

            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            eventLoadPromotionsTop.observe(viewLifecycleOwner, handleLoadPromotionsTop)
            eventLoadUpcoming.observe(viewLifecycleOwner, handleLoadUpcoming)
            eventLoadBookWith.observe(viewLifecycleOwner, handleLoadBookWith)
            eventLoadLive.observe(viewLifecycleOwner, handleLive)
            eventLoadOnDemand.observe(viewLifecycleOwner, handleOnDemand)
            eventLoadPromotionsBottom.observe(viewLifecycleOwner, handleLoadPromotionsBottom)
            eventLoadUser.observe(viewLifecycleOwner, handleLoadUser)

            doLoadView()
            doLoadPromotionsTop()
            doLoadUpcoming()
            doLoadBookWith()
            doLoadLive()
            doLoadOnDemand()
            doLoadPromotionsBottom()
        }
    }
    //endregion

    //region Handlers

    private val handleTapTest = Observer<Void> {
        LogManager.log("handleTapTest")
        NavigationManager.shared.present(this, R.id.test_utility_fragment)
    }

    private val handleLoadView = Observer<Void> {
        viewBinding.apply {
            with(requireContext()) {
                lblClasses.text = getString(R.string.home_user_classes_label, 0)
                lblSessions.text = getString(R.string.home_user_sessions_label, 0)
                lblWorkouts.text = getString(R.string.home_user_workouts_label, 0)
            }
        }
    }

    private val handleLoadPromotionsTop = Observer<List<PromotionModel>> { promotions ->
        loadPromotionsTop(promotions)
    }

    private val handleLoadUpcoming = Observer<List<VirtualWorkoutModel>> { workouts ->
        loadUpcoming(workouts)
    }

    private val handleLoadBookWith = Observer<List<VirtualWorkoutModel>> { workouts ->
        loadBookWith(workouts)
    }

    private val handleLive = Observer<List<WorkoutModel>> { workouts ->
        loadLive(workouts)
    }

    private val handleOnDemand = Observer<List<VideoModel>> { workouts ->
        loadOnDemand(workouts)
    }

    private val handleLoadPromotionsBottom = Observer<List<PromotionModel>> { promotions ->
        loadPromotionsBottom(promotions)
    }

    private val handleLoadUser = Observer<UserModel> { user ->
        loadUser(user)
    }

    //endregion

    //region Functions

    private fun loadPromotionsTop(promotions: List<PromotionModel>) {
        val hide = promotions.isEmpty()
        promotionTopAdapter.update(promotions)
        viewBinding.apply {
            viewForYouTop.lblTitle.text = getString(R.string.home_promotions_top_title_label)
            imgLineForYouTop.isHidden = hide
            viewForYouTop.viewMain.isHidden = hide
        }
    }

    private fun loadUpcoming(workouts: List<VirtualWorkoutModel>) {
        upcomingAdapter.update(workouts)
        viewBinding.apply {
            viewUpcoming.lblTitle.text = getString(R.string.live_upcoming_title_label)
            viewUpcoming.btnView.isHidden = false
            viewUpcoming.viewMain.isHidden = workouts.isEmpty()
        }
    }

    private fun loadBookWith(workouts: List<VirtualWorkoutModel>) {
        val hide = workouts.isEmpty()
        bookWithAdapter.update(workouts)
        viewBinding.apply {
            viewBookWith.lblTitle.text = getString(R.string.home_book_with_title_label)
            viewBookWith.btnView.isHidden = true
            imgLineBookWith.isHidden = hide
            viewBookWith.viewMain.isHidden = hide
        }
    }

    private fun loadLive(workouts: List<WorkoutModel>) {
        val hide = workouts.isEmpty()
        liveAdapter.update(workouts)
        viewBinding.apply {
            viewLive.lblTitle.text = getString(R.string.home_live_title_label)
            viewLive.btnSchedule.isHidden = true
            imgLineLive.isHidden = hide
            viewLive.viewMain.isHidden = hide
        }
    }

    private fun loadOnDemand(workouts: List<VideoModel>) {
        val hide = workouts.isEmpty()
        onDemandAdapter.update(workouts)
        viewBinding.apply {
            viewOnDemand.lblTitle.text = getString(R.string.home_on_demand_title_label)
            imgLineOnDemand.isHidden = hide
            viewOnDemand.viewMain.isHidden = hide
        }
    }

    private fun loadPromotionsBottom(promotions: List<PromotionModel>) {
        promotionBottomAdapter.update(promotions)
        viewBinding.apply {
            viewForYouBottom.lblTitle.text = getString(R.string.home_promotions_top_title_label)
            viewForYouBottom.viewMain.isHidden = promotions.isEmpty()
        }
    }

    private fun loadUser(model: UserModel) {
        viewBinding.apply {
            with(requireContext()) {
                // Temporary, logic will likely change
                lblUserName.text = getString(R.string.home_user_name_label, model.firstName)
                lblSessions.text = getString(R.string.home_user_sessions_label, model.bookings.size)
                lblClasses.text = getString(
                    R.string.home_user_classes_label,
                    model.freeClasses.size
                )
                lblWorkouts.text = getString(
                    R.string.home_user_workouts_label,
                    model.freeClasses.size
                )

                lblSessions.textColor(
                    if (model.bookings.isEmpty()) R.color.greyLight else R.color.black
                )
                lblClasses.textColor(
                    if (model.freeClasses.isEmpty()) R.color.greyLight else R.color.black
                )
                lblWorkouts.textColor(
                    if (model.freeClasses.isEmpty()) R.color.greyLight else R.color.black
                )
            }
        }
    }
    //endregion
}
