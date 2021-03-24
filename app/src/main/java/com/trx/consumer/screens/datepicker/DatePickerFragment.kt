package com.trx.consumer.screens.datepicker

import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.trx.consumer.R
import com.trx.consumer.base.BaseDialogFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentDatePickerBinding
import com.trx.consumer.extensions.action
import com.trx.consumer.managers.NavigationManager
import java.util.Calendar
import java.util.Date

class DatePickerFragment : BaseDialogFragment(R.layout.fragment_date_picker) {

    //region Objects
    private val viewModel: DatePickerViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentDatePickerBinding::bind)

    //endregion

    //region Setup

    override fun bind() {

        val model = NavigationManager.shared.params(this) as DatePickerModel

        // Set ClickListeners
        viewBinding.btnDone.action { viewModel.doTapDone() }

        // Set ViewModel Observers
        viewModel.apply {
            eventLoadView.observe(viewLifecycleOwner, handleLoadView)
            eventTapBack.observe(viewLifecycleOwner, handleTapBack)
        }

        viewModel.doLoadView(model)
    }

    override fun onDestroyView() {
        viewModel.doOnViewDismissed()
        super.onDestroyView()
    }

    //endregion

    //region Handlers
    private val handleLoadView = Observer<DatePickerModel> { model ->
        viewBinding.apply {
            datePicker.apply {
                model.minimumDate?.let { minDate = it.time }
                model.maximumDate?.let { maxDate = it.time }

                val calendar = Calendar.getInstance()
                calendar.time = model.initialDate ?: Date()
                init(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH),
                    viewModel
                )
            }
            viewContent.startInAnimation(bottomInAnimation(requireContext()))
        }
    }

    private val handleTapBack = Observer<Void> {
        dismiss()
    }

    override fun dismiss() {
        viewBinding.viewContent.startOutAnimation(bottomOutAnimation(requireContext())) {
            super.dismiss()
        }
    }

    //endregion
}
