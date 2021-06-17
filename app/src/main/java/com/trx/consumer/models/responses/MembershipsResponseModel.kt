package com.trx.consumer.models.responses

import com.trx.consumer.extensions.map
import com.trx.consumer.models.common.MembershipModel
import com.trx.consumer.models.common.UserPlanModel
import com.trx.consumer.screens.memberships.list.MembershipViewState
import org.json.JSONObject

class MembershipsResponseModel(val memberships: List<MembershipModel>) {

    companion object {

        fun parse(json: String): MembershipsResponseModel {
            val jsonObject = JSONObject(json)
            val dataObject = jsonObject.getJSONObject("data")

            val baseMembershipsRaw = dataObject.getJSONArray("baseValues").map {
                MembershipModel.parse(it)
            }

            val customMembershipsRaw = mutableListOf<MembershipModel>()
            val customValuesObject = dataObject.getJSONObject("customValues")
            customValuesObject.keys().forEach { key ->
                val value = customValuesObject.getJSONObject(key)
                val model = MembershipModel.parse(value).apply {
                    this.key = key
                    this.primaryState = MembershipViewState.CUSTOM
                }
                customMembershipsRaw.add(model)
            }

            val baseMemberships = baseMembershipsRaw.filter { it.userType == "customer" }
            val customMemberships = customMembershipsRaw.filter {
                it.userType == "customer" && it.revcatProductId.isNotEmpty()
            }
            return MembershipsResponseModel(baseMemberships + customMemberships)
        }
    }

    fun sections(plans: HashMap<String, UserPlanModel>): List<Any> {
        val sections = mutableListOf<Any>()
        val baseMemberships = memberships.filter { it.primaryState == MembershipViewState.BASE }
        val customMemberships = memberships.filter {
            !it.hideWhenNotSubscribed && it.primaryState == MembershipViewState.CUSTOM
        }
        val legacyMemberships = memberships.filter { it.hideWhenNotSubscribed }

        match(customMemberships, plans)
        if (baseMemberships.isNotEmpty() || customMemberships.isNotEmpty()) {
            sections.add("MEMBERSHIPS")

            if (customMemberships.none { it.primaryState == MembershipViewState.ACTIVE }) {
                sections.addAll(baseMemberships)
                sections.addAll(customMemberships.sortedBy { it.key })
            } else {
                sections.addAll(
                    customMemberships.sortedWith(
                        compareBy({ it.primaryState != MembershipViewState.ACTIVE }, { it.key })
                    )
                )
            }
        }

        match(legacyMemberships, plans)
        val activeLegacyMemberships = legacyMemberships.filter {
            it.primaryState == MembershipViewState.ACTIVE
        }
        if (activeLegacyMemberships.isNotEmpty()) {
            sections.add("LEGACY IN-APP SUBSCRIPTIONS")
            sections.addAll(activeLegacyMemberships)
        }

        return sections
    }

    private fun match(memberships: List<MembershipModel>, plans: HashMap<String, UserPlanModel>) {
        memberships.forEach { membership ->
            plans[membership.key]?.let { matchingPlan ->
                membership.primaryState = MembershipViewState.ACTIVE
                membership.currentPeriodEnd = matchingPlan.currentPeriodEnd
                membership.currentPeriodStart = matchingPlan.currentPeriodStart
            }
        }
    }

}
