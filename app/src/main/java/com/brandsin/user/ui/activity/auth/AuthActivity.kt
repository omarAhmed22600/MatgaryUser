package com.brandsin.user.ui.activity.auth

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.androidstudy.networkmanager.Tovuti
import com.brandsin.user.R
import com.brandsin.user.databinding.ActivityAuthBinding
import com.brandsin.user.ui.activity.ParentActivity
import com.brandsin.user.ui.activity.home.HomeActivity
import com.brandsin.user.utils.PrefMethods
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AuthActivity : ParentActivity() {
    lateinit var binding: ActivityAuthBinding
    var viewModel: AuthViewModel? = null
    private lateinit var navController: NavController

    var orderId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        //LocalUtil.changeLanguage(this)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_auth)
        //init view model
        initViewModel()
        binding.viewModel = viewModel
        navController = findNavController(R.id.nav_auth_host_fragment)

        Tovuti.from(this).monitor { connectionType, isConnected, isFast ->
            if (isConnected) {
                binding.noWifi.visibility = View.GONE
            } else {
                binding.noWifi.visibility = View.VISIBLE
            }
        }

        //data from NotificationOpenedHandler

        if (intent.getStringExtra("order_id") != null) {
            orderId = intent.getStringExtra("order_id")!!.toInt()
        }
        if(orderId==-1){
            val bundle = intent.extras
            if (bundle != null)
                if (bundle!!.getString("order_id") != null)
                    orderId = bundle!!.getString("order_id")!!.toInt()
        }

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

        viewModel!!.clickableLiveData.observe(this, {
            viewModel!!.obsIsClickable.set(false)
            lifecycleScope.launch {
                delay(2000)
                viewModel!!.clickableLiveData.value = false
                viewModel!!.obsIsClickable.set(true)
            }
        })

        setUpToolbarAndStatusBar()
        startIntent()

    }

    private fun initViewModel() {
        if (viewModel == null) {
            viewModel = ViewModelProvider(this@AuthActivity).get(AuthViewModel::class.java)
        }
    }

    private fun setUpToolbarAndStatusBar() {
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
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
        if (orderId != -1) {
            if (PrefMethods.getLoginState()) {
                var intent = Intent(this@AuthActivity, HomeActivity::class.java)
                intent.putExtra("order_id", orderId)
                startActivity(intent)
                finish()
            }
        }
    }
}