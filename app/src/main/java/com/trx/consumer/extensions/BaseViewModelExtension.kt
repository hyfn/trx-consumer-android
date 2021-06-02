package com.trx.consumer.extensions

import com.trx.consumer.base.BaseViewModel

inline val BaseViewModel.pageTitle: String
    get() = this.javaClass.simpleName.replace("ViewModel", "")
