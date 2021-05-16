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
import com.trx.consumer.models.common.PromoModel
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.screens.promotion.PromoAdapter
import com.trx.consumer.screens.videoworkout.VideoAdapter

class HomeFragment : BaseFragment(R.layout.fragment_home) {

    //region Objects
    private val viewModel: HomeViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentHomeBinding::bind)

    private lateinit var videosAdapter: VideoAdapter
    private lateinit var promoAdapter: PromoAdapter

    //endregion

    //region Setup
    override fun bind() {
        videosAdapter = VideoAdapter(viewModel) { lifecycleScope }
        promoAdapter = PromoAdapter(viewModel) { lifecycleScope }

        viewBinding.apply {
            btnTest.action { viewModel.doTapTest() }
            viewBanner.btnPrimary.action { viewModel.doTapBanner() }
            viewVideos.rvVideoWorkouts.adapter = videosAdapter
            viewPromos.rvPromos.adapter = promoAdapter
        }

        viewModel.apply {
            eventTapTest.observe(viewLifecycleOwner, handleTapTest)

            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            eventLoadVideos.observe(viewLifecycleOwner, handleLoadVideos)
            eventLoadPromos.observe(viewLifecycleOwner, handleLoadPromos)
            eventLoadUser.observe(viewLifecycleOwner, handleLoadUser)
            eventLoadBanner.observe(viewLifecycleOwner, handleLoadBanner)

            eventTapBanner.observe(viewLifecycleOwner, handleTapBanner)

            doLoadView()
            doLoadBanner()
            doLoadVideos()
            doLoadPromos()
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

    private val handleLoadVideos = Observer<List<VideoModel>> { workouts ->
        loadVideos(workouts)
    }

    private val handleLoadPromos = Observer<List<PromoModel>> { promotions ->
        loadPromos(promotions)
    }

    private val handleLoadUser = Observer<UserModel> { user ->
        loadUser(user)
    }

    private val handleLoadBanner = Observer<Boolean> { show ->
        viewBinding.apply {
            viewBanner.viewMain.isVisible = show
            viewBanner.viewMain.requestLayout()
        }
    }

    private val handleTapBanner = Observer<Void> {}

    //endregion

    //region Functions

    private fun loadVideos(workouts: List<VideoModel>) {
        val hide = workouts.isEmpty()
        videosAdapter.update(workouts)
        viewBinding.apply {
            viewVideos.lblTitle.text = getString(R.string.home_on_demand_title_label)
            imgLineOnDemand.isHidden = hide
            viewVideos.viewMain.isHidden = hide
        }
    }

    private fun loadPromos(promos: List<PromoModel>) {
        promoAdapter.update(promos)
        viewBinding.apply {
            viewPromos.lblTitle.text = getString(R.string.home_promotions_top_title_label)
            viewPromos.viewMain.isHidden = promos.isEmpty()
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
