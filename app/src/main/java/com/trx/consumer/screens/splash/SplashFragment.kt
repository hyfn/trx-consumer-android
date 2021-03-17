package com.trx.consumer.screens.splash

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment

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

        requireView().findViewById<Button>(R.id.btnStart).setOnClickListener {
            viewModel.onTapStart()
        }
    }
    //endregion

    //region Handlers
    private val handleLoadView = Observer<Void> {
        requireView().apply {
            findViewById<TextView>(R.id.tvTitle).text = getString(R.string.splash_title)
            findViewById<TextView>(R.id.tvSubtitle).text = getString(R.string.splash_subtitle)
            findViewById<Button>(R.id.btnStart).text = getString(R.string.splash_button)
        }
    }

    private val handleTapStart = Observer<Void> { }
    //endregion
}
