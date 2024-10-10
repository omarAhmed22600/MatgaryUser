package com.brandsin.user.ui.menu.refundableProducts

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.brandsin.user.databinding.FragmentRefundableSuccessBinding
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.ui.activity.home.HomeActivity
import com.brandsin.user.utils.PrefMethods

class RefundableSuccessFragment : BaseHomeFragment() {

    private var _binding: FragmentRefundableSuccessBinding? = null
    private val binding get() = _binding!!

    private var orderNumber: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRefundableSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orderNumber = requireArguments().getString("ORDER_NUMBER")

        if (PrefMethods.getLanguage() == "ar"){
            binding.orderNumber.gravity = Gravity.START
        }

        binding.orderNumber.text = orderNumber

        binding.btnHome.setOnClickListener {
            startActivity(Intent(requireActivity(), HomeActivity::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}