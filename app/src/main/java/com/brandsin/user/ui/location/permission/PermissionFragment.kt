package com.brandsin.user.ui.location.permission

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.brandsin.user.R
import com.brandsin.user.databinding.AuthFragmentPermissionBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Params
import com.brandsin.user.model.location.UserLocation
import com.brandsin.user.ui.activity.auth.AuthActivity
import com.brandsin.user.ui.activity.auth.BaseAuthFragment
import com.brandsin.user.ui.activity.home.HomeActivity
import com.brandsin.user.ui.location.map.MapsActivity
import com.brandsin.user.ui.location.permission.GpsUtils.onGpsListener
import com.brandsin.user.utils.PrefMethods
import com.brandsin.user.utils.map.MapUtil
import com.brandsin.user.utils.map.PermissionUtil
import com.google.android.gms.location.*
import java.util.*

class PermissionFragment : BaseAuthFragment(), Observer<Any?> {
    private lateinit var binding: AuthFragmentPermissionBinding
    lateinit var viewModel: PermissionViewModel
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null
    private lateinit var geocoder: Geocoder

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.auth_fragment_permission,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(PermissionViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.mutableLiveData.observe(viewLifecycleOwner, this)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        geocoder = Geocoder(requireActivity(), Locale.forLanguageTag(PrefMethods.getLanguage()))
    }

    override fun onChanged(it: Any?) {
        if (it == null) return
        when (it) {
            Codes.CURRENT_LOCATION_CLICKED -> {
                requestLocationPermission()
            }

            Codes.MANUALL_LOCATION_CLICKED -> {
                val intent = Intent(requireActivity(), MapsActivity::class.java)
                startActivityForResult(intent, Codes.GETTING_USER_LOCATION)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        /* When user select his location manually from map activity*/
        when {
            requestCode == Codes.GETTING_USER_LOCATION && data != null -> {
                when {
                    data.hasExtra(Params.USER_LOCATION) -> {
                        val locationItem =
                            data.getParcelableExtra<UserLocation>(Params.USER_LOCATION)
                        when {
                            locationItem != null -> {
                                saveLocationAndCloseFragment(locationItem)
                            }
                        }
                    }
                }
            }
        }

        /* When user clicked confirm to open setting page and allow permission from there */
        when {
            requestCode == Codes.OPEN_SETTING_DIALOG_REQUEST_CODE && data != null -> {
                when {
                    data.hasExtra(Params.DIALOG_CLICK_ACTION) -> {
                        when {
                            data.getIntExtra(Params.DIALOG_CLICK_ACTION, 1) == 1 -> {
                                openAppDetails()
                            }
                        }
                    }
                }
            }
        }

        /* When user back from setting page */
        when (requestCode) {
            Codes.ALLOW_PERMISSION_FROM_SETTING_PAGE -> {
                when {
                    PermissionUtil.isGranted(
                        requireActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) -> {
                        PrefMethods.saveIsPermissionDeniedForEver(false)
                        when {
                            isGpsEnabled() -> {
                                requestLocationUpdates()
                            }

                            else -> {
                                openLocationFromApp()
                            }
                        }
                    }
                }
            }
        }
    }

    /* Checking the LOCATION permission state before asking the user runtime permission */
    private fun requestLocationPermission() {
        if (checkIfAllPermissionsGranted().not())
        {
            checkAndRequestAllPermissions {
                openLocationFromApp()
            }
        }else{
            openLocationFromApp()
        }
        /* If user selected NEVER ASK AGAIN OR device policy prohibits the app from having that permission *//*
        when {
            PrefMethods.getIsPermissionDeniedForEver() -> {
                Utils.startDialogActivity(
                    requireActivity(),
                    DialogPermissionFragment::class.java.name,
                    Codes.OPEN_SETTING_DIALOG_REQUEST_CODE,
                    null
                )
            }
            *//* If user clicked deny once Or this the first time to open the application *//*
            else -> {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    Codes.ACCESS_LOCATION_REQUEST_CODE
                )
            }
        }*/

    }

    /* Handling actions when user click on Permissions dialog */
    /*override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        when (requestCode) {
            Codes.ACCESS_LOCATION_REQUEST_CODE -> {
                // Permission is granted. Continue the action or workflow
                when {
                    grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                        // Don't call tis method from here, I handle it (ON RESUME) method to not called twice
                        openLocationFromApp()
                    }

                    else -> {
                        when {
                            ActivityCompat.shouldShowRequestPermissionRationale(
                                requireActivity(),
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) -> {
                                // User clicked deny only once
                            }

                            else -> {
                                PrefMethods.saveIsPermissionDeniedForEver(true)
                                //Never ask again selected, or device policy prohibits the app from having that permission.
                                //So, disable that feature, or fall back to another situation...
                            }
                        }
                    }
                }
                return
            }
        }
    }*/

    private fun saveLocationAndCloseFragment(location: UserLocation) {
        PrefMethods.saveUserLocation(
            UserLocation(
                lat = location.lat.toString(),
                lng = location.lng.toString(),
                address = location.address
            )
        )
        when {
            PrefMethods.getLoginState() -> {
                requireActivity().startActivity(Intent(requireActivity(), HomeActivity::class.java))
                requireActivity().finishAffinity()
            }

            else -> {
                PrefMethods.deleteUserData()
                startActivity(Intent(requireActivity(), AuthActivity::class.java))
                requireActivity().finishAffinity()
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun requestLocationUpdates() {

        // Timber.e("getting location updates")
        viewModel.isShown.set(true)
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = (10 * 1000).toLong()
        locationRequest.fastestInterval = (2 * 1000).toLong()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult == null) {
                    // Timber.e("couldn't get location update")
                    requestLocationPermission()
                } else {
                    // Timber.e("$locationResult")
                    if (locationResult.locations.size > 0) {

                        val location = locationResult.locations[0]
                        val userLocation = UserLocation()
                        userLocation.run {
                            lat = location!!.latitude.toString()
                            lng = location.longitude.toString()
                            address = MapUtil.getLocationAddress(
                                getGeoCoder(),
                                location.latitude,
                                location.longitude
                            )
                        }
                        saveLocationAndCloseFragment(userLocation)
                    }
                    stopLocationUpdates()
                }

                viewModel.isShown.set(false)
            }
        }
        try {
            fusedLocationClient?.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: Exception) {
            // Timber.e(e)
        }
    }

    /* When user clicked Allow to open GPS without going to setting page */
    private fun openLocationFromApp() {

        GpsUtils(requireActivity()).turnGPSOn(object : onGpsListener {
            override fun gpsStatus(isGPSEnable: Boolean) {
                requestLocationUpdates()
            }
        })
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        locationCallback?.let {
            fusedLocationClient?.removeLocationUpdates(it)
        }
    }

    fun getGeoCoder(): Geocoder {
        return geocoder
    }

    /* Open app setting details page If user selected deny and don't ask again For the ACCESS_FINE_LOCATION permission */
    private fun openAppDetails() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", requireActivity().packageName, null)
        intent.data = uri
        startActivityForResult(intent, Codes.ALLOW_PERMISSION_FROM_SETTING_PAGE)
    }

    private fun isGpsEnabled(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return when {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> {
                true
            }

            else -> {
                false
            }
        }
    }
}