package and.drew.nkhukumanagement.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesResponseListener
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.queryPurchasesAsync
import com.google.common.collect.ImmutableList

object BillingState{
    var isSubscribed = false
}

class BillingManager (private val context: Context) : PurchasesUpdatedListener {
    private val pendingPurchasesParams = PendingPurchasesParams.newBuilder()
        .enablePrepaidPlans()
        .enableOneTimeProducts()
        .build()
    private  val billingClient: BillingClient = BillingClient.newBuilder(context)
        .enablePendingPurchases(pendingPurchasesParams)
        .setListener(this)
        .build()

    fun startConnection(onConnected: () -> Unit) {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                startConnection(onConnected)
            }

            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryActiveSubscriptions()
                    onConnected()
                }
            }
        })
    }

    fun launchSubscriptionFlow(activity: Activity, productDetails: ProductDetails, offerToken: String) {
        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .setOfferToken(offerToken)
                        .build()
                )
            )
            .build()

        billingClient.launchBillingFlow(activity, billingFlowParams)
    }

    fun queryProductDetails(productID: String, onResult: (ProductDetails?) -> Unit) {
        val queryProductDetailsParams = QueryProductDetailsParams.newBuilder()
            .setProductList(
                listOf(
                    QueryProductDetailsParams.Product.newBuilder()
                        .setProductId(productID)
                        .setProductType(BillingClient.ProductType.SUBS)
                        .build()
                )
            )
            .build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) { result, productDetailsList ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                onResult(productDetailsList.firstOrNull())
            } else {
                Log.e("BillingManager", "Failed to fetch product details: ${result.debugMessage}")
                onResult(null)
            }
        }
    }

    fun queryActiveSubscriptions() {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        billingClient.queryPurchasesAsync(params) { billingResult, purchaseList ->
            if(billingResult.responseCode == BillingClient.BillingResponseCode.OK){
                purchaseList.forEach {
                    handlePurchase(it)
                }
            }
        }
    }

    fun handlePurchase(purchase: Purchase) {
        when (purchase.purchaseState) {
            Purchase.PurchaseState.PENDING -> {
                Toast.makeText(context, "Purchase pending. Please complete the payment.", Toast.LENGTH_LONG).show()
            }
            Purchase.PurchaseState.PURCHASED -> {
                if (!purchase.isAcknowledged) {
                    val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.purchaseToken)
                        .build()

                    billingClient.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                            BillingState.isSubscribed = true
                        } else {
                            Log.e("BillingManager", "Failed to acknowledge purchase: ${billingResult.debugMessage}")

                        }

                        }
                    }
            }
            else -> {
                Log.e("BillingManager", "Purchase state is not supported: ${purchase.purchaseState}")
            }
        }
    }
    override fun onPurchasesUpdated(
        result: BillingResult,
        purchaseList: List<Purchase?>?
    ) {
        if (result.responseCode == BillingClient.BillingResponseCode.OK && purchaseList != null) {
            purchaseList.forEach {
                it?.let { purchase -> handlePurchase(purchase) }
            }
        } else if (result.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            Toast.makeText(context, "Purchase canceled", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Purchase failed: ${result.debugMessage}", Toast.LENGTH_SHORT).show()
        }
    }

}