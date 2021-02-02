package com.trx.consumer.screens.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_splash.*

class SplashFragment : BaseFragment(R.layout.fragment_splash) {

    //region Objects
    private val viewModel: SplashViewModel by activityViewModels()
    //endregion

    //region Setup
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onLoadView()
        bind()
    }

    private fun bind() {
        viewModel.eventLoadView.observe(viewLifecycleOwner, handleLoadView)
        viewModel.eventTapStart.observe(viewLifecycleOwner, handleTapStart)

        btnStart.setOnClickListener { viewModel.onTapStart() }
    }
    //endregion

    //region Handlers
    private val handleLoadView = Observer<Void> {
        tvTitle.text = getString(R.string.splash_title)
        tvSubtitle.text = getString(R.string.splash_subtitle)
        btnStart.text = getString(R.string.splash_button)
    }

    private val handleTapStart = Observer<Void> { }
    //endregion
}
