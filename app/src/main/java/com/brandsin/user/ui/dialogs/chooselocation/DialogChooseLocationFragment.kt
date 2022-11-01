package com.brandsin.user.ui.dialogs.chooselocation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.brandsin.user.databinding.DialogChooseLocationBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Params

class DialogChooseLocationFragment : DialogFragment()
    {
        lateinit  var  binding: DialogChooseLocationBinding

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?,
        ): View {
            binding = DialogChooseLocationBinding.inflate(inflater, container, false)
            return binding.root
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?)
        {
            super.onViewCreated(view, savedInstanceState)

            binding.consYourLocation.setOnClickListener {
                val intent = Intent()
                intent.putExtra(Params.DIALOG_CLICK_ACTION, 0)
                requireActivity().setResult(Codes.DIALOG_ORDER_SUCCESS, intent)
                requireActivity().finish()
            }

            binding.consYourAddress.setOnClickListener {
                val intent = Intent()
                intent.putExtra(Params.DIALOG_CLICK_ACTION, 1)
                requireActivity().setResult(Codes.DIALOG_ORDER_SUCCESS, intent)
                requireActivity().finish()
            }
        }
    }