package com.trx.consumer.screens.promotion

import com.trx.consumer.models.common.PromotionModel

interface PromotionListener {

    fun doTapPromotion(model: PromotionModel)
}
