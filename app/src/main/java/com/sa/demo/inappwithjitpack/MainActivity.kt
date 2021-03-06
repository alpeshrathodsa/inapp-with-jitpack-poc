package com.sa.demo.inappwithjitpack

import androidx.databinding.ViewDataBinding
import com.android.billingclient.api.*
import com.sa.demo.baselibrary.BaseActivity
import com.sa.demo.inappwithjitpack.databinding.ActivityMainBinding
import com.sa.demo.inappwithjitpack.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity(), PurchasesUpdatedListener {
    private lateinit var mBillingClient: BillingClient
    private val mSkuList = listOf("android.test.purchased")//test purchase sku

    lateinit var mActivityMainBinding: ActivityMainBinding
    private val mViewModel by viewModel<MainViewModel>()//viewmodel injected

    private var mServiceConnected = false

    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun postDataBinding(binding: ViewDataBinding) {
        mActivityMainBinding = binding as ActivityMainBinding
        mActivityMainBinding.viewModel = mViewModel
    }

    override fun initializeComponent() {
        setupBillingClient()
        setupUi()
    }

    private fun setupBillingClient() {
        mBillingClient = BillingClient.newBuilder(this)
            .enablePendingPurchases()
            .setListener(this)
            .build()

        mBillingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                mServiceConnected = false
            }

            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    mServiceConnected = true
                }
            }

        })
    }

    private fun setupUi() {
        btn_buy.setOnClickListener {
            loadAllSKUs()
        }
    }

    override fun onPurchasesUpdated(p0: BillingResult, purchases: MutableList<Purchase>?) {
        if (p0.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                //Handle purchase
                handlePurchase(purchase)
            }
        } else if (p0.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            //hide progress dialog
            hideProgressDialog()

            // user cancel purchase
        } else if (p0.responseCode == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            //hide progress dialog
            hideProgressDialog()

            //already purchased
        } else {
            //hide progress dialog
            hideProgressDialog()

            //custom error
            val message = p0.debugMessage
        }
    }

    private fun loadAllSKUs() = if (mBillingClient.isReady && mServiceConnected) {
        //show progress dialog here
        showProgressDialog()
        val params = SkuDetailsParams
            .newBuilder()
            .setSkusList(mSkuList)
            .setType(BillingClient.SkuType.INAPP)
            .build()
        mBillingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
            // Process the result.
            if (skuDetailsList != null) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && skuDetailsList.isNotEmpty()) {
                    //hide progress dialog
                    hideProgressDialog()
                    for (skuDetails in skuDetailsList) {
                        //this will return the SKUs from Google Play Console

                        //display price and set click listener
                        val price = (skuDetails.priceAmountMicros / 1000000.0f).toString()
                        val current = skuDetails.priceCurrencyCode

                        //launchBillingFlow
                        val billingFlowParams = BillingFlowParams
                            .newBuilder()
                            .setSkuDetails(skuDetails)
                            .build()

                        val responseCode =
                            mBillingClient.launchBillingFlow(this, billingFlowParams).responseCode
                        if (responseCode == 7) {
                            //already purchased
                        }
                    }
                } else {
                    //hide progress dialog
                    hideProgressDialog()
                }
            } else {
                //hide progress dialog
                hideProgressDialog()
            }
        }

    } else {
        println("Billing Client not ready")
    }

    private fun handlePurchase(purchase: Purchase) {
        //1. verify purchase using GOOGLE PLAY DEVELOPER API or using BillingClient.queryPurchases()
        //2. Ensure that entitlement not already grant for purchaseToken for this purchase "purchase.purchaseToken"
        //3. Give content to the user
        //4. acknwoldge the purchase

        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (purchase.sku == "android.test.purchased") {
                val orderId = purchase.orderId
                //store order id for reference and send to server

                //hide progress dialog
                hideProgressDialog()
            }
            acknowledgeOrConsumePurchase(purchase)
        } else if (purchase.purchaseState == Purchase.PurchaseState.PENDING) {
            handlePendingTransaction()

            //hide progress dialog
            hideProgressDialog()
        } else {
            //hide progress dialog
            hideProgressDialog()
        }
    }

    private fun acknowledgeOrConsumePurchase(purchase: Purchase) {
        //For consumable product
        val consumeParams =
            ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

        mBillingClient.consumeAsync(consumeParams) { billingResult, outToken ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                // Handle the success of the consume operation.
            }
        }

        //For non-consumable product
        /*if (!purchase.isAcknowledged) {
            val params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()
            mBillingClient.acknowledgePurchase(params) { billingResult ->
                val responseCode = billingResult.responseCode
                val debugMessage = billingResult.debugMessage

            }
        }*/
    }

    private fun handlePendingTransaction() {

    }
}