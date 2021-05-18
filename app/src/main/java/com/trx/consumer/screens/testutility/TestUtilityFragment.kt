package com.trx.consumer.screens.testutility

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentTestUtilityBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.FilterModel
import com.trx.consumer.models.common.PromoModel
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.models.common.VirtualWorkoutModel
import com.trx.consumer.models.common.WorkoutModel
import com.trx.consumer.models.params.ContentParamsModel
import com.trx.consumer.models.params.FilterParamsModel
import com.trx.consumer.screens.content.ContentViewState
import com.trx.consumer.screens.liveworkout.LiveWorkoutAdapter
import com.trx.consumer.screens.promotion.PromoAdapter
import com.trx.consumer.screens.update.UpdateViewState
import com.trx.consumer.screens.videoworkout.VideoAdapter
import com.trx.consumer.screens.virtualworkout.VirtualWorkoutAdapter

class TestUtilityFragment : BaseFragment(R.layout.fragment_test_utility) {

    //region Objects
    private val viewModel: TestUtilityViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentTestUtilityBinding::bind)

    private lateinit var liveWorkoutAdapter: LiveWorkoutAdapter
    private lateinit var virtualWorkoutAdapter: VirtualWorkoutAdapter
    private lateinit var videoAdapter: VideoAdapter
    private lateinit var promoAdapter: PromoAdapter

    //endregion

    //region Setup
    override fun bind() {
        liveWorkoutAdapter = LiveWorkoutAdapter(viewModel) { lifecycleScope }
        virtualWorkoutAdapter = VirtualWorkoutAdapter(viewModel) { lifecycleScope }
        videoAdapter = VideoAdapter(viewModel) { lifecycleScope }
        promoAdapter = PromoAdapter(viewModel) { lifecycleScope }

        viewBinding.apply {
            btnBack.action { viewModel.doTapBack() }
            btnCards.action { viewModel.doTapCards() }
            btnAddCard.action { viewModel.doTapAddCard() }
            btnUpdate.action { viewModel.doTapUpdate() }
            btnContent.action { viewModel.doTapContent() }
            btnPlans.action { viewModel.doTapPlans() }
            btnFilter.action { viewModel.doTapFilter() }
            btnDiscover.action { viewModel.doTapDiscover() }
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
            eventTapDiscover.observe(viewLifecycleOwner, handleTapDiscover)
            eventLoadVirtualWorkouts.observe(viewLifecycleOwner, handleLoadVirtualWorkouts)
            eventLoadVideoWorkouts.observe(viewLifecycleOwner, handleLoadVideoWorkouts)
            eventLoadPromotions.observe(viewLifecycleOwner, handleLoadPromotions)

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
        NavigationManager.shared.present(this, R.id.update_fragment, UpdateViewState.EDIT)
    }

    private val handleTapContent = Observer<Void> {
        val model = ContentParamsModel(ContentViewState.PLAIN, "Terms & Conditions", "Test")
        NavigationManager.shared.present(
            this,
            R.id.content_fragment,
            model
        )
    }

    private val handleTapPlans = Observer<Void> {
        NavigationManager.shared.present(this, R.id.plans_fragment)
    }

    private val handleTapDiscover = Observer<Void> {
        NavigationManager.shared.present(this, R.id.discover_fragment)
    }

    private val handleTapFilter = Observer<Void> {
        val model = FilterParamsModel(FilterModel.test(10))
        NavigationManager.shared.present(this, R.id.filter_fragment, model)
    }

    private val handleLoadLiveWorkouts = Observer<List<WorkoutModel>> { liveWorkouts ->
        liveWorkoutAdapter.update(liveWorkouts)
    }

    private val handleLoadVirtualWorkouts = Observer<List<VirtualWorkoutModel>> { virtualWorkouts ->
        virtualWorkoutAdapter.update(virtualWorkouts)
    }

    private val handleLoadVideoWorkouts = Observer<List<VideoModel>> { videoWorkouts ->
        videoAdapter.update(videoWorkouts)
    }

    private val handleLoadPromotions = Observer<List<PromoModel>> { promotions ->
        promoAdapter.update(promotions)
    }

    //endregion

    //region Functions
    override fun onBackPressed() {
        viewModel.doTapBack()
    }

    //endregion
}
