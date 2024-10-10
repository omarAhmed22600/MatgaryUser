package com.brandsin.user.ui.menu.payment

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.brandsin.user.databinding.FragmentTransferPointsSuccessBinding
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.ui.activity.home.HomeActivity
import com.brandsin.user.utils.PrefMethods

class TransferPointsSuccessFragment : BaseHomeFragment() {

    private var _binding: FragmentTransferPointsSuccessBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PaymentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransferPointsSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        setBtnListener()
    }

    private fun initView() {
        if (PrefMethods.getLanguage() == "en") {
            binding.recipientName.textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        } else {
            binding.recipientName.textAlignment = View.TEXT_ALIGNMENT_VIEW_END
        }

        binding.recipientName.text = viewModel.recipientName
        binding.recipientMobileNumber.text = viewModel.recipientMobileNumber
        binding.ponits.text = viewModel.numberOfPoints
    }

    private fun setBtnListener() {
        binding.btnHome.setOnClickListener {
            startActivity(Intent(requireActivity(), HomeActivity::class.java))
        }

        binding.btnShare.setOnClickListener {
            // startActivity(Intent(requireActivity(), HomeActivity::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}