package com.trx.consumer.screens.memberships.list

import com.trx.consumer.models.common.MembershipModel

interface MembershipListener {

    fun doTapChoose(model: MembershipModel)

    fun doTapCancel(model: MembershipModel)
}
