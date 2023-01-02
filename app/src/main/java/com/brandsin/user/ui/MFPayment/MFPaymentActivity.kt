package com.brandsin.user.ui.MFPayment

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil

import com.brandsin.user.R
import com.brandsin.user.databinding.ActivityMfpaymentBinding
import com.brandsin.user.utils.Config
import com.google.gson.Gson
import com.myfatoorah.sdk.entity.executepayment.MFExecutePaymentRequest
import com.myfatoorah.sdk.entity.executepayment_cardinfo.MFCardInfo
import com.myfatoorah.sdk.entity.executepayment_cardinfo.MFDirectPaymentResponse
import com.myfatoorah.sdk.entity.initiatepayment.MFInitiatePaymentRequest
import com.myfatoorah.sdk.entity.initiatepayment.MFInitiatePaymentResponse
import com.myfatoorah.sdk.entity.initiatepayment.PaymentMethod
import com.myfatoorah.sdk.entity.paymentstatus.MFGetPaymentStatusResponse
import com.myfatoorah.sdk.utils.MFAPILanguage
import com.myfatoorah.sdk.utils.MFCurrencyISO
import com.myfatoorah.sdk.views.MFResult
import com.myfatoorah.sdk.views.MFSDK
import java.util.ArrayList

class MFPaymentActivity : AppCompatActivity(),OnListFragmentInteractionListener {
    lateinit var binding : ActivityMfpaymentBinding
    private lateinit var adapter: MFRecyclerViewAdapter
    private var selectedPaymentMethod: PaymentMethod? = null

    override fun onListFragmentInteraction(position: Int, paymentMethod: PaymentMethod) {

        Log.d("pay25",paymentMethod.toString())
        selectedPaymentMethod = paymentMethod
        adapter.changeSelected(position)

        if (paymentMethod.directPayment)
            binding.llDirectPaymentLayout.visibility = View.VISIBLE
        else
            binding.llDirectPaymentLayout.visibility = View.GONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mfpayment)

        val AmountMF: Double = intent.getDoubleExtra("amountMF",0.0)

        if (Config.API_KEY.isEmpty()) {
            showAlertDialog("Missing API Key.. You can get it from here: https://myfatoorah.readme.io/docs/demo-information")
            return
        }
        // You can custom your action bar, but this is optional not required to set this line

        // You can custom your action bar, but this is optional not required to set this line
        MFSDK.setUpActionBar(
            "MyFatoorah Payment",
            R.color.white,
            com.myfatoorah.sdk.R.color.material_blue_grey_800,
            true)

        binding.btPay.setOnClickListener {
            if (selectedPaymentMethod == null) {
                showAlertDialog("Please select payment method first")
            }
            if (!selectedPaymentMethod?.directPayment!!)
                executePayment(selectedPaymentMethod?.paymentMethodId!!,AmountMF)
            else {
                when {
                    binding.etCardNumber.text!!.isEmpty() -> binding.etCardNumber.error = "Required"
                    binding.etExpiryMonth.text!!.isEmpty() -> binding.etExpiryMonth.error = "Required"
                    binding.etExpiryYear.text!!.isEmpty() -> binding.etExpiryYear.error = "Required"
                    binding.etSecurityCode.text!!.isEmpty() -> binding.etSecurityCode.error = "Required"
                    binding.etCardHolderName.text!!.isEmpty() -> binding.etCardHolderName.error = "Required"
                    else -> executePaymentWithCardInfo(selectedPaymentMethod?.paymentMethodId!!,AmountMF)
                }
            }
        }

        initiatePayment(AmountMF)

    }
    private fun executePayment(paymentMethod: Int,amountMF:Double){
        val request = MFExecutePaymentRequest(paymentMethod,amountMF)
        request.displayCurrencyIso= MFCurrencyISO.SAUDI_ARABIA_SAR
        MFSDK.executePayment(
            this,
            request,
            MFAPILanguage.EN,
            onInvoiceCreated = {
                Log.d("TAG", "invoiceId: $it")
            }
        ) { invoiceId: String, result: MFResult<MFGetPaymentStatusResponse> ->
            when (result) {
                is MFResult.Success ->{

                    Log.d("TAG", "Response: result.response.invoiceStatus"+result.response.invoiceStatus+"---" + Gson().toJson(result.response))

                    //showAlertDialog("Payment done successfully")
                    intent.putExtra("success",result.response.invoiceStatus.equals("Paid"))
                    setResult(3,intent)
                    finish()
                }

                is MFResult.Fail -> {
                    Log.d("TAG", "Fail: " + Gson().toJson(result.error))
                }

                else -> {}
            }
        }
    }

    private fun executePaymentWithCardInfo(paymentMethod: Int,amountMF:Double) {

        val request = MFExecutePaymentRequest(paymentMethod, amountMF)

//        val mfCardInfo = MFCardInfo("Your token here")
        val mfCardInfo = MFCardInfo(
            binding.etCardNumber.text.toString(),
            binding.etExpiryMonth.text.toString(),
            binding.etExpiryYear.text.toString(),
            binding.etSecurityCode.text.toString(),
            binding.etCardHolderName.text.toString(),
            false,
            false
        )

        MFSDK.executeDirectPayment(
            this,
            request,
            mfCardInfo,
            MFAPILanguage.EN,
            onInvoiceCreated = {
                Log.d("TAG", "invoiceId: $it")
            }
        ) { invoiceId: String, result: MFResult<MFDirectPaymentResponse> ->
            when (result) {
                is MFResult.Success -> {
                    Log.d("TAG", "Response: " + Gson().toJson(result.response))
                    //showAlertDialog("Payment done successfully")
//                    intent.putExtra("success",true)
//                    setResult(3,intent)
//                    finish()
                }
                is MFResult.Fail -> {
                    Log.d("TAG", "Fail: " + Gson().toJson(result.error))
                }
                else -> {}
            }
        }
    }

    private fun initiatePayment(amountMF: Double){
        val request = MFInitiatePaymentRequest(amountMF, MFCurrencyISO.SAUDI_ARABIA_SAR)
        MFSDK.initiatePayment(
            request,
            MFAPILanguage.EN
        ) { result: MFResult<MFInitiatePaymentResponse> ->
            when (result) {
                is MFResult.Success -> {
                    Log.d("TAG25", "Response: " + Gson().toJson(result.response))
                    setAvailablePayments(result.response.paymentMethods!!)
                }
                is MFResult.Fail ->
                    Log.d("TAG26", "Fail: " + Gson().toJson(result.error))
                else -> {}
            }
        }
    }

    private fun setAvailablePayments(paymentMethods: ArrayList<PaymentMethod>) {
        adapter = MFRecyclerViewAdapter(paymentMethods, this)
        binding.rvPaymentMethod.adapter = adapter
    }


    private fun showAlertDialog(text: String) {
        AlertDialog.Builder(this)
            .setMessage(text)
            .setPositiveButton(
                android.R.string.ok
            ) { dialog: DialogInterface?, which: Int -> }
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }


}