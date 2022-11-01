package com.brandsin.user.ui.location.addaddress

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.brandsin.user.R
import com.brandsin.user.databinding.ActivityAddAddressBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Const
import com.brandsin.user.model.constants.Params
import com.brandsin.user.model.location.AddedAddress
import com.brandsin.user.model.location.UserLocation
import com.brandsin.user.network.Status
import com.brandsin.user.ui.dialogs.toast.DialogToastFragment
import com.brandsin.user.ui.location.map.MapsActivity
import com.brandsin.user.utils.PrefMethods
import com.brandsin.user.utils.Utils
import com.brandsin.user.utils.map.observe
import timber.log.Timber

class AddAddressActivity : AppCompatActivity(), Observer<Any?>, OnMapReadyCallback
{
    lateinit var binding: ActivityAddAddressBinding
    lateinit var viewModel: AddAddressViewModel
    lateinit var mMap : GoogleMap

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_address)
        viewModel = ViewModelProvider(this).get(AddAddressViewModel::class.java)
        binding.viewModel = viewModel

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        when {
            intent.hasExtra(Params.USER_LOCATION) -> {
                when {
                    intent.getParcelableExtra<UserLocation>(Params.USER_LOCATION) != null -> {
                        val userLocation: UserLocation = intent.extras!!.getParcelable(Params.USER_LOCATION)!!
                        viewModel.addAddressRequest.lat = userLocation.lat
                        viewModel.addAddressRequest.lng = userLocation.lng
                        viewModel.addAddressRequest.streetName = userLocation.address
                    }
                }
            }
        }

        if (PrefMethods.getUserData()!!.name !=null){
            viewModel.addAddressRequest.firstName = PrefMethods.getUserData()!!.name.toString()
        }
        if (PrefMethods.getUserData()!!.last_name !=null){
            viewModel.addAddressRequest.lastName = PrefMethods.getUserData()!!.last_name.toString()
        }
        if (PrefMethods.getUserData()!!.phoneNumber !=null){
            viewModel.addAddressRequest.phoneNumber = PrefMethods.getUserData()!!.phoneNumber.toString()
        }


        binding.spinnerType.apply {
            setItems(viewModel.typesSpinnerLst)
            setOnItemSelectedListener { _, _, _, item ->
                viewModel.addAddressRequest.type = item.toString()
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
                    val latLng = LatLng(viewModel.addAddressRequest.lat!!.toDouble(), viewModel.addAddressRequest.lng!!.toDouble())
                    val option = MarkerOptions().position(latLng)
                    mMap.addMarker(option)
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Const.zoomLevel))
                }
                else -> {}
            }
        }
    }

    override fun onChanged(it: Any?) {
        if (it == null) return
        when (it) {
            Codes.EMPTY_FIRST_NAME -> {
                showToast(getString(R.string.please_enter_your_first_name) , 1)
            }
            Codes.EMPTY_LAST_NAME -> {
                showToast(getString(R.string.please_enter_your_last_name) , 1)
            }
            Codes.EMPTY_TYPE -> {
                showToast(getString(R.string.please_enter_your_place_type) , 1)
            }
            Codes.EMPTY_streetName -> {
                showToast(getString(R.string.please_enter_your_streetName) , 1)
            }
            Codes.EMPTY_PHONE -> {
                showToast(getString(R.string.empty_phone) , 1)
            }
            Codes.INVALID_PHONE -> {
                showToast(getString(R.string.invalid_phone) , 1)
            }
            Codes.CHANGE_LOCATION -> {
                startMapForResult()
            }
            Codes.BACK_PRESSED -> {
                finishAffinity()
            }
            Codes.ADDRESS_SAVED ->
            {
                val addedAddress = AddedAddress(viewModel.newAddressResponse.id, viewModel.newAddressResponse.lat,
                                viewModel.newAddressResponse.lng , viewModel.newAddressResponse.streetName ,
                                viewModel.newAddressResponse.type , viewModel.newAddressResponse.typeLabel , viewModel.newAddressResponse.phoneNumber,
                    viewModel.newAddressResponse.landmark)

                val bundle = Bundle()
                bundle.putParcelable(Params.ADDED_ADDRESS, addedAddress)
                val intent = Intent()
                intent.putExtras(bundle)
                setResult(Codes.ADD_ADDRESS_REQUEST_CODE, intent)
                finish()
            }
        }
    }

    private fun startMapForResult() {
        val intent = Intent(this, MapsActivity::class.java)
        val userLocation = when (viewModel.addAddressRequest.lat) {
            null -> {
                UserLocation(PrefMethods.getUserLocation()!!.lat, PrefMethods.getUserLocation()!!.lng)
            }
            else -> {
                UserLocation(viewModel.addAddressRequest.lat, viewModel.addAddressRequest.lng)
            }
        }
        intent.putExtra(Params.USER_LOCATION, userLocation)
        startActivityForResult(intent, Codes.GETTING_USER_LOCATION)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /* When This page opened navigate user to select new location from The map first */
        when (requestCode) {
            Codes.GETTING_USER_LOCATION -> {
                when (data) {
                    null -> {
                        viewModel.isMapReady.value = true
                        when (viewModel.addAddressRequest.lat){
                            null -> {
                                viewModel.addAddressRequest.lat = PrefMethods.getUserLocation()!!.lat
                                viewModel.addAddressRequest.lng = PrefMethods.getUserLocation()!!.lng
                                viewModel.addAddressRequest.streetName = PrefMethods.getUserLocation()!!.address
                                binding.etStreetNAme.setText(PrefMethods.getUserLocation()!!.address)
                            }
                        }
                    }
                    else -> {
                        when {
                            data.hasExtra(Params.USER_LOCATION) -> {
                                val locationItem = data.getParcelableExtra<UserLocation>(Params.USER_LOCATION)
                                when {
                                    locationItem != null -> {
                                        viewModel.isMapReady.value = true
                                        viewModel.addAddressRequest.lat = locationItem.lat
                                        viewModel.addAddressRequest.lng = locationItem.lng
                                        viewModel.addAddressRequest.streetName = locationItem.address
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
            viewModel.addAddressRequest.lat != null && viewModel.addAddressRequest.lng != null -> {
                val latLng = LatLng(viewModel.addAddressRequest.lat!!.toDouble(), viewModel.addAddressRequest.lng!!.toDouble())
                val option = MarkerOptions().position(latLng)
                googleMap!!.addMarker(option)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Const.zoomLevel))
            }
        }
    }

    private fun showToast(msg : String, type : Int) {
        // success 2
        // false  1
        val bundle = Bundle()
        bundle.putString(Params.DIALOG_TOAST_MESSAGE, msg)
        bundle.putInt(Params.DIALOG_TOAST_TYPE, type)
        Utils.startDialogActivity(this, DialogToastFragment::class.java.name, Codes.DIALOG_TOAST_REQUEST, bundle)
    }
}

