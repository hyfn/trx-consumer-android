package com.trx.consumer.managers

import android.app.Activity
import com.android.billingclient.api.Purchase
import com.revenuecat.purchases.Offerings
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PurchaserInfo
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.interfaces.MakePurchaseListener
import com.revenuecat.purchases.interfaces.ReceiveOfferingsListener
import com.revenuecat.purchases.interfaces.ReceivePurchaserInfoListener
import com.trx.consumer.models.common.IAPModel
import com.trx.consumer.models.common.IAPOfferingsModel
import com.trx.consumer.models.common.SubscriptionModel
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class IAPManager {

    suspend fun identify(appUserId: String): IAPModel {
        return suspendCoroutine { cont ->
            val callback = object : ReceivePurchaserInfoListener {
                override fun onReceived(purchaserInfo: PurchaserInfo) {
                    cont.resume(IAPModel(purchaserInfo = purchaserInfo))
                }

                override fun onError(error: PurchasesError) {
                    cont.resume(IAPModel(error = error))
                }
            }
            Purchases.sharedInstance.identify(appUserId, callback)
        }
    }

    suspend fun offerings(): IAPOfferingsModel {
        return suspendCoroutine { cont ->
            val callback = object : ReceiveOfferingsListener {
                override fun onReceived(offerings: Offerings) {
                    cont.resume(IAPOfferingsModel(offerings))
                }

                override fun onError(error: PurchasesError) {
                    cont.resume(IAPOfferingsModel(error = error))
                }
            }
            Purchases.sharedInstance.getOfferings(callback)
        }
    }

    suspend fun packages(): List<SubscriptionModel> {
        return offerings().lstPackages
    }

    suspend fun purchase(activity: Activity, model: Package): IAPModel {
        return suspendCoroutine { cont ->
            val callback = object : MakePurchaseListener {
                override fun onCompleted(purchase: Purchase, purchaserInfo: PurchaserInfo) {
                    cont.resume(IAPModel(purchaserInfo = purchaserInfo, purchase = purchase))
                }

                override fun onError(error: PurchasesError, userCancelled: Boolean) {
                    cont.resume(IAPModel(error = error, userCancelled = userCancelled))
                }
            }
            Purchases.sharedInstance.purchasePackage(activity, model, callback)
        }
    }

    suspend fun purchases(): IAPModel {
        return suspendCoroutine { cont ->
            val callback = object : ReceivePurchaserInfoListener {
                override fun onReceived(purchaserInfo: PurchaserInfo) {
                    cont.resume(IAPModel(purchaserInfo = purchaserInfo))
                }

                override fun onError(error: PurchasesError) {
                    cont.resume(IAPModel(error = error))
                }
            }
            Purchases.sharedInstance.getPurchaserInfo(callback)
        }
    }

    suspend fun restore(): IAPModel {
        return suspendCoroutine { cont ->
            val callback = object : ReceivePurchaserInfoListener {
                override fun onReceived(purchaserInfo: PurchaserInfo) {
                    cont.resume(IAPModel(purchaserInfo = purchaserInfo))
                }

                override fun onError(error: PurchasesError) {
                    cont.resume(IAPModel(error = error))
                }
            }
            Purchases.sharedInstance.restorePurchases(callback)
        }
    }

    companion object {

        val shared: IAPManager = getInstance()

        @Volatile
        private var instance: IAPManager? = null

        private fun init(): IAPManager = instance ?: synchronized(this) {
            instance ?: IAPManager().also {
                instance = it
            }
        }

        private fun getInstance(): IAPManager = instance ?: init()
    }
}
