package com.trx.consumer.screens.home

import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.trx.consumer.BuildConfig
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentHomeBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.extensions.isHidden
import com.trx.consumer.extensions.load
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.managers.UtilityManager
import com.trx.consumer.models.common.BannerModel
import com.trx.consumer.models.common.PromoModel
import com.trx.consumer.models.common.UserModel
import com.trx.consumer.models.common.VideoModel
import com.trx.consumer.screens.promotion.PromoAdapter
import com.trx.consumer.screens.videoworkout.VideoWorkoutAdapter

class HomeFragment : BaseFragment(R.layout.fragment_home) {

    //region Objects
    private val viewModel: HomeViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentHomeBinding::bind)

    private lateinit var videosAdapter: VideoWorkoutAdapter
    private lateinit var promoAdapter: PromoAdapter

    //endregion

    //region Setup
    override fun bind() {
        videosAdapter = VideoWorkoutAdapter(viewModel) { lifecycleScope }
        promoAdapter = PromoAdapter(viewModel) { lifecycleScope }

        viewBinding.apply {
            viewUserInfo.action { viewModel.doShowEditProfile() }
            viewBanner.btnPrimary.action { viewModel.doTapBanner() }
            viewVideos.rvVideoWorkouts.adapter = videosAdapter
            viewPromos.rvPromos.adapter = promoAdapter
        }

        viewModel.apply {

            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            eventLoadVideos.observe(viewLifecycleOwner, handleLoadVideos)
            eventLoadPromos.observe(viewLifecycleOwner, handleLoadPromos)
            eventLoadUser.observe(viewLifecycleOwner, handleLoadUser)
            eventLoadBanner.observe(viewLifecycleOwner, handleLoadBanner)

            eventShowVideo.observe(viewLifecycleOwner, handleShowVideo)
            eventShowPromo.observe(viewLifecycleOwner, handleShowPromo)
            eventShowHud.observe(viewLifecycleOwner, handleShowHud)
            eventShowEditProfile.observe(viewLifecycleOwner, handleShowEditProfile)

            eventTapBanner.observe(viewLifecycleOwner, handleTapBanner)

            doTrackPageView()
            doLoadView()
            doLoadBanner()
            doLoadVideos()
            doLoadPromos()
        }
    }
    //endregion

    //region Handlers

    private val handleLoadView = Observer<Void> {
        LogManager.log("handleLoadView")
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

    private val handleLoadBanner = Observer<BannerModel> { banner ->
        LogManager.log("handleLoadBanner: isActive - ${banner.isActive}")
        loadBanner(banner)
    }

    private val handleShowVideo = Observer<VideoModel> { model ->
        LogManager.log("handleShowVideo")
        NavigationManager.shared.present(this, R.id.workout_fragment, model)
    }

    private val handleShowPromo = Observer<PromoModel> { promo ->
        LogManager.log("handleShowPromo: ${promo.ctaHref}")
        promo.ctaHref.let { url ->
            if (url.isNotEmpty()) UtilityManager.shared.openUrl(requireContext(), url)
        }
    }

    private val handleTapBanner = Observer<String> { url ->
        LogManager.log("handleTapBanner: $url")
        if (url.isNotEmpty()) UtilityManager.shared.openUrl(requireContext(), url)
    }

    private val handleShowHud = Observer<Boolean> { show ->
        viewBinding.hudView.isVisible = show
    }

    private val handleShowEditProfile = Observer<Void> {
        if (BuildConfig.isVersion2Enabled) {
            NavigationManager.shared.present(this, R.id.profile_fragment)
        }
    }
    //endregion

    //region Functions

    private fun loadBanner(banner: BannerModel) {
        viewBinding.apply {
            viewBanner.lblBanner.text = getString(R.string.home_banner_section_title_label)
            viewBanner.imgBanner.load(banner.modalImageUrl)
            viewBanner.lblTitle.text = banner.modalTitle
            viewBanner.lblSubtitle.text = banner.modalDescription
            viewBanner.btnPrimary.text = banner.modalButtonText
            viewBanner.viewMain.isHidden = !banner.isActive
            viewBanner.viewMain.requestLayout()
        }
    }

    private fun loadVideos(workouts: List<VideoModel>) {
        val hide = workouts.isEmpty()
        videosAdapter.update(workouts)
        viewBinding.apply {
            viewVideos.lblTitle.text = getString(R.string.home_on_demand_title_label)
            viewVideos.viewMain.isHidden = hide
        }
    }

    private fun loadPromos(promos: List<PromoModel>) {
        promos.isEmpty().let { isEmpty ->
            if (!isEmpty) promoAdapter.update(promos)
            viewBinding.apply {
                imgLineOnDemand.isGone = isEmpty
                viewPromos.viewMain.isGone = isEmpty
                viewPromos.lblTitle.text = getString(R.string.promos_top_title_label)
            }
        }
    }

    private fun loadUser(model: UserModel) {
        viewBinding.apply {
            with(requireContext()) {
                lblUserName.text = getString(R.string.home_user_name_label, model.firstName)
            }
        }
    }
    //endregion
}
