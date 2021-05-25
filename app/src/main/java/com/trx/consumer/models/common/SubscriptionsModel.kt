package com.trx.consumer.models.common

import android.os.Parcelable
import com.trx.consumer.screens.subscriptions.list.SubscriptionsViewState
import kotlinx.parcelize.Parcelize

@Parcelize
class SubscriptionsModel(
    var state: SubscriptionsViewState = SubscriptionsViewState.CURRENT,
    var subscriptions: List<SubscriptionModel> = SubscriptionModel.testList(10),
    var current: List<SubscriptionModel> = SubscriptionModel.testList(1),
) : Parcelable
