package com.brandsin.user.ui.activity.home

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.androidstudy.networkmanager.Tovuti
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.brandsin.user.R
import com.brandsin.user.databinding.ActivityHomeBinding
import com.brandsin.user.databinding.NavHeaderMainBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.ui.activity.ParentActivity
import com.brandsin.user.ui.activity.auth.AuthActivity
import com.brandsin.user.ui.main.homenew.HomeNewFragmentDirections
import com.brandsin.user.utils.PrefMethods

class HomeActivity : ParentActivity(), Observer<Any?>, NavController.OnDestinationChangedListener {
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
        //data from NotificationOpenedHandler
        if (intent.getIntExtra("order_id",-1) != -1) {
            orderId = intent.getIntExtra("order_id", -1)
        }else{
            val bundle = intent.extras
            if (bundle != null)
                if (bundle!!.getInt("order_id") != null)
                    orderId = bundle!!.getInt("order_id")!!.toInt()

        }

        Log.d("orderId", "onCreate: "+orderId)
        //init view model
        initViewModel()
        binding.viewModel = viewModel
        navController = findNavController(R.id.nav_home_host_fragment)
        val navView: BottomNavigationView = binding.navView
        navController.addOnDestinationChangedListener(this)
        viewModel?.mutableLiveData!!.observe(this, this)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.nav_home,
//                //R.id.nav_offers,
//                //R.id.nav_notifications,
//                //R.id.nav_payment,
//                //R.id.nav_my_orders,
//                //R.id.nav_help,
//                //R.id.nav_about,
//                //R.id.nav_contact
//                R.id.nav_favourits,
//                R.id.nav_discover,
//                R.id.nav_profile
//            )
//        )

        //setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        Tovuti.from(this).monitor { connectionType, isConnected, isFast ->
            if (isConnected) {
                binding.noWifi.visibility = View.GONE
            } else {
                binding.noWifi.visibility = View.VISIBLE
            }
        }
        setUpToolbarAndStatusBar()
        //navView.setupWithNavController(navController)
        //setupNavHeader()
//        binding.ibBack.setOnClickListener {
//            when (navController.currentDestination?.id) {
//                R.id.nav_home -> {
//                    finishAffinity()
//                }
//                R.id.nav_order_status -> {
//                    navController.navigate(R.id.nav_my_orders)
//                }
//                R.id.nav_my_orders -> {
//                    startActivity(Intent(this, HomeActivity::class.java))
//                    finishAffinity()
//                }
//                else -> {
//                    navController.navigateUp()
//                }
//            }
//        }

        when (PrefMethods.getUserData()) {
            null -> {
                viewModel!!.obsIsLogin.set(false)
            }
            else -> {
                viewModel!!.obsIsLogin.set(true)
            }
        }


        if (orderId != -1&&orderId != 0){
            val action = HomeNewFragmentDirections.homeNewToOrderDetails(orderId)
            navController.navigate(action)
            orderId=-1
        }

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        Log.d("TAG", "onWindowFocusChanged: "+hasFocus)
    }


    private fun setUpToolbarAndStatusBar() {
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.nav_home_new, R.id.nav_home, R.id.nav_search, R.id.nav_store_details, R.id.nav_notifications -> {
                    //customBarColor(ContextCompat.getColor(this, R.color.white))
                    viewModel?.obsShowToolbar!!.set(false)
                }
                R.id.nav_cart -> {
                    //customBarColor(ContextCompat.getColor(this, R.color.offers_bg_color))
                    viewModel?.obsShowToolbar!!.set(false)
                }
                R.id.nav_payment -> {
                    viewModel?.obsShowToolbar!!.set(true)
                    when {
                        PrefMethods.getUserData() != null -> {
                            customBarColor(ContextCompat.getColor(this, R.color.payment_color))
                        }
                        else -> {
                            customBarColor(ContextCompat.getColor(this, R.color.offers_bg_color))
                        }
                    }
                }
                R.id.nav_my_orders -> {
                    viewModel?.obsShowToolbar!!.set(true)
                    when {
                        PrefMethods.getUserData() != null -> {
                      //      customBarColor(ContextCompat.getColor(this, R.color.white))
                        }
                        else -> {
                        //    customBarColor(ContextCompat.getColor(this, R.color.offers_bg_color))
                        }
                    }
                }
                R.id.nav_contact -> {
                    customBarColor(ContextCompat.getColor(this, R.color.payment_color))
                    viewModel?.obsShowToolbar!!.set(true)
                }
                R.id.nav_offers, R.id.nav_confirm_order -> {
                    //customBarColor(ContextCompat.getColor(this, R.color.offers_bg_color))
                    viewModel?.obsShowToolbar!!.set(true)
                }
                else -> {
                    viewModel?.obsShowToolbar!!.set(true)
                    //customBarColor(ContextCompat.getColor(this, R.color.white))
                }
            }
        }
    }

    private fun initViewModel() {
        if (viewModel == null) {
            viewModel = ViewModelProvider(this@HomeActivity).get(MainViewModel::class.java)
        }
    }

    private fun setupNavHeader() {
        val header: View = navView.getHeaderView(0)
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE)
        val binding = NavHeaderMainBinding.inflate(inflater as LayoutInflater, navView, true);
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

    override fun onChanged(it: Any?) {
        if (it == null) return
        when (it) {
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
        //binding.toolbar.setBackgroundColor(color)

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

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        when(destination.id){
            R.id.nav_home_new, R.id.nav_favourits,R.id.nav_discover,R.id.profileButtomNavFragment->{
                binding.navView.visibility=View.VISIBLE
            }else->{
                binding.navView.visibility=View.GONE
            }


        }
    }
}