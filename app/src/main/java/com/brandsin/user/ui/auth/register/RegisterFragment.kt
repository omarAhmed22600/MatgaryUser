package com.brandsin.user.ui.auth.register

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import com.brandsin.user.R
import com.brandsin.user.databinding.AuthFragmentRegisterBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.ui.activity.auth.BaseAuthFragment
import com.brandsin.user.ui.activity.home.HomeActivity
import com.brandsin.user.utils.PrefMethods

class RegisterFragment : BaseAuthFragment(), Observer<Any?>
{
    lateinit var binding : AuthFragmentRegisterBinding
    lateinit var viewModel : RegisterViewModel
    val bundle = Bundle()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.auth_fragment_register,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.mutableLiveData.observe(viewLifecycleOwner, this)
        viewModel.apiCountryId()

        binding.ibBack.setOnClickListener {
            findNavController().navigateUp()
        }

        viewModel.showProgress().observe(viewLifecycleOwner, { aBoolean ->
            if (!aBoolean!!) {
                binding.progressLayout.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            } else {
                binding.progressLayout.visibility = View.VISIBLE
                requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        })

        // Get token
        // [START retrieve_current_token]
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                // Get new Instance ID token
                val token = task.result.token
                viewModel.deviceTokenRequest.token = token

                // Log and toast
                val msg = getString(R.string.msg_token_fmt, token)
            })
        // [END retrieve_current_token]
    }

    override fun onChanged(it: Any?)
    {
        if(it == null) return
        when (it) {
            Codes.SHOW_CONDITIONS -> {
                findNavController().navigate(R.id.register_to_condition)
            }
            Codes.EMPTY_PHONE ->
            {
                showToast(getString(R.string.empty_phone) , 1)
            }
            Codes.INVALID_PHONE ->
            {
                showToast(getString(R.string.invalid_phone) , 1)
            }
            Codes.PASSWORD_EMPTY ->
            {
                showToast(getString(R.string.empty_password) , 1)
            }
            Codes.PASSWORD_SHORT -> {
                showToast(getString(R.string.invalid_password) , 1)
            }
            Codes.CONFIRM_PASS_EMPTY ->
            {
                showToast(getString(R.string.empty_confirm_password) , 1)
            }
            Codes.PASSWORD_NOT_MATCH -> {
                showToast(getString(R.string.not_matc_passwords) , 1)
            }
            Codes.CONDITION_REJECTED -> {
                showToast(getString(R.string.please_accept_condition) , 1)
            }
            Codes.REGISTER_SUCCESS -> {
                showToast(viewModel.code, 2)
                val action = RegisterFragmentDirections.registerToVerify(PrefMethods.getUserData()!!.phoneNumber.toString(),viewModel.userID,"register")
                findNavController().navigate(action)
                viewModel.setShowProgress(false)
            }
            Codes.SKIP_CLICKED -> {
                startActivity(Intent(requireActivity(), HomeActivity::class.java))
                requireActivity().finishAffinity()
            }
            else -> {
                if (it is String) {
                    showToast(it.toString(), 1)
                }
                viewModel.setShowProgress(false)
            }
        }
    }
}