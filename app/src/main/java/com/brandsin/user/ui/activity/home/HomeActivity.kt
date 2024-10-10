package com.brandsin.user.ui.activity.home

import android.annotation.SuppressLint
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.brandsin.user.R
import com.brandsin.user.databinding.ActivityHomeBinding
import com.brandsin.user.databinding.NavHeaderMainBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Params
import com.brandsin.user.ui.activity.ParentActivity
import com.brandsin.user.ui.activity.auth.AuthActivity
import com.brandsin.user.ui.main.homenew.HomeNewFragmentDirections
import com.brandsin.user.ui.main.order.storedetails.addons.skus.activity.OrderAddonsActivity
import com.brandsin.user.ui.main.order.storedetails.addons.skus.dialog.DialogOrderAddonsFragment
import com.brandsin.user.utils.PrefMethods
import com.brandsin.user.utils.Utils
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.coroutines.delay
import timber.log.Timber

class HomeActivity : ParentActivity(), Observer<Any?> {

    private lateinit var binding: ActivityHomeBinding

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var navView: NavigationView

    var viewModel: MainViewModel? = null

    private lateinit var navController: NavController

    var orderId = -1

    /**
     * Change ViewPage sliding programmatically(not using reflection).
     * https://tech.dely.jp/entry/2018/12/13/110000
     * What for?
     * setCurrentItem(int, boolean) changes too fast. And it cannot set animation duration.
     */
    private var prevDragPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)

        // init view model
        initViewModel()
        binding.viewModel = viewModel
        viewModel?.mutableLiveData!!.observe(this, this)

        // data from NotificationOpenedHandler

        checkIntentFireBase()

//        if (intent.getIntExtra("order_id", -1) != -1) {
//            orderId = intent.getIntExtra("order_id", -1)
//        } else {
//            val bundle = intent.extras
//            if (bundle != null)
//                if (bundle.getInt("order_id") != null)
//                    orderId = bundle.getInt("order_id").toInt()
//        }
//        Log.d("orderId", "onCreate: $orderId")

        initConnectivityManager()
        Timber.e("data is ${intent.extras}\n${intent.getStringExtra("chat_id")}")
        navController = findNavController(R.id.nav_home_host_fragment)
        val navView: BottomNavigationView = binding.navView
        if (intent.getIntExtra("chat_id",-1) != -1) {
            Timber.e("chat")
            navController.navigate(R.id.inboxFragment)
        } else if (intent.getIntExtra("order_id",-1) != -1)
        {
            Timber.e("order")
            val args = Bundle().apply {
                putInt("order_id", intent.getIntExtra("order_id",-1))  // Pass the chat_id variable
            }
            navController.navigate(R.id.nav_order_details,args)
        }else if (intent.getIntExtra("refundable_id",-1) != -1)
        {
            Timber.e("refund")
            navController.navigate(R.id.refundableProductsFragment)
        } else if (intent.getIntExtra("wallet_id",-1) != -1)
        {
            Timber.e("payment")
            navController.navigate(R.id.nav_payment)
        }
