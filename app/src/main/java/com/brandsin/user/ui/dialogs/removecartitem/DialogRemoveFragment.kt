package com.brandsin.user.ui.dialogs.removecartitem

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.brandsin.user.databinding.DialogConfirmBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Params
import com.brandsin.user.model.order.cart.CartItem
import com.brandsin.user.model.order.myorders.MyOrderItem

class DialogRemoveFragment  : DialogFragment()
{
    lateinit  var  binding: DialogConfirmBinding
    var message : String = ""
    var positiveBtn : String = ""
    var negativeBtn : String = ""
    private var cartItem = CartItem()
    private var orderItem = MyOrderItem()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        if (arguments != null)
        {
            if (requireArguments().containsKey(Params.DIALOG_CONFIRM_MESSAGE))
            {
                message = requireArguments().getString(Params.DIALOG_CONFIRM_MESSAGE, null)
                positiveBtn = requireArguments().getString(Params.DIALOG_CONFIRM_POSITIVE, null)
                negativeBtn = requireArguments().getString(Params.DIALOG_CONFIRM_NEGATIVE, null)
                when {
                    requireArguments().containsKey(Params.DIALOG_CART_ITEM) -> {
                        cartItem = (requireArguments().getSerializable(Params.DIALOG_CART_ITEM) as CartItem?)!!
                    }
                }
                when {
                    requireArguments().containsKey(Params.DIALOG_ORDER_ITEM) -> {
                        orderItem = (requireArguments().getSerializable(Params.DIALOG_ORDER_ITEM) as MyOrderItem?)!!
                    }
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = DialogConfirmBinding.inflate(inflater, container, false)

        binding.tvMessage.text = message
        binding.btnConfirm.text = positiveBtn
        binding.btnIgnore.text = negativeBtn

        binding.btnIgnore.setOnClickListener {
            val intent = Intent()
            intent.putExtra(Params.DIALOG_CLICK_ACTION, 0)
            requireActivity().setResult(Codes.DIALOG_CONFIRM_REQUEST, intent)
            requireActivity().finish()
        }

        binding.btnConfirm.setOnClickListener {
            val intent = Intent()
            intent.putExtra(Params.DIALOG_CLICK_ACTION, 1)

            when {
                cartItem.productName != null -> {
                    intent.putExtra(Params.DIALOG_CART_ITEM, cartItem)
                }
            }

            when {
                orderItem.id != null -> {
                    intent.putExtra(Params.DIALOG_ORDER_ITEM, orderItem)
                }
            }


            requireActivity().setResult(Codes.DIALOG_CONFIRM_REQUEST, intent)
            requireActivity().finish()
        }
        return binding.root
    }
}