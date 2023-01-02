package com.brandsin.user.ui.main.order.orderreview

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.brandsin.user.R
import com.brandsin.user.databinding.HomeFragmentOrderReviewBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Const
import com.brandsin.user.model.constants.Params
import com.brandsin.user.model.order.cart.CartItem
import com.brandsin.user.model.order.confirmorder.createorder.CreateOrderResponse
import com.brandsin.user.network.Status
import com.brandsin.user.ui.MFPayment.MFPaymentActivity
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.ui.activity.home.HomeActivity
import com.brandsin.user.ui.dialogs.sendcode.DialogSendCodeFragment
import com.brandsin.user.ui.dialogs.successorder.DialogOrderSuccessFragment
import com.brandsin.user.ui.dialogs.verifycode.DialogVerifyCodeFragment
import com.brandsin.user.utils.PrefMethods
import com.brandsin.user.utils.Utils
import com.brandsin.user.utils.map.observe
import timber.log.Timber

@Suppress("DEPRECATION")
class OrderReviewFragment : BaseHomeFragment(), Observer<Any?>, OnMapReadyCallback
{
    private lateinit var binding : HomeFragmentOrderReviewBinding
    private lateinit var viewModel: OrderReviewViewModel
    private val orderArgs : OrderReviewFragmentArgs by navArgs()
    var deliveryTime = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment_order_review, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(OrderReviewViewModel::class.java)
        binding.viewModel = viewModel
        viewModel.mutableLiveData.observe(viewLifecycleOwner, this)

        setBarName(requireActivity().getString(R.string.order_review))

        deliveryTime = orderArgs.time
        val orderData = orderArgs.orderData
        viewModel.orderData = orderData
        viewModel.obsDeliveryTime.set(orderData.deliveryTime)
        viewModel.obsPaymentMethod.set(orderData.paymentMethod)
        viewModel.obsOrderAmount.set(orderData.orderCost.toString())
        viewModel.orderItemsAdapter.updateList(orderData.orderItems as ArrayList<CartItem>)
        viewModel.isTimeChanged = orderData.isTimeChanged
        viewModel.isMapReady.value = true
        when (orderData.addressStatus) {
            1 -> {
                viewModel.isVerified = true
            }
        }

