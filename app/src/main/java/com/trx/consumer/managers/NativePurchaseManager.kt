package com.trx.consumer.managers

import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class NativePurchaseManager(private val context: Context) {

    // Required for billing client
    private val purchasesUpdatedListener = PurchasesUpdatedListener { _, _ -> }

    private val billingClient by lazy {
        BillingClient
            .newBuilder(context)
            .enablePendingPurchases()
            .setListener(purchasesUpdatedListener)
            .build()
    }

    private suspend fun connect(): Boolean {
        return suspendCoroutine { cont ->
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    cont.resume(billingResult.responseCode == BillingClient.BillingResponseCode.OK)
                }

                override fun onBillingServiceDisconnected() {
                    cont.resume(false)
                }
            })
        }
    }

    suspend fun getPurchases(): List<Purchase> {
        return withContext(Dispatchers.IO) {
            val isConnected = if (billingClient.isReady) true else connect()
            if (isConnected) {
                val purchases = billingClient.queryPurchases(BillingClient.SkuType.SUBS)
                purchases.purchasesList ?: emptyList()
            } else {
                emptyList()
            }
        }
    }
}
