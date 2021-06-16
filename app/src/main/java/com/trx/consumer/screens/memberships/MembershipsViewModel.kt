package com.trx.consumer.screens.memberships

import com.trx.consumer.base.BaseViewModel
import com.trx.consumer.common.CommonLiveEvent
import com.trx.consumer.models.common.MembershipModel
import com.trx.consumer.screens.memberships.list.MembershipListener
import com.trx.consumer.screens.memberships.list.MembershipViewState

class MembershipsViewModel : BaseViewModel(), MembershipListener {

    var state = MembershipViewState.PLAIN

    val eventTapBack = CommonLiveEvent<Void>()
    val eventLoadView = CommonLiveEvent<List<MembershipModel>>()
    val eventTapChooseMembership = CommonLiveEvent<MembershipModel>()
    val eventTapCancelMembership = CommonLiveEvent<MembershipModel>()

    fun doTapBack() {
        eventTapBack.call()
    }

    fun doTapCancelMembership() {
        eventTapCancelMembership.postValue(MembershipModel.test())
    }

    fun doLoadView() {
        eventLoadView.postValue(MembershipModel.testList(5))
    }

    override fun doTapChooseMembership(membershipModel: MembershipModel) {
        if (membershipModel.primaryState != MembershipViewState.ACTIVE)
            eventTapChooseMembership.postValue(membershipModel)
    }
}
