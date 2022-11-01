package com.brandsin.user.ui.main.order.confirmorder

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
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
import com.brandsin.user.databinding.HomeFragmentConfirmOrderBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Const
import com.brandsin.user.model.constants.Params
import com.brandsin.user.model.location.addresslist.AddressListItem
import com.brandsin.user.network.Status
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.ui.dialogs.timedialog.DialogOrderTimeFragment
import com.brandsin.user.ui.location.address.ListAddressesActivity
import com.brandsin.user.utils.PrefMethods
import com.brandsin.user.utils.Utils
import com.brandsin.user.utils.map.observe
import timber.log.Timber

class ConfirmOrderFragment : BaseHomeFragment(), Observer<Any?>, OnMapReadyCallback
{
    private lateinit var binding : HomeFragmentConfirmOrderBinding
    private lateinit var viewModel: ConfirmOrderViewModel
    private val cartArgs : ConfirmOrderFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment_confirm_order, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(ConfirmOrderViewModel::class.java)
        binding.viewModel = viewModel

        setBarName(getString(R.string.confirm_order))

        viewModel.userCart = cartArgs.cartData
        viewModel.obsItemsPrice.set(cartArgs.cartData.totalItemsPrices)
        viewModel.obsExtraFees.set(cartArgs.cartData.cartStoreData!!.extraFees)
        viewModel.obsTotalPrice.set(cartArgs.cartData.totalCartPrices)
        if (viewModel.obsDeliveryTime.get().isNullOrEmpty()){
        viewModel.obsDeliveryTime.set(
            "${getString(R.string.during)} " +
                    "${cartArgs.cartData.cartStoreData!!.deliveryTime}" +
                    " ${getString(R.string.minute)}")
        }
        getDefaultAddress()

