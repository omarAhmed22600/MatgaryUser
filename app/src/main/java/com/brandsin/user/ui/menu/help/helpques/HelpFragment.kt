package com.brandsin.user.ui.menu.help.helpques

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.brandsin.user.R
import com.brandsin.user.databinding.HomeFragmentHelpBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.menu.help.HelpQuesItem
import com.brandsin.user.network.Status
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.utils.map.observe
import timber.log.Timber

class HelpFragment : BaseHomeFragment(), Observer<Any?> {

    private lateinit var binding: HomeFragmentHelpBinding

    private lateinit var viewModel: HelpViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment_help, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[HelpViewModel::class.java]
        binding.viewModel = viewModel

        setBarName(getString(R.string.help))

        viewModel.helpAdapter.helpLiveData.observe(viewLifecycleOwner, this)

        binding.swipeLayout.setOnRefreshListener {
            binding.swipeLayout.isRefreshing = false
            viewModel.getHelpQues()
            viewModel.getPhoneNumber()
        }

        binding.btnCall.setOnClickListener {
            val action = HelpFragmentDirections.helpToContact()
            findNavController().navigate(action)
        }
        observe(viewModel.apiResponseLiveData) {
            when (it.status) {
                Status.ERROR_MESSAGE -> {
                    showToast(it.message.toString(), 1)
                }

                Status.SUCCESS_MESSAGE -> {
                    showToast(it.message.toString(), 2)
                }

                else -> {
                    Timber.e(it.message)
                }
            }
        }
    }

    override fun onChanged(value: Any?) {
        if (value == null) return
        when (value) {
            is HelpQuesItem -> {
                val action =
                    HelpFragmentDirections.helpToAnswers(value, viewModel.phoneNumber.toString())
                findNavController().navigate(action)
            }

            Codes.EMPTY_MESSAGE -> {
                showToast(getString(R.string.please_enter_your_message), 1)
            }
        }
    }
}