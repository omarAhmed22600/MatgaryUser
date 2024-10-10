package com.brandsin.user.ui.location.changeaddress

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.brandsin.user.R
import com.brandsin.user.databinding.ActivityChangeAddressBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Const
import com.brandsin.user.model.constants.Params
import com.brandsin.user.model.location.UserLocation
import com.brandsin.user.model.location.addresslist.AddressListItem
import com.brandsin.user.network.Status
import com.brandsin.user.ui.dialogs.toast.DialogToastFragment
import com.brandsin.user.ui.location.map.MapsActivity
import com.brandsin.user.utils.PrefMethods
import com.brandsin.user.utils.Utils
import com.brandsin.user.utils.map.observe
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import timber.log.Timber

class ChangeAddressActivity : AppCompatActivity(), Observer<Any?>, OnMapReadyCallback {
    lateinit var binding: ActivityChangeAddressBinding
    lateinit var viewModel: ChangeAddressViewModel
    private lateinit var addressItem: AddressListItem
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_address)
        viewModel = ViewModelProvider(this).get(ChangeAddressViewModel::class.java)
        binding.viewModel = viewModel

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        when {
            intent.hasExtra(Params.ADDRESS_ITEM) -> {
                when {
                    intent.getParcelableExtra<AddressListItem>(Params.ADDRESS_ITEM) != null -> {
                        addressItem = intent.extras!!.getParcelable(Params.ADDRESS_ITEM)!!
                        viewModel.changeAddressRequest.addressId = addressItem.id
                        viewModel.changeAddressRequest.firstName = addressItem.firstName
                        viewModel.changeAddressRequest.lastName = addressItem.lastName
                        viewModel.changeAddressRequest.lat = addressItem.lat
                        viewModel.changeAddressRequest.lng = addressItem.lng
                        viewModel.changeAddressRequest.streetName = addressItem.streetName
                        viewModel.changeAddressRequest.stateId = addressItem.stateId
                        viewModel.changeAddressRequest.countryId = addressItem.countryId
                        viewModel.changeAddressRequest.cityId = addressItem.cityId
                        viewModel.changeAddressRequest.userId = PrefMethods.getUserData()!!.id
                        viewModel.changeAddressRequest.phoneNumber = addressItem.phoneNumber
                        viewModel.changeAddressRequest.lang = PrefMethods.getLanguage()
                        viewModel.changeAddressRequest.type = addressItem.type
                        viewModel.changeAddressRequest.landmark = addressItem.landmark
                        viewModel.changeAddressRequest.firstName = addressItem.firstName
                        viewModel.changeAddressRequest.lastName = addressItem.lastName
                        Timber.e("LaaandMark : " + addressItem.landmark)

                    }
                }
            }
        }

        binding.spinnerType.apply {
            setItems(viewModel.typesSpinnerLst)
            setOnItemSelectedListener { _, _, _, item ->
                viewModel.changeAddressRequest.type = item.toString()
            }
        }

        viewModel.mutableLiveData.observe(this, this)
        startMapForResult()

        observe(viewModel.apiResponseLiveData) {
            when (it.status) {
                Status.ERROR_MESSAGE -> {
                    showToast(it.message.toString(), 1)
                }

                else -> {
                    Timber.e(it.message)
                }
            }
        }

        observe(viewModel.isMapReady) {
            when (it) {
                true -> {
                    val latLng = LatLng(
                        viewModel.changeAddressRequest.lat!!.toDouble(),
                        viewModel.changeAddressRequest.lng!!.toDouble()
                    )
                    val option = MarkerOptions().position(latLng)
                    mMap.addMarker(option)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Const.zoomLevel))
                }

                else -> {}
            }
        }

        setBtnListener()
    }

    private fun setBtnListener() {
        binding.ibBack.setOnClickListener {
            finish()
        }
    }

    override fun onChanged(it: Any?) {
        if (it == null) return
        when (it) {
            Codes.EMPTY_FIRST_NAME -> {
                showToast(getString(R.string.please_enter_your_first_name), 1)
            }

            Codes.EMPTY_LAST_NAME -> {
                showToast(getString(R.string.please_enter_your_last_name), 1)
            }

            Codes.EMPTY_TYPE -> {
                showToast(getString(R.string.please_enter_your_place_type), 1)
            }

            Codes.EMPTY_streetName -> {
                showToast(getString(R.string.please_enter_your_streetName), 1)
            }

            Codes.EMPTY_addressName -> {
                showToast(getString(R.string.please_enter_your_addressName), 1)
            }

            Codes.EMPTY_PHONE -> {
                showToast(getString(R.string.empty_phone), 1)
            }

            Codes.INVALID_PHONE -> {
                showToast(getString(R.string.invalid_phone), 1)
            }

            Codes.CHANGE_LOCATION -> {
                startMapForResult()
            }

            Codes.ADDRESS_CHANGED -> {
                val intent = Intent()
                intent.putExtra(Params.EDITED_ADDRESS, true)
                setResult(Codes.CHANGE_ADDRESS_REQUEST_CODE, intent)
                finish()
            }
        }
    }

    private fun startMapForResult() {
        val intent = Intent(this, MapsActivity::class.java)
        val userLocation = when (viewModel.changeAddressRequest.lat) {
            null -> {
                UserLocation(
                    PrefMethods.getUserLocation()!!.lat,
                    PrefMethods.getUserLocation()!!.lng
                )
            }

            else -> {
                UserLocation(viewModel.changeAddressRequest.lat, viewModel.changeAddressRequest.lng)
            }
        }
        intent.putExtra(Params.USER_LOCATION, userLocation)
        startActivityForResult(intent, Codes.GETTING_USER_LOCATION)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /* When This page opened navigate user to select new location from The map first */
        /* When This page opened navigate user to select new location from The map first */
        when (requestCode) {
            Codes.GETTING_USER_LOCATION -> {

                when (data) {
                    null -> {
                        viewModel.isMapReady.value = true
                        when (viewModel.changeAddressRequest.lat) {
                            null -> {
                                viewModel.changeAddressRequest.lat =
                                    PrefMethods.getUserLocation()!!.lat
                                viewModel.changeAddressRequest.lng =
                                    PrefMethods.getUserLocation()!!.lng
                                viewModel.changeAddressRequest.streetName =
                                    PrefMethods.getUserLocation()!!.address
                                binding.etStreetNAme.setText(PrefMethods.getUserLocation()!!.address)
                            }
                        }
                    }

                    else -> {
                        when {
                            data.hasExtra(Params.USER_LOCATION) -> {
                                val locationItem =
                                    data.getParcelableExtra<UserLocation>(Params.USER_LOCATION)
                                when {
                                    locationItem != null -> {
                                        viewModel.isMapReady.value = true
                                        viewModel.changeAddressRequest.lat = locationItem.lat
                                        viewModel.changeAddressRequest.lng = locationItem.lng
                                        viewModel.changeAddressRequest.streetName =
                                            locationItem.address
                                        binding.etStreetNAme.setText(locationItem.address)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {

        if (googleMap != null) {
            mMap = googleMap
        }

        when {
            viewModel.changeAddressRequest.lat != null && viewModel.changeAddressRequest.lng != null -> {

                val latLng = LatLng(
                    viewModel.changeAddressRequest.lat!!.toDouble(),
                    viewModel.changeAddressRequest.lng!!.toDouble()
                )
                val option = MarkerOptions().position(latLng)
                googleMap!!.addMarker(option)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Const.zoomLevel))
            }
        }
    }

    private fun showToast(msg: String, type: Int) {
        // success 2  >>>  false  1
        val bundle = Bundle()
        bundle.putString(Params.DIALOG_TOAST_MESSAGE, msg)
        bundle.putInt(Params.DIALOG_TOAST_TYPE, type)
        Utils.startDialogActivity(
            this,
            DialogToastFragment::class.java.name,
            Codes.DIALOG_TOAST_REQUEST,
            bundle
        )
    }
}