        viewModel.mutableLiveData.observe(viewLifecycleOwner, this)
        viewModel.paymentWaysAdapter.paymentLiveData.observe(viewLifecycleOwner, this)

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
                else -> {
                    Timber.e(it.message)
                }
            }
        }

        binding.rawPaymentCash.visibility = View.VISIBLE
        binding.rawPaymentVisa.visibility = View.GONE
        binding.tvPaymentDesc.text = requireActivity().getString(R.string.qr_desc)
        viewModel.obsPaymentMethod.set("cash")
        when {
            PrefMethods.getLanguage() == "ar" -> {
                binding.rawPaymentCash.background = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_cash_border)
            }
            else -> {
                binding.rawPaymentCash.background = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_qr_border)
            }
        }
    }

    override fun onChanged(it: Any?) {
        if(it == null) return
        when (it)
        {
            Codes.EMPTY_TIME -> {
                showToast(getString(R.string.please_select_delivery_time) , 1)
            }
            Codes.EMPTY_PAYMENT_METHOD -> {
                showToast(getString(R.string.please_select_payment_method) , 1)
            }
            Codes.EMPTY_LOCATION -> {
                showToast(getString(R.string.please_select_your_location) , 1)
            }
            Codes.CHANGE_LOCATION -> {
                startActivityForResult((Intent(requireActivity(), ListAddressesActivity::class.java))
                    .putExtra(Params.DELIVERY_ADDRESSES_FLAG , 2), Codes.SHOW_DELIVERY_ADDRESSES_CODE)
            }
            Codes.NO_DEFAULT_LOCATION -> {
                startActivityForResult((Intent(requireActivity(), ListAddressesActivity::class.java))
                    .putExtra(Params.DELIVERY_ADDRESSES_FLAG , 2), Codes.SHOW_DELIVERY_ADDRESSES_CODE)
            }
            Codes.SELECT_TIME_CLICKED -> {
                val bundle = Bundle()
                bundle.putSerializable(Params.STORE_TIMES_RESPONSE, viewModel.storeTimesResponse)
                Utils.startDialogActivity(requireActivity(), DialogOrderTimeFragment::class.java.name, Codes.DIALOG_ORDER_TIME, bundle)
            }
            Codes.EMPTY_COUPON -> {
                showToast(getString(R.string.please_enter_coupon_code) , 1)
            }
            Codes.CONFIRM_ORDER -> {
                val action = ConfirmOrderFragmentDirections
                    .confirmOrderToReview(viewModel.obsDeliveryTime.get().toString(),viewModel.orderRequestParcelableClass)
                findNavController().navigate(action)
            }
            is PaymentWayItem -> {
                when (it.id) {
                    1 -> {
                        binding.rawPaymentCash.visibility = View.GONE
                        binding.rawPaymentVisa.visibility = View.VISIBLE
                        binding.tvPaymentDesc.text = requireActivity().getString(R.string.cash_desc)
                        viewModel.obsPaymentMethod.set("visa")
                        when {
                            PrefMethods.getLanguage() == "ar" -> {
                                binding.rawPaymentVisa.background = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_visa_border)
                            }
                            else -> {
                                binding.rawPaymentVisa.background = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_wallet_border)
                            }
                        }
                    }
                    2 -> {
                        binding.rawPaymentCash.visibility = View.VISIBLE
                        binding.rawPaymentVisa.visibility = View.GONE
                        binding.tvPaymentDesc.text = requireActivity().getString(R.string.qr_desc)
                        viewModel.obsPaymentMethod.set("cash")
                        when {
                            PrefMethods.getLanguage() == "ar" -> {
                                binding.rawPaymentCash.background = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_cash_border)
                            }
                            else -> {
                                binding.rawPaymentCash.background = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_qr_border)
                            }
                        }
                    }
                    3 -> {
                        binding.rawPaymentCash.visibility = View.VISIBLE
                        binding.rawPaymentVisa.visibility = View.GONE
                        binding.tvPaymentDesc.text = requireActivity().getString(R.string.qr_desc)
                        viewModel.obsPaymentMethod.set("qr code")
                        when {
                            PrefMethods.getLanguage() == "ar" -> {
                                binding.rawPaymentCash.background = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_qr_border)
                            }
                            else -> {
                                binding.rawPaymentCash.background = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_cash_border)
                            }
                        }
                    }
                    4 -> {
                        binding.rawPaymentCash.visibility = View.VISIBLE
                        binding.rawPaymentVisa.visibility = View.GONE
                        binding.tvPaymentDesc.text = requireActivity().getString(R.string.qr_desc)
                        viewModel.obsPaymentMethod.set("wallet")
                        when {
                            PrefMethods.getLanguage() == "ar" -> {
                                binding.rawPaymentCash.background = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_wallet_border)
                            }
                            else -> {
                                binding.rawPaymentCash.background = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_visa_border)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        /* When This page opened navigate user to select his default address from delivery addresses list */
        when (requestCode) {
            Codes.SHOW_DELIVERY_ADDRESSES_CODE -> {
                getDefaultAddress()
                when (data) {
                    null -> {
//                        when {
//                            viewModel.userAddressItem.lat == null || viewModel.userAddressItem.lng == null -> {
//                                findNavController().navigateUp()
//                            }
//                        }
                    }
                    else -> {
                        when {
                            data.hasExtra(Params.ADDRESS_ITEM) -> {
                                val addressItem = data.getParcelableExtra<AddressListItem>(Params.ADDRESS_ITEM)
                                when {
                                    addressItem != null -> {
                                        viewModel.userAddressItem = addressItem
                                        viewModel.isMapReady.value = true
                                        viewModel.obsAddressTitle.set((addressItem.typeLabel))
                                        viewModel.obsAddressDesc.set("${addressItem.streetName} ${addressItem.landmark}")
                                        viewModel.obsPhoneNumber.set("""${getString(R.string.phone_number)}: ${addressItem.phoneNumber}""")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        if (requestCode == Codes.DIALOG_ORDER_TIME && data != null) {
            if (data.hasExtra(Params.DIALOG_CLICK_ACTION)) {
                when {
                    data.getIntExtra(Params.DIALOG_CLICK_ACTION,1) == 1 -> {
                        when {
                            data.hasExtra(Params.DELIVERY_TIME) -> {
                                viewModel.obsDeliveryTime.set(data.getStringExtra(Params.DELIVERY_TIME))
                                viewModel.orderRequestParcelableClass.deliveryTime = data.getStringExtra(Params.DELIVERY_TIME)
                                viewModel.isTimeChanged = true
                            }
                        }
                    }
                    data.getIntExtra(Params.DIALOG_CLICK_ACTION,1) == 0 -> {
                        viewModel.obsDeliveryTime.set("${getString(R.string.during)} ${viewModel.userCart.cartStoreData!!.deliveryTime} ${getString(R.string.minute)}")
                        viewModel.orderRequestParcelableClass.deliveryTime = null
                        viewModel.isTimeChanged = false
                    }
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {

        observe(viewModel.isMapReady) {
            when (it) {
                true -> {
                    googleMap!!.clear()
                    val latLng = LatLng(viewModel.userAddressItem.lat!!.toDouble(), viewModel.userAddressItem.lng!!.toDouble())
                    val option = MarkerOptions().position(latLng)
                    googleMap.addMarker(option)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Const.zoomLevel))
                }
                else -> {}
            }
        }

        when {
            PrefMethods.getUserLocation()!!.lat != null && PrefMethods.getUserLocation()!!.lng != null -> {
                googleMap!!.clear()
                val latLng = LatLng(PrefMethods.getUserLocation()!!.lat!!.toDouble(), PrefMethods.getUserLocation()!!.lng!!.toDouble())
                val option = MarkerOptions().position(latLng)
                googleMap.addMarker(option)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Const.zoomLevel))
            }
        }
    }
    fun getDefaultAddress(){
        when {
            PrefMethods.getDefaultAddress() == null -> {
                binding.addAddress.visibility = View.VISIBLE
                binding.locationLayout.visibility = View.GONE
            }
            else -> {
                if (PrefMethods.getDefaultAddress()!!.phoneNumber != null) {
                    binding.addAddress.visibility = View.GONE
                    binding.locationLayout.visibility = View.VISIBLE
                    viewModel.phoneNumber = PrefMethods.getDefaultAddress()?.phoneNumber.toString()
                    viewModel.obsPhoneNumber.set("""${getString(R.string.phone_number)}: ${viewModel.phoneNumber}""")
                    viewModel.obsAddressDesc.set(PrefMethods.getDefaultAddress()!!.streetName + " " + PrefMethods.getDefaultAddress()!!.landmark)
                    viewModel.obsAddressTitle.set(PrefMethods.getDefaultAddress()!!.type)
                    viewModel.isAddress.value = false
                    viewModel.userAddressItem.run {
                        id = PrefMethods.getDefaultAddress()?.id
                        lat = PrefMethods.getDefaultAddress()?.lat
                        lng = PrefMethods.getDefaultAddress()?.lng
                        type = PrefMethods.getDefaultAddress()?.type
                        typeLabel = PrefMethods.getDefaultAddress()?.typeLabel
                        phoneNumber = PrefMethods.getDefaultAddress()?.phoneNumber
                        streetName = PrefMethods.getDefaultAddress()?.streetName
                        cityId = PrefMethods.getDefaultAddress()?.cityId
                        countryId = PrefMethods.getDefaultAddress()?.countryId
                        countryName = PrefMethods.getDefaultAddress()?.countryName
                        code = PrefMethods.getDefaultAddress()?.code
                        lastName = PrefMethods.getDefaultAddress()?.lastName
                        isDefault = PrefMethods.getDefaultAddress()?.isDefault
                        status = PrefMethods.getDefaultAddress()?.status
                        landmark = PrefMethods.getDefaultAddress()?.landmark
                    }
                }else{
                    binding.addAddress.visibility = View.VISIBLE
                    binding.locationLayout.visibility = View.GONE
                }
            }
        }
    }
}