package com.brandsin.user.ui.menu.payment

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.TEXT_ALIGNMENT_TEXT_END
import android.view.View.TEXT_ALIGNMENT_TEXT_START
import android.view.View.TEXT_ALIGNMENT_VIEW_END
import android.view.View.TEXT_ALIGNMENT_VIEW_START
import android.view.ViewGroup
import androidx.compose.ui.text.style.TextAlign
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.brandsin.user.R
import com.brandsin.user.databinding.FragmentConfirmTransferPointsBinding
import com.brandsin.user.network.ResponseHandler
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.utils.PrefMethods

class ConfirmTransferPointsFragment : BaseHomeFragment() {

    private var _binding: FragmentConfirmTransferPointsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PaymentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConfirmTransferPointsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBarName(getString(R.string.transfer_points))

        initView()
        setBtnListener()
        subscribeData()
    }

    private fun initView() {
        if (PrefMethods.getLanguage() == "en") {
            binding.recipientName.textAlignment = TEXT_ALIGNMENT_VIEW_START
        } else {
            binding.recipientName.textAlignment = TEXT_ALIGNMENT_VIEW_END
        }
        binding.recipientName.text = viewModel.recipientName
        binding.recipientMobileNumber.text = viewModel.recipientMobileNumber
        binding.numberOfPoints.text = viewModel.numberOfPoints
    }

    private fun setBtnListener() {
        binding.btnTransformation.setOnClickListener {
            viewModel.transferPoints()
        }
    }

    private fun subscribeData() {
        viewModel.transferPointsResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseHandler.Success -> {
                    // bind data to the view
                    viewModel.obsIsLoading.set(false)
                    if (it.data?.success == true) {
                        showToast(it.data.message.toString(), 2)
                        findNavController().navigate(R.id.transferPointsSuccessFragment)
                    } else {
                        showToast(it.data?.message.toString(), 1)
                    }
                }

                is ResponseHandler.Error -> {
                    // show error message
                    viewModel.obsIsEmpty.set(true)
                    viewModel.obsIsLoading.set(false)
                }

                is ResponseHandler.Loading -> {
                    // show a progress bar
                    viewModel.obsIsLoading.set(true)
                }

                is ResponseHandler.StopLoading -> {
                    // show a progress bar
                    viewModel.obsIsLoading.set(false)
                }

                else -> {}
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}