//        FirebaseMessaging.getInstance().getInitialMessage()
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        /*appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                //R.id.nav_offers,
                //R.id.nav_notifications,
                //R.id.nav_payment,
                //R.id.nav_my_orders,
                //R.id.nav_help,
                //R.id.nav_about,
                //R.id.nav_contact
                R.id.nav_favourites,
                R.id.nav_discover,
                R.id.nav_profile
            )
        )*/

        //setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        setUpToolbarAndStatusBar()
        //navView.setupWithNavController(navController)
        //setupNavHeader()
        binding.ibBack.setOnClickListener {
            when (navController.currentDestination?.id) {
                R.id.nav_home -> {
                    finishAffinity()
                }

                R.id.nav_order_status -> {
                    navController.navigate(R.id.nav_my_orders)
                }
//                R.id.nav_my_orders -> {
//                    startActivity(Intent(this, HomeActivity::class.java))
//                    finishAffinity()
//                }
                else -> {
                    navController.navigateUp()
                }
            }
        }

        binding.icWhiteBack.setOnClickListener {
            when (navController.currentDestination?.id) {
                R.id.nav_home -> {
                    finishAffinity()
                }

                R.id.nav_order_status -> {
                    navController.navigate(R.id.nav_my_orders)
                }
//                R.id.nav_my_orders -> {
//                    startActivity(Intent(this, HomeActivity::class.java))
//                    finishAffinity()
//                }
                else -> {
                    navController.navigateUp()
                }
            }
        }

        when (PrefMethods.getUserData()) {
            null -> {
                viewModel!!.obsIsLogin.set(false)
            }

            else -> {
                viewModel!!.obsIsLogin.set(true)
            }
        }

        if (orderId != -1 && orderId != 0) {
            val action = HomeNewFragmentDirections.homeNewToOrderDetails(orderId)
            navController.navigate(action)
            orderId = -1
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

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        Log.d("TAG", "onWindowFocusChanged: $hasFocus")
    }

    private fun setUpToolbarAndStatusBar() {
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.profileButtomNavFragment, R.id.nav_home_new -> {
                    customBarColor(ContextCompat.getColor(this, R.color.white))
                    binding.tvLoginTitle.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.black
                        )
                    )
                    binding.ibBack.visibility = View.VISIBLE
                    binding.icWhiteBack.visibility = View.GONE
                    viewModel?.obsShowToolbar!!.set(false)
                    binding.navView.visibility = View.VISIBLE
                }

                R.id.nav_home, R.id.nav_search, R.id.nav_store_details -> {
                    customBarColor(ContextCompat.getColor(this, R.color.white))
                    binding.tvLoginTitle.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.black
                        )
                    )
                    binding.ibBack.visibility = View.VISIBLE
                    binding.icWhiteBack.visibility = View.GONE
                    viewModel?.obsShowToolbar!!.set(false)
                    binding.navView.visibility = View.GONE
                }

                R.id.videoPreviewFragment, R.id.imagesPreviewFragment, R.id.refundableSuccessFragment,
                R.id.storyDetailsFragment, R.id.messageFragment, R.id.messageImagePreviewFragment -> {
                    binding.ibBack.visibility = View.GONE
                    binding.icWhiteBack.visibility = View.GONE
                    viewModel?.obsShowToolbar?.set(false)
                    binding.navView.visibility = View.GONE
                }

                R.id.ratingsProductFragment -> {
                    binding.ibBack.visibility = View.VISIBLE
                    binding.toolbar.visibility = View.VISIBLE
                    binding.icWhiteBack.visibility = View.GONE
                    viewModel?.obsShowToolbar?.set(true)
                    binding.navView.visibility = View.GONE
                }

                R.id.nav_favorites, R.id.nav_discover -> {
                    binding.ibBack.visibility = View.GONE
                    binding.icWhiteBack.visibility = View.GONE
                    viewModel?.obsShowToolbar!!.set(false)
                    binding.navView.visibility = View.VISIBLE
                }

                R.id.nav_notifications, R.layout.home_fragment_store_information -> { // R.id.nav_favourites,
                    customBarColor(ContextCompat.getColor(this, R.color.white))
                    binding.tvLoginTitle.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.black
                        )
                    )
                    binding.ibBack.visibility = View.VISIBLE
                    binding.icWhiteBack.visibility = View.GONE
                    viewModel?.obsShowToolbar!!.set(true)
                    binding.navView.visibility = View.GONE
                }

                R.id.nav_cart -> {
                    customBarColor(ContextCompat.getColor(this, R.color.white))
                    binding.tvLoginTitle.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.black
                        )
                    )
                    binding.ibBack.visibility = View.VISIBLE
                    binding.icWhiteBack.visibility = View.GONE
                    viewModel?.obsShowToolbar!!.set(false)
                    binding.navView.visibility = View.GONE
                }

                R.id.nav_payment -> {
                    viewModel?.obsShowToolbar!!.set(true)
                    binding.ibBack.visibility = View.INVISIBLE
                    binding.icWhiteBack.visibility = View.VISIBLE
                    binding.tvLoginTitle.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.white
                        )
                    )
                    when {
                        PrefMethods.getUserData() != null -> {
                            customBarColor(ContextCompat.getColor(this, R.color.black))
                            binding.navView.visibility = View.GONE
                        }

                        else -> {
                            customBarColor(ContextCompat.getColor(this, R.color.black))
                            binding.navView.visibility = View.GONE
                        }
                    }
                }

                R.id.nav_my_orders, R.id.refundableProductsFragment, R.id.addRefundableProductFragment -> {
                    viewModel?.obsShowToolbar!!.set(true)
                    binding.ibBack.visibility = View.VISIBLE
                    binding.tvLoginTitle.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.black
                        )
                    )
                    binding.icWhiteBack.visibility = View.GONE
                    binding.navView.visibility = View.GONE
                    when {
                        PrefMethods.getUserData() != null -> {
                            // customBarColor(ContextCompat.getColor(this, R.color.black))
                        }

                        else -> {
                            //    customBarColor(ContextCompat.getColor(this, R.color.offers_bg_color))
                        }
                    }
                }

                R.id.nav_contact -> {
                    binding.ibBack.visibility = View.INVISIBLE
                    binding.icWhiteBack.visibility = View.VISIBLE
                    customBarColor(ContextCompat.getColor(this, R.color.color_primary))

                    binding.tvLoginTitle.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.white
                        )
                    )
                    viewModel?.obsShowToolbar!!.set(true)
                    binding.navView.visibility = View.GONE
                }

                R.id.nav_offers, R.id.nav_confirm_order, R.id.nav_help, R.id.nav_about, R.id.brandsFragment, R.id.transferPointsFragment, R.id.confirmTransferPointsFragment -> {
                    binding.ibBack.visibility = View.VISIBLE
                    binding.icWhiteBack.visibility = View.GONE
                    customBarColor(ContextCompat.getColor(this, R.color.white))
                    binding.tvLoginTitle.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.black
                        )
                    )
                    viewModel?.obsShowToolbar!!.set(true)
                    binding.navView.visibility = View.GONE
                }

                else -> {
                    binding.ibBack.visibility = View.VISIBLE
                    binding.icWhiteBack.visibility = View.GONE
                    binding.tvLoginTitle.setTextColor(
                        ContextCompat.getColor(
                            this,
                            R.color.black
                        )
                    )
                    viewModel?.obsShowToolbar!!.set(true)
                    binding.navView.visibility = View.GONE
                }
            }
        }
    }

    private fun initViewModel() {
        if (viewModel == null) {
            viewModel = ViewModelProvider(this@HomeActivity)[MainViewModel::class.java]
        }
    }

    private fun setupNavHeader() {
        val header: View = navView.getHeaderView(0)
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE)
        val binding = NavHeaderMainBinding.inflate(inflater as LayoutInflater, navView, true)
        binding.viewModel = viewModel

        val btnEdit = header.findViewById<View>(R.id.btn_login) as Button

        btnEdit.setOnClickListener {
            navController.navigate(R.id.nav_profile)
            //drawerLayout.closeDrawer(GravityCompat.START)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_home_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

//    fun showDrawer() {
//        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            drawerLayout.closeDrawer(GravityCompat.START)
//        } else {
//            drawerLayout.openDrawer(GravityCompat.START)
//        }
//    }

//    fun lockDrawer() {
//        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//    }

    override fun onChanged(value: Any?) {
        if (value == null) return
        when (value) {
            Codes.BUTTON_LOGIN_CLICKED -> {
                startActivity(Intent(this, AuthActivity::class.java))
            }

            Codes.LOGOUT_CLICK -> {
                startActivity(Intent(this, AuthActivity::class.java))
                finishAffinity()
            }

            Codes.EDIT_CLICKED -> {
                navController.navigate(R.id.home_to_profile)
            }
        }
    }

    private fun customBarColor(color: Int) {
        binding.toolbar.setBackgroundColor(color)

        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = color
        }*/
    }

    fun setBarName(title: String) {
        initViewModel()
        viewModel?.obsTitle?.set(title)
    }

    override fun onResume() {
        super.onResume()

        when (PrefMethods.getUserData()) {
            null -> {
                viewModel!!.obsIsLogin.set(false)
            }

            else -> {
                viewModel!!.obsIsLogin.set(true)
                viewModel!!.setUpUserData()
            }
        }
    }