        if (viewModel.obsDeliveryTime.get().isNullOrEmpty()){
            binding.tvTime.text = deliveryTime
        }else{
            binding.tvTime.text = viewModel.obsDeliveryTime.get()
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

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
                        is CreateOrderResponse -> {
                            val bundle = Bundle()
                            bundle.putInt(Params.ORDER_ID, it.data.orders!![0]!!.id!!)
                            Utils.startDialogActivity(requireActivity(),
                                DialogOrderSuccessFragment::class.java.name,
                                Codes.DIALOG_ORDER_SUCCESS,
                                bundle)
                        }
                        is Int -> {
                            if (it.data == 1) {
                                findNavController().navigate(R.id.review_to_my_orders)
                            }
                        }
                    }
                }
                else -> {
                    Timber.e(it.message)
                }
            }
        }

        binding.btnConfirm.setOnClickListener {
            when {
                !viewModel.isVerified -> {
                    viewModel.setValue(Codes.VERIFY_PHONE)
                }
                else -> {
                    if (viewModel.obsPaymentMethod.get().equals(getString(R.string.visa))||
                        viewModel.obsPaymentMethod.get().equals(getString(R.string.visa))) {
                        val intent = Intent(requireActivity(), MFPaymentActivity::class.java)
                        intent.putExtra("amountMF",viewModel.orderData.orderCost)
                        startActivityForResult(intent, 3)
                    }else if(viewModel.obsPaymentMethod.get().equals(getString(R.string.cash))){
                        viewModel.createOrder(context)
                    }

                }
            }
        }
    }

    override fun onChanged(it: Any?)
    {
        when (it) {
            null -> return
            else -> when (it) {
                Codes.VERIFY_PHONE -> {
                    val bundle = Bundle()
                    bundle.putString(Params.VERIFIED_PHONE, viewModel.orderData.phoneNumber)
                    bundle.putInt(Params.ADDRESS_ID, viewModel.orderData.addressId!!)
                    Utils.startDialogActivity(requireActivity(),
                        DialogSendCodeFragment::class.java.name,
                        Codes.DIALOG_SEND_CODE_REQUEST,
                        bundle)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        /* Callback from dialog order success */
        when {
            requestCode == Codes.DIALOG_ORDER_SUCCESS && data != null -> {
                when {
                    data.hasExtra(Params.DIALOG_CLICK_ACTION) -> {
                        when {
                            data.getIntExtra(Params.DIALOG_CLICK_ACTION, 1) == 1 -> {
                                startActivity(Intent(requireActivity(), HomeActivity::class.java))
                                requireActivity().finishAffinity()
                            }
                            data.getIntExtra(Params.DIALOG_CLICK_ACTION, 1) == 2 -> {
                                when {
                                    data.getIntExtra(Params.ORDER_ID, 0) != -1 -> {
                                       val action = OrderReviewFragmentDirections.reviewToDetails(
                                           data.getIntExtra(
                                               Params.ORDER_ID,
                                               0))
                                        findNavController().navigate(action)
                                    }
                                    else -> {
                                        startActivity(Intent(requireActivity(),
                                            HomeActivity::class.java))
                                        requireActivity().finishAffinity()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        /* Callback from dialog send code */
        when {
            requestCode == Codes.DIALOG_SEND_CODE_REQUEST && data != null -> {
                when {
                    data.hasExtra(Params.DIALOG_CLICK_ACTION) -> {
                        when {
                            data.getIntExtra(Params.DIALOG_CLICK_ACTION, 1) == 1 -> {
                                val bundle = Bundle()
                                when {
                                    data.getStringExtra(Params.VERIFIED_PHONE) != null -> {
                                        bundle.putString(Params.VERIFIED_PHONE, data.getStringExtra(
                                            Params.VERIFIED_PHONE))
                                    }
                                    else -> {
                                        bundle.putString(Params.VERIFIED_PHONE,
                                            viewModel.orderData.phoneNumber)
                                    }
                                }
                                when {
                                    data.getStringExtra(Params.VERIFIED_CODE) != null -> {
                                        bundle.putString(Params.VERIFIED_PHONE, data.getStringExtra(
                                            Params.VERIFIED_PHONE))
                                        showToast(data.getStringExtra(Params.VERIFIED_CODE)!!, 2)
                                    }
                                }
                                bundle.putInt(Params.ADDRESS_ID, data.getIntExtra(Params.ADDRESS_ID,
                                    0))
                                Utils.startDialogActivity(requireActivity(),
                                    DialogVerifyCodeFragment::class.java.name,
                                    Codes.DIALOG_VERIFY_CODE_REQUEST,
                                    bundle)
                            }
                        }
                    }
                }
            }
        }

        /* Callback from verify code*/
        when {
            requestCode == Codes.DIALOG_VERIFY_CODE_REQUEST && data != null -> {
                when {
                    data.hasExtra(Params.DIALOG_CLICK_ACTION) -> {
                        when {
                            data.getIntExtra(Params.DIALOG_CLICK_ACTION, 1) == 1 -> {
                                val addressItem = PrefMethods.getDefaultAddress()
                                addressItem!!.status = 1
                                PrefMethods.saveDefaultAddress(addressItem)
                                viewModel.isVerified = true
                                viewModel.createOrder(context)
                            }
                        }
                    }
                }
            }
        }

        if (data != null) {

            when (requestCode) {
                3 -> {
                    val success = data!!.getBooleanExtra("success", false)
                    if (success)
                        viewModel.createOrder(context)
                }
            }

        }
    }

    override fun onMapReady(googleMap: GoogleMap?)
    {
        observe(viewModel.isMapReady) {
            when (it) {
                true -> {
                    googleMap!!.clear()
                    val latLng = LatLng(viewModel.orderData.lat!!.toDouble(),
                        viewModel.orderData.lng!!.toDouble())
                    val option = MarkerOptions().position(latLng)
                    googleMap.addMarker(option)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Const.zoomLevel))
                }
                else -> {}
            }
        }
    }
}