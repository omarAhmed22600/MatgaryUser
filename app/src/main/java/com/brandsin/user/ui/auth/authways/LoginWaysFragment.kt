package com.brandsin.user.ui.auth.authways

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.brandsin.user.databinding.AuthFragmentLoginWaysBinding
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.ui.activity.auth.AuthActivity
import com.brandsin.user.ui.activity.auth.BaseAuthFragment
import com.brandsin.user.ui.activity.home.HomeActivity
import com.brandsin.user.utils.PrefMethods
import org.json.JSONException
import com.brandsin.user.R
import java.util.*

class LoginWaysFragment : BaseAuthFragment(), Observer<Any?> , GoogleApiClient.OnConnectionFailedListener{

    lateinit var binding: AuthFragmentLoginWaysBinding
    lateinit var viewModel : LoginWaysViewModel


    var fbLogin: Button? = null
    var loginButton: LoginButton? = null
    var callbackManager: CallbackManager? = null

    private var mGoogleApiClient: GoogleApiClient? = null
    val RC_SIGN_IN = 6

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.auth_fragment_login_ways,
            container,
            false)

        (requireActivity() as AuthActivity).setBarName(getString(R.string.login))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginWaysViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.mutableLiveData.observe(viewLifecycleOwner, this)
        viewModel.apiCountryId()

        viewModel.showProgress().observe(viewLifecycleOwner, { aBoolean ->
            if (!aBoolean!!) {
                binding.progressLayout.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            } else {
                binding.progressLayout.visibility = View.VISIBLE
                requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        })

        FacebookSdk.sdkInitialize(requireActivity().applicationContext)
        callbackManager = CallbackManager.Factory.create()
        facebookLogin()

        binding.btnPhoneLogin.setOnClickListener {
            findNavController().navigate(R.id.ways_to_login)
        }

        binding.btnFacebook.setOnClickListener {
            loginButton!!.performClick()
        }

        binding.btnGmail.setOnClickListener {

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
            mGoogleApiClient = GoogleApiClient.Builder(requireActivity())
                .enableAutoManage(requireActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build()

            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
    }

    override fun onChanged(it: Any?) {
        if (it == null) return
        when (it) {
            Codes.LOGIN_SUCCESS -> {
                startActivity(Intent(requireActivity(), HomeActivity::class.java))
                requireActivity().finishAffinity()
                viewModel.setShowProgress(false)
            }
            else -> {
                if (it is String) {
                    showToast(it.toString(), 1)
                }
                viewModel.setShowProgress(false)
            }
        }
    }

    //  facebook login
    fun facebookLogin() {
        loginButton = binding.loginFb
        fbLogin = binding.btnFacebook
        LoginManager.getInstance().logOut()
        loginButton!!.setReadPermissions(Arrays.asList("email"))
        loginButton!!.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {

                setFacebookData(loginResult)
            }

            override fun onCancel() {

            }

            override fun onError(error: FacebookException) {

            }
        })
    }
    // Parsing facebook result response to fetch user data
    fun setFacebookData(loginResult: LoginResult) {
        val request = GraphRequest.newMeRequest(
            loginResult.accessToken
        ) { `object`, response ->
            try {
                val name = response.jsonObject.getString("first_name") + " " + response.jsonObject.getString(
                    "last_name")
                val email = response.jsonObject.getString("email")
                //Call api request
                viewModel.setShowProgress(true)
                viewModel.apiLogin("P" + response.jsonObject.getString("id"),
                    response.jsonObject.getString("id"),
                    PrefMethods.getLanguage(), "Facebook",
                    name, email)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        val params = Bundle()
        params.putString("fields", "id,email,first_name,last_name,gender")
        request.parameters = params
        request.executeAsync()
    }

    // Attempting google login
    fun goLogin(result: GoogleSignInResult?) {

        if (result!!.isSuccess) {
            // Signed in successfully, show authenticated UI.
            val acct = result.signInAccount

            val name = acct!!.displayName
            val email = acct.email
            //Call api request
            viewModel.setShowProgress(true)
            viewModel.apiLogin("P" + acct.id,
                acct.id!!,
                PrefMethods.getLanguage(), "Gmail",
                name!!, email!!)


//            Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback { }
        } else {
            // Signed out, show unauthenticated UI.
        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d("google", "onConnectionFailed:$connectionResult")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            goLogin(result)
        }
    }
    override fun onPause() {
        super.onPause()
        mGoogleApiClient?.stopAutoManage(requireActivity())
        mGoogleApiClient?.disconnect()
    }
}