package com.brandsin.user.ui.activity.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.brandsin.user.R
import com.brandsin.user.databinding.ActivityAuthBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Params
import com.brandsin.user.ui.activity.ParentActivity
import com.brandsin.user.ui.activity.home.HomeActivity
import com.brandsin.user.ui.main.homenew.HomeNewFragmentDirections
import com.brandsin.user.ui.main.order.storedetails.addons.skus.activity.OrderAddonsActivity
import com.brandsin.user.ui.main.order.storedetails.addons.skus.dialog.DialogOrderAddonsFragment
import com.brandsin.user.utils.PrefMethods
import com.brandsin.user.utils.Utils
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class AuthActivity : ParentActivity() {

    lateinit var binding: ActivityAuthBinding

    var viewModel: AuthViewModel? = null

    private lateinit var navController: NavController

    private var orderId = -1
    private var chatId = -1
    private var refundableId = -1
    private var walletId = -1
    private var shouldNavigate = false

    private lateinit var networkConnectionManager: ConnectivityManager
    private lateinit var networkConnectionCallback: ConnectivityManager.NetworkCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        //LocalUtil.changeLanguage(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_auth)

        // Init view model
        initViewModel()
        binding.viewModel = viewModel

        navController = findNavController(R.id.nav_auth_host_fragment)

        initConnectivityManager()
        checkIntentFireBase()
        if (intent.getStringExtra("chat_id") != null) {
            Timber.e("chat")
            chatId = intent.getStringExtra("chat_id")?.toInt()?:-1
            shouldNavigate = true
        } else if (intent.getStringExtra("order_id") != null) {
            Timber.e("order")
            orderId = intent.getStringExtra("order_id")?.toInt()?:-1
            shouldNavigate = true
        } else if (intent.getStringExtra("refundable_id") != null) {
            Timber.e("refund")
            refundableId = intent.getStringExtra("refundable_id")?.toInt()?:-1
            shouldNavigate = true
        } else if (intent.getStringExtra("wallet_id") != null) {
            Timber.e("wallet")
            walletId = intent.getStringExtra("wallet_id")?.toInt()?:-1
            shouldNavigate = true
        }
        Timber.e("activity ${intent.extras}")
        // Data from NotificationOpenedHandler
       /* if (intent.getStringExtra("order_id") != null) {
            orderId = intent.getStringExtra("order_id")!!.toInt()
        }*/
        /*if (orderId == -1) {
            val bundle = intent.extras
            if (bundle != null)
                if (bundle.getString("order_id") != null)
                    orderId = bundle.getString("order_id")!!.toInt()
        }*/

        binding.ibBack.setOnClickListener {
            when (navController.currentDestination?.id) {
                R.id.navigation_permission -> {
                    finishAffinity()
                }

                else -> {
                    navController.navigateUp()
                }
            }
        }

        viewModel!!.clickableLiveData.observe(this) {
            viewModel!!.obsIsClickable.set(false)
            lifecycleScope.launch {
                delay(2000)
                viewModel!!.clickableLiveData.value = false
                viewModel!!.obsIsClickable.set(true)
            }
        }

        setUpToolbarAndStatusBar()
        startIntent()

    }

    private fun initViewModel() {
        if (viewModel == null) {
            viewModel = ViewModelProvider(this@AuthActivity)[AuthViewModel::class.java]
        }
    }
    @SuppressLint("LogNotTimber")
    private fun checkIntentFireBase() {
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener { pendingDynamicLinkData: PendingDynamicLinkData? ->

                Log.d(
                    "onNewIntent",
                    "pendingDynamicLinkData?.link  ==  ${pendingDynamicLinkData?.link}"
                )
                Log.d(
                    "onNewIntent",
                    "Parameter(product)  ==  ${pendingDynamicLinkData?.link?.getQueryParameter("product_Id")}"
                )
                Log.d(
                    "onNewIntent",
                    "Parameter(check)  ==  ${pendingDynamicLinkData?.link?.getQueryParameter("check")}"
                )
                Log.d(
                    "onNewIntent",
                    "Parameter(story_ID)  ==  ${pendingDynamicLinkData?.link?.getQueryParameter("story_Id")}"
                )
                Log.d(
                    "onNewIntent",
                    "Parameter(shopID)  ==  ${pendingDynamicLinkData?.link?.getQueryParameter("store_id")}"
                )

                // Get deep link from result (may be null if no link is found)
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                    Log.d(
                        "onNewIntent",
                        "pendingDynamicLinkData?.link22  ==  ${pendingDynamicLinkData?.link}"
                    )
                    if (deepLink?.getQueryParameter("product_Id") != null) {
                        val productId = deepLink.getQueryParameter("product_Id")
                        val type = deepLink.getQueryParameter("ch" +
                                "eck")
                        Log.d("onNewIntent", "product  ==  $productId")
                        Log.d("onNewIntent", "check  ==  $type")
                        when (type) {
                            "simple", "Simple" -> {
                                val bundle = Bundle()
                                bundle.putInt("productID", productId?.toInt() ?: 0)
                                Utils.startDialogActivity(
                                    this,
                                    DialogOrderAddonsFragment::class.java.name,
                                    Codes.SELECT_ORDER_ADDONS_DIALOG,
                                    bundle
                                )
                            }

                            "variable", "Variable" -> {
                                val intent = Intent(this, OrderAddonsActivity::class.java)
                                intent.putExtra(Params.STORE_PRODUCT_ITEM, productId)
                                intent.putExtra(Params.DIALOG_CLICK_ACTION, 0)
                                startActivityForResult(intent, Codes.SELECT_ORDER_ADDONS_ACTIVITY)
                            }
                        }
                    } else if (deepLink?.getQueryParameter("store_id") != null) {
                        val storeId = deepLink.getQueryParameter("store_id")
                        Log.d("onNewIntent", "shopID  ==  $storeId")
                        val action =
                            HomeNewFragmentDirections.homeNewToStoreDetails(storeId?.toInt() ?: 0)
                        navController.navigate(action)
                    } else if (deepLink?.getQueryParameter("story_Id") != null) {
                        val storyId = deepLink.getQueryParameter("story_Id")
                        Log.d("onNewIntent", "storyID ==  $storyId")

                        val bundle = Bundle()
                        bundle.putInt("STORY_ID", storyId!!.toInt())
                        navController.navigate(R.id.storyDetailsFragment, bundle)
                    } else {
                        startActivity(Intent(this, AuthActivity::class.java))
                        finishAffinity()
                    }
                }
            }
    }
    private fun setUpToolbarAndStatusBar() {
        navController.addOnDestinationChangedListener { _, destination, _ -> // controller, destination, arguments
            when (destination.id) {
                R.id.navigation_splash, R.id.navigation_permission -> {
                    //customBarColor(ContextCompat.getColor(this, R.color.status_bar_color))
                    viewModel?.obsShowToolbar!!.set(false)
                }

                R.id.navigation_login_ways, R.id.navigation_login, R.id.navigation_register -> {
                    //customBarColor(ContextCompat.getColor(this, R.color.white))
                    viewModel?.obsShowToolbar!!.set(false)
                }

                else -> {
                    viewModel?.obsShowToolbar!!.set(true)
                    //customBarColor(ContextCompat.getColor(this, R.color.white))
                }
            }
        }
    }

    private fun customBarColor(color: Int) {
        binding.toolbar.setBackgroundColor(color)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = color
        }
    }

    fun setBarName(title: String) {
        initViewModel()
        viewModel?.obsTitle!!.set(title)
    }

    private fun startIntent() {
        if (shouldNavigate) {
            if (PrefMethods.getLoginState()) {
                val intent = Intent(this@AuthActivity, HomeActivity::class.java)
                if (orderId != -1)
                    intent.putExtra("order_id", orderId)
                if (chatId != -1)
                    intent.putExtra("chat_id", chatId)
                if (refundableId != -1)
                    intent.putExtra("refundable_id", refundableId)
                if (walletId != -1)
                    intent.putExtra("wallet_id", walletId)
                startActivity(intent)
                finish()
            }
        }

    }

    private fun initConnectivityManager() {
        networkConnectionManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        networkConnectionCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                try {
                // there is internet
                binding.noWifi.visibility = View.GONE
                }catch (e:Exception) {
                    Timber.e(e.stackTraceToString())
                }
            }

            override fun onLost(network: Network) {
                try {
                    // there is no internet
                    binding.noWifi.visibility = View.VISIBLE
                }catch (e:Exception) {
                    Timber.e(e.stackTraceToString())
                }
            }
        }
        networkConnectionManager.registerDefaultNetworkCallback(networkConnectionCallback)
    }
}
