package com.brandsin.user.ui.dialogs.notlogin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.brandsin.user.databinding.DialogNotLoginBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Params
import com.brandsin.user.ui.activity.BaseFragment

class DialogNotLoginFragment  : BaseFragment()
{
    lateinit  var  binding: DialogNotLoginBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = DialogNotLoginBinding.inflate(inflater, container, false)

        binding.btnIgonre.setOnClickListener {
            val intent = Intent()
            intent.putExtra(Params.DIALOG_CLICK_ACTION, 0)
            requireActivity().setResult(Codes.DIALOG_CONFIRM_REQUEST, intent)
            requireActivity().finish()
        }

        binding.btnConfirm.setOnClickListener {
            val intent = Intent()
            intent.putExtra(Params.DIALOG_CLICK_ACTION, 1)
            requireActivity().setResult(Codes.DIALOG_CONFIRM_REQUEST, intent)
            requireActivity().finish()
        }
        return binding.root
    }
}