package com.trx.consumer.models.responses

import com.trx.consumer.BuildConfig
import com.trx.consumer.extensions.map
import com.trx.consumer.models.common.MembershipModel
import com.trx.consumer.models.common.UserMembershipModel
import com.trx.consumer.screens.memberships.list.MembershipViewState
import org.json.JSONObject

class MembershipsResponseModel(
    val baseMemberships: List<MembershipModel>,
    val customMemberships: List<MembershipModel>
) {

    companion object {

        fun parse(json: String): MembershipsResponseModel {
            val jsonObject = JSONObject(json)
            val dataObject = jsonObject.getJSONObject("data")

            val baseMemberships = dataObject.getJSONArray("baseValues").map {
                MembershipModel.parse(it)
            }.filter { it.userType == "customer" }

            val customMemberships = mutableListOf<MembershipModel>()
            val customValuesObject = dataObject.getJSONObject("customValues")
            customValuesObject.keys().forEach { key ->
                val value = customValuesObject.getJSONObject(key)
                val model = MembershipModel.parse(value).apply {
                    this.key = key
                    this.primaryState = MembershipViewState.CUSTOM
                }
                customMemberships.add(model)
            }

            return MembershipsResponseModel(
                baseMemberships = baseMemberships,
                customMemberships = customMemberships.filter { it.userType == "customer" }
            )
        }
    }

    fun memberships(userMemberships: Map<String, UserMembershipModel>): List<MembershipModel> {
        val memberships = mutableListOf<MembershipModel>()

        match(customMemberships, userMemberships)
        val filteredMemberships = customMemberships.filter {
            it.isActive || it.isCancelled || it.showInMobile
        }
        if (filteredMemberships.none { it.isActive || it.isCancelled }) {
            if (BuildConfig.isVersion2Enabled) memberships.addAll(baseMemberships)
            memberships.addAll(filteredMemberships.sortedBy { it.key })
        } else {
            val comparator = compareBy<MembershipModel> { !it.isActive }
                .thenBy { !it.isCancelled }
                .thenBy { it.key }
            val sortedMemberships = filteredMemberships.sortedWith(comparator)
            memberships.addAll(sortedMemberships)
        }

        return memberships
    }

    private fun match(
        memberships: List<MembershipModel>,
        userMemberships: Map<String, UserMembershipModel>
    ) {
        memberships.forEach { membership ->
            userMemberships[membership.key]?.let { matchingPlan ->
                membership.primaryState = if (matchingPlan.cancelAtPeriodEnd) {
                    MembershipViewState.CANCELLED
                } else {
                    MembershipViewState.ACTIVE
                }
                membership.currentPeriodEnd = matchingPlan.currentPeriodEnd
                membership.currentPeriodStart = matchingPlan.currentPeriodStart
            }
        }
    }
}
