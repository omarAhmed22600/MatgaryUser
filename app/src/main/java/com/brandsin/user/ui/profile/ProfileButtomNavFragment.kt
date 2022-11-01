package com.brandsin.user.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.brandsin.user.R
import com.brandsin.user.databinding.FragmentProfileButtomNavBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.ui.activity.auth.AuthActivity
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.ui.activity.home.MainViewModel
import com.brandsin.user.utils.PrefMethods


class ProfileButtomNavFragment : BaseHomeFragment() , Observer<Any?> {
    private lateinit var binding: FragmentProfileButtomNavBinding
    var viewModel: MainViewModel? = null



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding=FragmentProfileButtomNavBinding.inflate(inflater,container,false)

        initViewModel()
        viewModel?.mutableLiveData!!.observe(viewLifecycleOwner, this)
        binding.viewModel = viewModel

        when (PrefMethods.getUserData()) {
            null -> {
                viewModel!!.obsIsLogin.set(false)
            }
            else -> {
                viewModel!!.obsIsLogin.set(true)
            }
        }
        setBarName(getString(R.string.profile))
        //(activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        return binding.root
    }
    private fun initViewModel() {
        if (viewModel == null) {
            viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        }
    }

    override fun onChanged(t: Any?) {
        when (t) {
            Codes.BUTTON_LOGIN_CLICKED -> {
                startActivity(Intent(requireActivity(), AuthActivity::class.java))
            }
            Codes.LOGOUT_CLICK -> {
                startActivity(Intent(requireActivity(), AuthActivity::class.java))
            }
            Codes.EDIT_CLICKED -> {
                findNavController().navigate(R.id.home_to_profile)
            }
            Codes.BUTTON_OFFER_CLICKED->{
                findNavController().navigate(R.id.nav_offers)
            }
            Codes.BUTTON_NOTIFICATION_CLICKED->{
                findNavController().navigate(R.id.nav_notifications)
            }
            Codes.BUTTON_MYORDER_CLICKED->{
                findNavController().navigate(R.id.nav_my_orders)
            }
            Codes.BUTTON_PAYMENT_CLICKED->{
                findNavController().navigate(R.id.nav_payment)
            }
            Codes.BUTTON_HELP_CLICKED->{
                findNavController().navigate(R.id.nav_help)
            }
            Codes.BUTTON_ABOUT_CLICKED->{
                findNavController().navigate(R.id.nav_about)
            }
            Codes.BUTTON_CONTACT_CLICKED->{
                findNavController().navigate(R.id.nav_contact)
            }
        }
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



}