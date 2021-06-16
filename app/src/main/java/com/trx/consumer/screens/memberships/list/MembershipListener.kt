package com.trx.consumer.screens.memberships.list

import com.trx.consumer.models.common.MembershipModel

interface MembershipListener {
    fun doTapChooseMembership(membershipModel: MembershipModel)
}
