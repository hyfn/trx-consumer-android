package com.trx.consumer.screens.home

import androidx.core.view.isVisible
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
import com.trx.consumer.screens.promotion.PromotionAdapter
import com.trx.consumer.screens.videoworkout.VideoWorkoutAdapter

class HomeFragment : BaseFragment(R.layout.fragment_home) {

    //region Objects
    private val viewModel: HomeViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentHomeBinding::bind)

    private lateinit var promotionTopAdapter: PromotionAdapter
    private lateinit var onDemandAdapter: VideoWorkoutAdapter
    private lateinit var promotionBottomAdapter: PromotionAdapter

    //endregion

    //region Setup
    override fun bind() {
        promotionTopAdapter = PromotionAdapter(viewModel) { lifecycleScope }
        onDemandAdapter = VideoWorkoutAdapter(viewModel) { lifecycleScope }
        promotionBottomAdapter = PromotionAdapter(viewModel) { lifecycleScope }

        viewBinding.apply {
            btnTest.action { viewModel.doTapTest() }
            viewBanner.btnPrimary.action { viewModel.doTapPrimary() }
            viewOnDemand.rvVideoWorkouts.adapter = onDemandAdapter
            viewForYou.rvPromotions.adapter = promotionBottomAdapter
        }

        viewModel.apply {
            eventTapTest.observe(viewLifecycleOwner, handleTapTest)

            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            eventLoadOnDemand.observe(viewLifecycleOwner, handleOnDemand)
            eventLoadPromotionsBottom.observe(viewLifecycleOwner, handleLoadPromotionsBottom)
            eventLoadUser.observe(viewLifecycleOwner, handleLoadUser)
            eventLoadBannerView.observe(viewLifecycleOwner, handleLoadBannerView)

            eventTapPrimary.observe(viewLifecycleOwner, handleTapPrimary)

            doLoadView()
            doLoadBanner()
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
        LogManager.log("handleLoadView")
        viewBinding.apply {
            lblPlan.text = lblPlan.context.getText(R.string.home_dummy_plan_label)
        }
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

    private val handleLoadBannerView = Observer<Boolean> { show ->
        viewBinding.apply {
            viewBanner.viewMain.isVisible = show
            viewBanner.viewMain.requestLayout()
        }
    }

    private val handleTapPrimary = Observer<Void> {}

    //endregion

    //region Functions

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
            viewForYou.lblTitle.text = getString(R.string.home_promotions_top_title_label)
            viewForYou.viewMain.isHidden = promotions.isEmpty()
        }
    }

    private fun loadUser(model: UserModel) {
        viewBinding.apply {
            with(requireContext()) {
                // Temporary, logic will likely change
                lblUserName.text = getString(R.string.home_user_name_label, model.firstName)
            }
        }
    }
    //endregion
}
