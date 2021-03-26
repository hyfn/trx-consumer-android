package com.trx.consumer.base

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment {

    private val name = this.javaClass.simpleName

    constructor() : super()

    constructor(layoutResId: Int) : super(layoutResId)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            onBackPressed()
        }
    }

    final override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind()
    }

    open fun bind() {}

    override fun onResume() {
        super.onResume()
        print("fragmentName: $name")
    }

    open fun onBackPressed() {}
}
