package com.brandsin.user.ui.dialogs.sendcode

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.brandsin.user.R
import com.brandsin.user.databinding.DialogSendCodeBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Params
import com.brandsin.user.model.order.confirmorder.verifyphome.SendCodeResponse
import com.brandsin.user.network.Status
import com.brandsin.user.ui.dialogs.toast.DialogToastFragment
import com.brandsin.user.utils.Utils
import com.brandsin.user.utils.map.observe
import timber.log.Timber

class DialogSendCodeFragment  : DialogFragment(), Observer<Any?>
{
    lateinit  var  binding: DialogSendCodeBinding
    lateinit var viewModel: DialogSendCodeViewModel
    lateinit var phoneNumber : String
    var addressId : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when {
            arguments != null -> {
                when {
                    requireArguments().containsKey(Params.VERIFIED_PHONE) -> {
                        phoneNumber = requireArguments().getString(Params.VERIFIED_PHONE, null)
                        addressId = requireArguments().getInt(Params.ADDRESS_ID, 0)
                    }
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_send_code, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(DialogSendCodeViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.obsPhoneNumber.set(phoneNumber)

        viewModel.request.addressId = addressId

        viewModel.mutableLiveData.observe(viewLifecycleOwner, this)

        observe(viewModel.apiResponseLiveData) {
            when (it.status) {
                Status.ERROR_MESSAGE -> {
                    showToast(it.message.toString(), 1)
                }
                Status.SUCCESS_MESSAGE -> {
                    showToast(it.message.toString(), 2)
                }
                Status.SUCCESS -> {
                    when (it.data) {
                        is SendCodeResponse -> {
                            val codeData = it.data
                            val intent = Intent()
                            intent.putExtra(Params.DIALOG_CLICK_ACTION, 1)
                            intent.putExtra(Params.VERIFIED_PHONE, phoneNumber)
                            intent.putExtra(Params.VERIFIED_CODE, codeData.code.toString())
                            intent.putExtra(Params.ADDRESS_ID, addressId)
                            requireActivity().setResult(Codes.DIALOG_SEND_CODE_REQUEST, intent)
                            requireActivity().finish()
                        }
                    }
                }
                else -> {
                    Timber.e(it.message)
                }
            }
        }
    }

    override fun onChanged(t: Any?)
    {
        if (t == null) return
        t.let {
            if (t is Int)
            {
                when (t)
                {
                    Codes.CANCEL_CLICKED ->
                    {
                        val intent = Intent()
                        intent.putExtra(Params.DIALOG_CLICK_ACTION, 0)
                        requireActivity().setResult(Codes.DIALOG_SEND_CODE_REQUEST, intent)
                        requireActivity().finish()
                    }
                }
            }
        }
    }

    fun showToast(msg : String, type : Int) {
        val bundle = Bundle()
        bundle.putString(Params.DIALOG_TOAST_MESSAGE, msg)
        bundle.putInt(Params.DIALOG_TOAST_TYPE, type)
        Utils.startDialogActivity(requireActivity(), DialogToastFragment::class.java.name, Codes.DIALOG_TOAST_REQUEST, bundle)
    }
}