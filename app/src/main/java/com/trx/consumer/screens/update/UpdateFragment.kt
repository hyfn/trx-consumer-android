package com.trx.consumer.screens.update

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentUpdateBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.managers.NavigationManager
import com.trx.consumer.models.params.UpdateParamsModel

class UpdateFragment : BaseFragment(R.layout.fragment_update) {

    private val viewModel: UpdateViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentUpdateBinding::bind)

    override fun bind() {
        val model = NavigationManager.shared.params(this) as UpdateParamsModel

        viewModel.apply {
            state = model.state

            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            eventUpdateDate.observe(viewLifecycleOwner, handleUpdateDate)

            doLoadView()
        }

        viewBinding.apply {
            ivBirthDate.showDatePicker(this@UpdateFragment)
            ivBirthDate.setInputViewListener(viewModel)
            btnBack.action { viewModel.doTapBack() }
        }
    }

    override fun onBackPressed() {
        viewModel.doTapBack()
    }

    private val handleTapBack = Observer<Void> {
        NavigationManager.shared.dismiss(this)
    }

    private val handleLoadView = Observer<UpdateViewState> {
        viewBinding.apply {
            btnSave.text = getString(it.buttonTitle)
            if (it == UpdateViewState.CREATE) {
                lblAgreement.isVisible = true
                cbAgreement.isVisible = true
            }
        }
    }

    private val handleUpdateDate = Observer<String> {
        viewBinding.ivBirthDate.text = it
    }
}
