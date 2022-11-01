package com.brandsin.user.ui.dialogs.confirm

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.brandsin.user.databinding.DialogConfirmBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Params
import com.brandsin.user.model.location.addresslist.AddressListItem
import com.brandsin.user.model.order.cart.CartParcelableClass

class DialogConfirmFragment  : DialogFragment()
{
    lateinit  var  binding: DialogConfirmBinding
    var message : String = ""
    var positiveBtn : String = ""
    var negativeBtn : String = ""
    var cartParcelableClass = CartParcelableClass()
    var hasCartItem : Boolean = false
    private var addressItem = AddressListItem()

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
                    requireArguments().containsKey(Params.DIALOG_STORE_ITEM) -> {
                        cartParcelableClass = (requireArguments().getSerializable(Params.DIALOG_STORE_ITEM) as CartParcelableClass?)!!
                        hasCartItem = true
                    }
                }
                when {
                    requireArguments().containsKey(Params.DIALOG_ADDRESS_ITEM) -> {
                        addressItem = requireArguments().getParcelable(Params.DIALOG_ADDRESS_ITEM)!!
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
                hasCartItem -> {
                    when {
                        cartParcelableClass.storeProductItem!!.id != null -> {
                            intent.putExtra(Params.DIALOG_STORE_ITEM, cartParcelableClass)
                        }
                    }
                }
            }

            when {
                addressItem.streetName != null -> {
                    intent.putExtra(Params.DIALOG_ADDRESS_ITEM, addressItem)
                }
            }

            requireActivity().setResult(Codes.DIALOG_CONFIRM_REQUEST, intent)
            requireActivity().finish()
        }
        return binding.root
    }
}