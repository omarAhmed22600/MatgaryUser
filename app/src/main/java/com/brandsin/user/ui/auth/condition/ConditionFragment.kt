package com.brandsin.user.ui.auth.condition

import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.brandsin.user.R
import com.brandsin.user.databinding.AuthFragmentConditionBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.ui.activity.auth.AuthActivity
import com.brandsin.user.ui.activity.auth.BaseAuthFragment

class ConditionFragment : BaseAuthFragment(), Observer<Any?>
{
    lateinit var binding : AuthFragmentConditionBinding
    lateinit var viewModel : ConditionViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.auth_fragment_condition, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(ConditionViewModel::class.java)
        binding.viewModel = viewModel

        (requireActivity() as AuthActivity).setBarName(getString(R.string.terms_and_conditions))

        viewModel.mutableLiveData.observe(viewLifecycleOwner, this)

        viewModel.setShowProgress(true)
        viewModel.apiCondition()

        viewModel.showProgress().observe(viewLifecycleOwner, { aBoolean ->
            if (!aBoolean!!) {
                binding.progressLayout.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            } else {
                binding.progressLayout.visibility = View.VISIBLE
                requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            }
        })
    }

    override fun onChanged(it: Any?)
    {
        if(it == null) return
        when (it) {
            Codes.SHOW_CONDITIONS -> {
                binding.loginText.text = Html.fromHtml(Html.fromHtml(viewModel.txt).toString())
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
}