//    private fun fakeDrag(forward: Boolean) {
//        //if (viewPagerFixedViewPager != null) {
//            if (prevDragPosition == 0 && viewPagerFixedViewPager.beginFakeDrag()) {
//                ValueAnimator.ofInt(0, viewPagerFixedViewPager.width).apply {
//                    duration = 400L
//                    interpolator = FastOutSlowInInterpolator()
//                    addListener(object : Animator.AnimatorListener {
//                        override fun onAnimationRepeat(p0: Animator?) {}
//
//                        override fun onAnimationEnd(animation: Animator?) {
//                            removeAllUpdateListeners()
//                            if (viewPagerFixedViewPager.isFakeDragging) {
//                                viewPagerFixedViewPager.endFakeDrag()
//                            }
//                            prevDragPosition = 0
//                        }
//
//                        override fun onAnimationCancel(animation: Animator?) {
//                            removeAllUpdateListeners()
//                            if (viewPagerFixedViewPager.isFakeDragging) {
//                                viewPagerFixedViewPager.endFakeDrag()
//                            }
//                            prevDragPosition = 0
//                        }
//
//                        override fun onAnimationStart(p0: Animator?) {}
//                    })
//                    addUpdateListener {
//                        if (!viewPagerFixedViewPager.isFakeDragging) return@addUpdateListener
//                        val dragPosition: Int = it.animatedValue as Int
//                        val dragOffset: Float =
//                            ((dragPosition - prevDragPosition) * if (forward) -1 else 1).toFloat()
//                        prevDragPosition = dragPosition
//                        viewPagerFixedViewPager.fakeDragBy(dragOffset)
//                    }
//                }.start()
//            }
//      //  }
//    }

    override fun onBackPressed() {
        super.onBackPressed()
        when (navController.currentDestination?.id) {
            R.id.nav_home_new -> {
                finishAffinity()
            }

            R.id.nav_order_status -> {
                navController.navigate(R.id.nav_my_orders)
            }

            R.id.nav_my_orders -> {
                startActivity(Intent(this, HomeActivity::class.java))
                finishAffinity()
            }

            else -> {
                navController.navigateUp()
            }
        }
    }

    private lateinit var networkConnectionManager: ConnectivityManager
    private lateinit var networkConnectionCallback: ConnectivityManager.NetworkCallback

    private fun initConnectivityManager() {
        networkConnectionManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        networkConnectionCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                // there is internet
                binding.noWifi.visibility = View.GONE
            }

            override fun onLost(network: Network) {
                // there is no internet
                lifecycleScope.launchWhenResumed {
                    delay(1000)
                    binding.noWifi.visibility = View.VISIBLE
                }
            }
        }
        networkConnectionManager.registerDefaultNetworkCallback(networkConnectionCallback)
    }

    fun showLoading() {
        binding.progressLoading.visibility = View.VISIBLE
    }

    fun hideLoading() {
        binding.progressLoading.visibility = View.GONE
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        Log.d("onNewIntent", "intent?.extras  ==  ${intent?.extras}")
        Log.d("onNewIntent", "intent?.data ==  ${intent?.data}")
        Log.d("onNewIntent", "intent?.type ==  ${intent?.type}")

        if (intent?.extras?.containsKey("product_id") == true) {
            Log.d(
                "onNewIntent",
                "containsKey(product_id) ==  ${intent?.extras?.containsKey("product_id")}"
            )

//                val mIntent = Intent(this, ProductDetailsActivity::class.java)
//                mIntent.putExtra("product_id", metadata.getString("product_id").toInt())
//                startActivity(mIntent)
        } else if (intent?.extras?.containsKey("story_id") == true) {
            Log.d(
                "onNewIntent",
                "containsKey(story_id) ==  ${intent?.extras?.containsKey("story_id")}"
            )

            // val mIntent = Intent(this, StoryDisplayFragment::class.java)
//            mIntent.putExtra("brand_id", metadata.getString("brand_id"))
//            mIntent.putExtra("category_name", metadata.getString("category_name"))
            // startActivity(mIntent)
        } else if (intent?.extras?.containsKey("store_id") == true) {
            Log.d(
                "onNewIntent",
                "containsKey(store_id) ==  ${intent?.extras?.containsKey("store_id")}"
            )

            // val mIntent = Intent(this, StoreDetailsFragment::class.java)
            // mIntent.putExtra("store_id", store_id.toString())
            // mIntent.putExtra("category_name", metadata.getString("category_name"))
            // startActivity(mIntent)
        } else if (intent?.extras?.containsKey("order_id") == true) {
            Log.d(
                "onNewIntent",
                "containsKey(order_id) ==  ${intent?.extras?.containsKey("order_id")}"
            )

            // val mIntent = Intent(this, StoreDetailsFragment::class.java)
            // mIntent.putExtra("store_id", store_id.toString())
            // mIntent.putExtra("category_name", metadata.getString("category_name"))
            // startActivity(mIntent)
        } else {
            startActivity(Intent(this, HomeActivity::class.java))
            finishAffinity()
        }
    }
}
