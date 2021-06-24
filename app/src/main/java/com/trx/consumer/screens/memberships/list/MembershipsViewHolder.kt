package com.trx.consumer.screens.memberships.list

import android.view.View
import androidx.core.view.isVisible
import com.trx.consumer.R
import com.trx.consumer.common.CommonButton
import com.trx.consumer.common.CommonLabel
import com.trx.consumer.common.CommonView
import com.trx.consumer.common.CommonViewHolder
import com.trx.consumer.models.common.MembershipModel

class MembershipsViewHolder(view: View) : CommonViewHolder(view) {

    private val lblTitle: CommonLabel = view.findViewById(R.id.lblTitle)
    private val lblPrice: CommonLabel = view.findViewById(R.id.lblPrice)
    private val lblDescription: CommonLabel = view.findViewById(R.id.lblDescription)
    private val btnChoose: CommonButton = view.findViewById(R.id.btnChoose)
    private val viewBottom: CommonView = view.findViewById(R.id.viewBottom)
    private val lblNextBillDate: CommonLabel = view.findViewById(R.id.lblNextBillDate)
    private val lblLastBillDate: CommonLabel = view.findViewById(R.id.lblLastBillDate)
    private val btnCancel: CommonButton = view.findViewById(R.id.btnCancel)

    fun setup(item: MembershipModel, listener: MembershipListener) {
        lblTitle.text = item.customerFacingName
        lblPrice.text = item.cost
        lblDescription.text = item.description
        lblNextBillDate.text = item.nextBillDate
        lblLastBillDate.text = item.lastBillDate

        val state = item.primaryState
        val isActive = state == MembershipViewState.ACTIVE || state == MembershipViewState.BASE
        btnChoose.apply {
            text = context.getString(state.buttonText)
            textColor(state.buttonTextColor)
            bgColor(state.buttonBgColor)
            action { if (!isActive) listener.doTapChoose(item) }
        }
        viewBottom.isVisible = state == MembershipViewState.ACTIVE
        btnCancel.apply {
            text = context.getString(state.cancelButtonText)
            action { if (!item.isCancelled) listener.doTapCancel(item) }
        }
    }
}
