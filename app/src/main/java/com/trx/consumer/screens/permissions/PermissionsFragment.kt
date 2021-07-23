package com.trx.consumer.screens.permissions

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentPermissionsBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.managers.LogManager
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.common.PermissionModel

class PermissionsFragment : BaseFragment(R.layout.fragment_permissions) {

    private val viewModel: PermissionsViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentPermissionsBinding::bind)

    private lateinit var rvPermissionsAdapter: PermissionsAdapter

    override fun bind() {
        rvPermissionsAdapter = PermissionsAdapter(viewModel) { lifecycleScope }

        viewBinding.apply {
            lblTitle.text = getString(R.string.settings_permissions)
            rvPermissions.adapter = rvPermissionsAdapter
            btnBack.action { viewModel.doTapBack() }
        }
        viewModel.apply {
            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
        }

        viewModel.doLoadView()
    }

    private val handleTapBack = Observer<Void> {
        LogManager.log("handleTapBack")
        NavigationManager.shared.dismiss(this)
    }

    private val handleLoadView = Observer<List<PermissionModel>> { permissionList ->
        LogManager.log("handlePermissionList")
        rvPermissionsAdapter.update(permissionList)
    }

    override fun onBackPressed() {
        NavigationManager.shared.dismiss(this)
    }
}
