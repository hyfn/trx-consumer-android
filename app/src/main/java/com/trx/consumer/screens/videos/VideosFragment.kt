package com.trx.consumer.screens.videos

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentVideosBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.managers.NavigationManager

class VideosFragment : BaseFragment(R.layout.fragment_videos) {

    private val viewModel: VideosViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentVideosBinding::bind)

    override fun bind() {
        viewModel.eventTapBack.observe(viewLifecycleOwner, handleTapBack)

        viewBinding.btnBack.action { viewModel.doTapBack() }
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }

    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }
}
