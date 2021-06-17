package com.trx.consumer.screens.testutility

import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentTestUtilityBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.AlertModel
import com.trx.consumer.models.common.BookingAlertModel
import com.trx.consumer.models.common.FilterModel
import com.trx.consumer.models.common.PromoModel
import com.trx.consumer.models.common.TrainerModel
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.models.params.ContentParamsModel
import com.trx.consumer.models.params.FilterParamsModel
import com.trx.consumer.screens.alert.AlertViewState
import com.trx.consumer.screens.content.ContentViewState
import com.trx.consumer.screens.groupplayer.GroupPlayerActivity
import com.trx.consumer.screens.liveworkout.LiveWorkoutAdapter
import com.trx.consumer.screens.loading.LoadingViewState
import com.trx.consumer.screens.privateplayer.PrivatePlayerActivity
import com.trx.consumer.screens.promotion.PromoAdapter
import com.trx.consumer.screens.video.VideoActivity
import com.trx.consumer.screens.videoworkout.VideoWorkoutAdapter
import com.trx.consumer.screens.virtualworkout.VirtualWorkoutAdapter
import com.trx.consumer.screens.welcome.WelcomeState

class TestUtilityFragment : BaseFragment(R.layout.fragment_test_utility) {

    //region Objects
    private val viewModel: TestUtilityViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentTestUtilityBinding::bind)

    private lateinit var liveWorkoutAdapter: LiveWorkoutAdapter
    private lateinit var virtualWorkoutAdapter: VirtualWorkoutAdapter
    private lateinit var videoAdapter: VideoWorkoutAdapter
    private lateinit var promoAdapter: PromoAdapter

    //endregion

    //region Setup
    override fun bind() {
        liveWorkoutAdapter = LiveWorkoutAdapter(viewModel) { lifecycleScope }
        virtualWorkoutAdapter = VirtualWorkoutAdapter(viewModel) { lifecycleScope }
        videoAdapter = VideoWorkoutAdapter(viewModel) { lifecycleScope }
        promoAdapter = PromoAdapter(viewModel) { lifecycleScope }

        viewBinding.apply {
            btnBack.action { viewModel.doTapBack() }
            btnCards.action { viewModel.doTapCards() }
            btnAddCard.action { viewModel.doTapAddCard() }
            btnUpdate.action { viewModel.doTapUpdate() }
            btnContent.action { viewModel.doTapContent() }
            btnPlans.action { viewModel.doTapPlans() }
            btnVideo.action { viewModel.doTapVideo() }
            btnFilter.action { viewModel.doTapFilter() }
            btnDiscover.action { viewModel.doTapDiscover() }
            btnAlert.action { viewModel.doTapAlert() }
            btnWelcome.action { viewModel.doTapWelcome() }
            btnSettings.action { viewModel.doTapSettings() }
            btnWorkout.action { viewModel.doTapWorkout() }
            btnTrainer.action { viewModel.doTapTrainer() }
            btnLoadingScreen.action { viewModel.doTapLoadingScreen() }
            btnBookingAlert.action { viewModel.doTapBookingAlert() }
            btnSchedule.action { viewModel.doTapSchedule() }
            btnMemberships.action { viewModel.doTapMemberships() }
            btnGroupPlayer.action { viewModel.doTapGroupPlayer() }
            btnPrivatePlayer.action { viewModel.doTapPrivatePlayer() }
            rvLiveWorkouts.adapter = liveWorkoutAdapter
            rvVirtualWorkouts.adapter = virtualWorkoutAdapter
            rvVideoWorkouts.adapter = videoAdapter
            rvPromos.adapter = promoAdapter
        }

        viewModel.apply {
            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
            eventTapCards.observe(viewLifecycleOwner, handleTapCards)
            eventTapAddCard.observe(viewLifecycleOwner, handleTapAddCard)
            eventTapUpdate.observe(viewLifecycleOwner, handleTapUpdate)
            eventTapContent.observe(viewLifecycleOwner, handleTapContent)
            eventTapFilter.observe(viewLifecycleOwner, handleTapFilter)
            eventLoadLiveWorkouts.observe(viewLifecycleOwner, handleLoadLiveWorkouts)
            eventTapPlans.observe(viewLifecycleOwner, handleTapPlans)
            eventTapVideo.observe(viewLifecycleOwner, handleTapVideo)
            eventTapDiscover.observe(viewLifecycleOwner, handleTapDiscover)
            eventTapAlert.observe(viewLifecycleOwner, handleTapAlert)
            eventTapWelcome.observe(viewLifecycleOwner, handleTapWelcome)
            eventTapSettings.observe(viewLifecycleOwner, handleTapSettings)
            eventTapWorkout.observe(viewLifecycleOwner, handleTapWorkout)
            eventTapTrainer.observe(viewLifecycleOwner, handleTapTrainer)
            eventTapBookingAlert.observe(viewLifecycleOwner, handleTapBookingAlert)
            eventTapSchedule.observe(viewLifecycleOwner, handleTapSchedule)
            eventLoadVirtualWorkouts.observe(viewLifecycleOwner, handleLoadVirtualWorkouts)
            eventLoadVideoWorkouts.observe(viewLifecycleOwner, handleLoadVideoWorkouts)
            eventLoadPromotions.observe(viewLifecycleOwner, handleLoadPromotions)
            eventLoadingScreen.observe(viewLifecycleOwner, handleTapLoadingScreen)
            eventTapMemberships.observe(viewLifecycleOwner, handleTapMemberships)
            eventLoadGroupPlayer.observe(viewLifecycleOwner, handleGroupPlayer)
            eventPrivatePlayer.observe(viewLifecycleOwner, handlePrivatePlayer)
            doLoadView()
        }
    }

    //endregion

    //region Handlers
    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    private val handleTapCards = Observer<Void> {
        NavigationManager.shared.present(this, R.id.cards_fragment)
    }

    private val handleTapAddCard = Observer<Void> {
        NavigationManager.shared.present(this, R.id.add_card_fragment)
    }

    private val handleTapUpdate = Observer<Void> {
        NavigationManager.shared.present(this, R.id.update_fragment)
    }

    private val handleTapContent = Observer<Void> {
        val model = ContentParamsModel(ContentViewState.PLAIN, "Terms & Conditions", "Test")
        NavigationManager.shared.present(this, R.id.content_fragment, model)
    }

    private val handleTapPlans = Observer<Void> {
        NavigationManager.shared.present(this, R.id.plans_fragment)
    }

    private val handleTapVideo = Observer<Void> {
        val video = VideoModel.test().apply { id = "6232799349001" }
        NavigationManager.shared.presentActivity(
            requireActivity(),
            VideoActivity::class.java,
            video
        )
    }

    private val handleTapDiscover = Observer<Void> {
        NavigationManager.shared.present(this, R.id.discover_fragment)
    }

    private val handleTapAlert = Observer<Void> {
        val model = AlertModel.create(title = "This is a title", message = "This is a message")
        model.setPrimaryButton(
            title = R.string.test_utility_alert_button_primary_label,
            state = AlertViewState.POSITIVE
        ) {
            Toast.makeText(requireContext(), "Did something", Toast.LENGTH_LONG).show()
        }
        model.setSecondaryButton(
            title = R.string.test_utility_alert_button_secondary_label,
            state = AlertViewState.NEGATIVE
        )
        NavigationManager.shared.present(this, R.id.alert_fragment, params = model)
    }

    private val handleTapFilter = Observer<Void> {
        val model = FilterParamsModel(lstFilters = FilterModel.testList(10))
        NavigationManager.shared.present(this, R.id.filter_fragment, model)
    }

    private val handleTapWelcome = Observer<Void> {
        NavigationManager.shared.present(this, R.id.welcome_fragment, WelcomeState.WELCOME)
    }

    private val handleTapSettings = Observer<Void> {
        NavigationManager.shared.present(this, R.id.settings_fragment)
    }

    private val handleTapWorkout = Observer<Void> {
        NavigationManager.shared.present(this, R.id.workout_fragment)
    }

    private val handleTapMemberships = Observer<Void> {
        NavigationManager.shared.present(this, R.id.memberships_fragment)
    }

    private val handleTapTrainer = Observer<Void> {
        NavigationManager.shared.present(this, R.id.trainer_fragment, TrainerModel.test())
    }

    private val handleTapBookingAlert = Observer<Void> {
        //  Uncomment other options for testing
        //  Live Booking - No/Yes Cancel Buttons
        val model = BookingAlertModel.testLiveBook()
        //  Live Booking - Card Info / Enabled Book Now w/ Price
        // val model = BookingAlertModel.testCardLiveBook()
        //  Live Booking - Add Card / Disabled Book Now w/ Price
        // val model = BookingAlertModel.testNoCardLiveBook()
        //  Virtual Booking - No/Yes Cancel Buttons
        // val model = BookingAlertModel.testVirtualBook()
        //  Virtual Booking - Card Info / Enabled Book Now w/ Price
        // val model = BookingAlertModel.testCardVirtualBook()
        //  Virtual Booking - Add Card / Disabled Book Now w/ Price
        // val model = BookingAlertModel.testNoCardVirtualBook()

        NavigationManager.shared.present(this, R.id.booking_alert_fragment, params = model)
    }

    private val handleTapSchedule = Observer<Void> {
        NavigationManager.shared.present(this, R.id.schedule_fragment)
    }

    private val handleLoadLiveWorkouts = Observer<List<WorkoutModel>> { liveWorkouts ->
        liveWorkoutAdapter.update(liveWorkouts)
    }

    private val handleLoadVirtualWorkouts = Observer<List<WorkoutModel>> { virtualWorkouts ->
        virtualWorkoutAdapter.update(virtualWorkouts)
    }

    private val handleLoadVideoWorkouts = Observer<List<VideoModel>> { videoWorkouts ->
        videoAdapter.update(videoWorkouts)
    }

    private val handleLoadPromotions = Observer<List<PromoModel>> { promotions ->
        promoAdapter.update(promotions)
    }

    private val handleTapLoadingScreen = Observer<Void> {
        NavigationManager.shared.present(this, R.id.loading_screen, LoadingViewState.LAUNCH)
    }


    private val handleGroupPlayer = Observer<Void> {
        NavigationManager.shared.presentActivity(requireActivity(), GroupPlayerActivity::class.java)
    }

    private val handlePrivatePlayer = Observer<Void> {
        NavigationManager.shared.presentActivity(requireActivity(), PrivatePlayerActivity::class.java)
    }

    //endregion

    //region Functions
    override fun onBackPressed() {
        viewModel.doTapBack()
    }

    //endregion
}
