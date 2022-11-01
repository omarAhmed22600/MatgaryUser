package com.brandsin.user.ui.dialogs.successorder

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.brandsin.user.databinding.DialogOrderSuccessBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Params

class DialogOrderSuccessFragment : DialogFragment()
{
    private lateinit var binding: DialogOrderSuccessBinding
    var message = ""
    var orderId  = 0

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        if (arguments != null)
        {
            if (requireArguments().containsKey(Params.ORDER_ID))
            {
                message = requireArguments().getString(Params.DIALOG_ORDER_SUCCESS, "")
                orderId = requireArguments().getInt(Params.ORDER_ID, 0)
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View
    {
        binding = DialogOrderSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        binding.btnHome.setOnClickListener {
            val intent = Intent()
            intent.putExtra(Params.DIALOG_CLICK_ACTION, 1)
            requireActivity().setResult(Codes.DIALOG_ORDER_SUCCESS, intent)
            requireActivity().finish()
        }

        binding.btnOrderStatus.setOnClickListener {
            val intent = Intent()
            intent.putExtra(Params.DIALOG_CLICK_ACTION, 2)
            when {
                orderId != 0 -> {
                    intent.putExtra(Params.ORDER_ID, orderId)
                }
            }
            requireActivity().setResult(Codes.DIALOG_ORDER_SUCCESS, intent)
            requireActivity().finish()
        }
    }
}
