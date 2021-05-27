package com.trx.consumer.screens.schedule

import androidx.fragment.app.viewModels
import com.trx.consumer.R
import com.trx.consumer.base.BaseFragment
import com.trx.consumer.base.viewBinding
import com.trx.consumer.databinding.FragmentScheduleBinding

class ScheduleFragment : BaseFragment(R.layout.fragment_schedule) {

    private val viewModel: ScheduleViewModel by viewModels()
    private val viewBinding by viewBinding(FragmentScheduleBinding::bind)
}