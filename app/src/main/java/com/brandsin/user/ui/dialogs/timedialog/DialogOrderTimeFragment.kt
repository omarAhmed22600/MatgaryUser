package com.brandsin.user.ui.dialogs.timedialog

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.brandsin.user.databinding.DialogOrderTimeBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Params
import com.brandsin.user.model.order.storedetails.StoreTimeItem
import com.brandsin.user.model.order.storedetails.StoreTimesResponse
import com.brandsin.user.utils.wheelview.OrderTimeWheelView

class DialogOrderTimeFragment  : DialogFragment(), Observer<Any?>
{
    lateinit  var  binding: DialogOrderTimeBinding
    lateinit var viewModel : DateViewModel
    private lateinit var rvTimes: OrderTimeWheelView
    private var storeTimeList: ArrayList<StoreTimeItem> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        when {
            arguments != null -> {
                when {
                    requireArguments().containsKey(Params.STORE_TIMES_RESPONSE) -> {
                        val storeTimesResponse : StoreTimesResponse= (requireArguments().getSerializable(Params.STORE_TIMES_RESPONSE) as StoreTimesResponse?)!!
                        storeTimesResponse.let {
                            when {
                                it.storeTimesList!!.isNotEmpty() -> {
                                    storeTimeList = it.storeTimesList as ArrayList<StoreTimeItem>
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DialogOrderTimeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(DateViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.setDates(storeTimeList)

        viewModel.mutableLiveData.observe(viewLifecycleOwner, this)
        viewModel.datesAdapter.dateLiveData.observe(viewLifecycleOwner, this)

        rvTimes = binding.timeWheelView
        rvTimes.setOnSelectedStringCallback(object :
            OrderTimeWheelView.OnSelectedStringCallback {
            override fun onSelectedString(selectedString: String) {
                viewModel.selectedTime = selectedString
            }
        })
    }

    override fun onChanged(it: Any?)
    {
        if(it == null) return
        it.let {
            when (it) {
                is Int -> {
                    when (it) {
                        Codes.CONFIRM_CLICKED -> {
                            val intent = Intent()
                            intent.putExtra(Params.DIALOG_CLICK_ACTION, 1)
                            intent.putExtra(Params.DELIVERY_TIME, viewModel.selectedDate + " " + viewModel.selectedTime)
                            requireActivity().setResult(Codes.DIALOG_ORDER_TIME, intent)
                            requireActivity().finish()
                        }
                        Codes.CANCEL_CLICKED -> {
                            val intent = Intent()
                            intent.putExtra(Params.DIALOG_CLICK_ACTION, 0)
                            requireActivity().setResult(Codes.DIALOG_ORDER_TIME, intent)
                            requireActivity().finish()
                        }
                    }
                }
                is StoreTimeItem -> {
                    rvTimes.setStringItemList(it.storeTime as ArrayList<String>)
                    viewModel.selectedDate = it.storeDate.toString()
                }
            }
        }
    }
}