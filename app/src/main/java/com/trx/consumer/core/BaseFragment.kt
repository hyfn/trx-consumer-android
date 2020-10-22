package com.trx.consumer.core

import androidx.fragment.app.Fragment

open class BaseFragment : Fragment {

    private val name = this.javaClass.simpleName

    constructor() : super()

    constructor(layoutResId: Int) : super(layoutResId)

    override fun onResume() {
        super.onResume()
        print("fragmentName: $name")
    }
}
