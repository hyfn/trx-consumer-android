package com.trx.consumer.screens.settings

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentSettingsBinding
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.managers.UtilityManager
import com.trx.consumer.screens.settings.option.SettingsOptionAdapter

class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    private val viewModel: SettingsViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentSettingsBinding::bind)

    private lateinit var adapter: SettingsOptionAdapter

    override fun bind() {
        adapter = SettingsOptionAdapter(viewModel) { lifecycleScope }

        viewBinding.apply {
            rvSettings.adapter = adapter

            btnBack.setOnClickListener { viewModel.doTapBack() }
        }

        viewModel.apply {
            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)

            doLoadView()
        }
    }

    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    private val handleLoadView = Observer<MutableList<Any>> { list ->
        viewBinding.lblVersion.text = UtilityManager.shared.versionDisplay()
        adapter.update(list)
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }
}
