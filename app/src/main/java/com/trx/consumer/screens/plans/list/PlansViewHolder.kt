package com.trx.consumer.screens.plans.list

import android.view.View
import com.trx.consumer.R
import com.trx.consumer.common.CommonButton
import com.trx.consumer.common.CommonLabel
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.extensions.isHidden
import com.trx.consumer.models.common.PlanModel
import com.trx.consumer.models.common.UserModel

class PlansViewHolder(view: View) : CommonViewHolder(view) {

    private val lblTitle: CommonLabel = view.findViewById(R.id.lblTitle)
    private val lblDescription: CommonLabel = view.findViewById(R.id.lblDescription)
    private val lblCost: CommonLabel = view.findViewById(R.id.lblCost)
    private val btnPlan: CommonButton = view.findViewById(R.id.btnPrimary)

    fun setup(item: PlanModel, listener: PlansListener) {
        lblTitle.text = item.title

        item.cost.let {
            lblCost.apply {
                text = it
                isHidden = it.isEmpty()
            }
        }

        item.description.let {
            lblDescription.apply {
                text = it
                isHidden = it.isEmpty()
            }
        }

        btnPlan.apply {
            text = itemView.context.getString(item.primaryState.buttonText)
            textColor(item.primaryState.buttonTextColor)
            bgColor(item.primaryState.buttonBgColor)
            action { listener.doTapChoosePlan(item) }
            isEnabled = item.title != UserModel.kPlanNamePay
        }
    }
}
