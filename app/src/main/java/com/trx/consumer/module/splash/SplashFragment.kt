package com.trx.consumer.module.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.core.BaseFragment
import com.trx.consumer.core.MainActivity
import kotlinx.android.synthetic.main.fragment_splash.*

class SplashFragment : BaseFragment(R.layout.fragment_splash) {

    private val viewModel: SplashViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        initClickListeners()
        initViewModelObservers()
    }

    private fun initViewModel() {
        viewModel.onLoadView()
    }

    private fun initViewModelObservers() {
        viewModel.eventLoadView.observe(viewLifecycleOwner, handleLoadView)
        viewModel.eventTapStart.observe(viewLifecycleOwner, handleTapStart)
    }

    private fun initClickListeners() {
        btnStart.setOnClickListener { viewModel.onTapStart() }
    }

    private val handleLoadView = Observer<Void> {
        tvTitle.text = getString(R.string.splash_title)
        tvSubtitle.text = getString(R.string.splash_subtitle)
        btnStart.text = getString(R.string.splash_button)
    }

    private val handleTapStart = Observer<Void> { }
}
