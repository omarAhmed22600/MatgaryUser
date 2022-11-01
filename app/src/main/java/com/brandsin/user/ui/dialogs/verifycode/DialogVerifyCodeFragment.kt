package com.brandsin.user.ui.dialogs.verifycode

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.brandsin.user.R
import com.brandsin.user.databinding.DialogVerifyCodeBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Params
import com.brandsin.user.model.order.confirmorder.verifyphome.VerifyCodeResponse
import com.brandsin.user.network.Status
import com.brandsin.user.utils.map.observe
import es.dmoral.toasty.Toasty
import timber.log.Timber

class DialogVerifyCodeFragment  : DialogFragment(), Observer<Any?>
{
    lateinit  var  binding: DialogVerifyCodeBinding
    lateinit var viewModel: DialogVerifyCodeViewModel
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
        binding = DialogVerifyCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(DialogVerifyCodeViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.request.addressId = addressId
        viewModel.obsPhoneNumber.set(phoneNumber)

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
                        is VerifyCodeResponse -> {
                            val intent = Intent()
                            intent.putExtra(Params.DIALOG_CLICK_ACTION, 1)
                            requireActivity().setResult(Codes.DIALOG_VERIFY_CODE_REQUEST, intent)
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
        when (t) {
            null -> return
            else -> t.let {
                when (t) {
                    Codes.CODE_EMPTY -> {
                        showToast(getString(R.string.enter_verification_code), 1)
                    }
                    Codes.CODE_SHORT -> {
                        showToast(getString(R.string.short_code), 1)
                    }
                    Codes.CANCEL_CLICKED -> {
                        val intent = Intent()
                        intent.putExtra(Params.DIALOG_CLICK_ACTION, 0)
                        requireActivity().setResult(Codes.DIALOG_VERIFY_CODE_REQUEST, intent)
                        requireActivity().finish()
                    }
                }
            }
        }
    }

    fun showToast(msg : String, type : Int) {
        Toasty.success(requireActivity(), msg).show()
/*        val bundle = Bundle()
        bundle.putString(Params.DIALOG_TOAST_MESSAGE, msg)
        bundle.putInt(Params.DIALOG_TOAST_TYPE, type)
        Utils.startDialogActivity(requireActivity(), DialogToastFragment::class.java.name, Codes.DIALOG_TOAST_REQUEST, bundle)*/
    }
}