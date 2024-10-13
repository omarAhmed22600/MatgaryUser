package com.brandsin.user.ui.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.brandsin.user.R
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Params
import com.brandsin.user.ui.dialogs.toast.DialogToastFragment
import com.brandsin.user.utils.Utils
import com.brandsin.user.utils.map.PermissionUtil
import com.brandsin.user.utils.map.observe
import com.fxn.pix.Options
import com.fxn.pix.Pix

open class BaseFragment : Fragment() {
    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 1001
    }
    var isNavigated = false

    val storagePermissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)


    private val activityResultsData = MutableLiveData<Intent?>()
    val activityResultsDataLive = activityResultsData



    private val permissionsToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

        listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

    } else {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            listOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,

            )

        } else {

            listOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION

            )

        }
    }
    private var doAfterPermissionIsGranted: (() -> Unit)? = null
    fun checkAndRequestAllPermissions(function: (() -> Unit)? = null) {
        // Request permissions if any are missing
        if (permissionsToRequest.isNotEmpty()) {
            doAfterPermissionIsGranted = function
            requestPermissions(permissionsToRequest.toTypedArray(), REQUEST_CODE_PERMISSIONS)
        }
    }
    fun checkIfAllPermissionsGranted():Boolean =
        hasPermission(permissionsToRequest.toTypedArray())

    private fun hasPermission(permissions: Array<String>): Boolean {
        return permissions.all { ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (hasPermission(permissionsToRequest.toTypedArray())) {
                if (doAfterPermissionIsGranted == null) {
                    // Permission granted, continue with your task
                    showToast(getString(R.string.permission_granted_try_again), 2)
                } else {
                    doAfterPermissionIsGranted!!.invoke()
                    doAfterPermissionIsGranted = null
                }
            } else {
                // Permission denied
                // Check if the user has denied the permission without checking "Don't ask again"
                val shouldShowRationale = permissions.any {
                    ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), it)
                }

                if (shouldShowRationale) {
                    // Show a rationale dialog or Toast message explaining why the permission is needed
                    showToast(getString(R.string.permissions_are_needed_to_access_your_media), 1)

                    // Request permissions again
                    requestPermissions(permissions, REQUEST_CODE_PERMISSIONS)
                } else {
                    // The user has selected "Don't ask again". You might want to disable the feature or show a message.
                    showToast(getString(R.string.permissions_denied_please_enable_them_in_settings), 1)
                }
            }
        }
    }
    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                activityResultsData.value = data
            }
        }

    protected fun launchActivityForResult(intent: Intent) {
        try {
            resultLauncher.launch(intent)
        } catch (ex: Exception) {
            Log.d("myDebug", "BaseFragment launchActivityForResult   " + ex.localizedMessage)
        }
    }

    fun navigateWithAction(action: NavDirections) {
        isNavigated = true
        findNavController().navigate(action)
    }

    fun navigate(resId: Int) {
        isNavigated = true
        findNavController().navigate(resId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (!isNavigated)
            requireActivity().onBackPressedDispatcher.addCallback(this) {
                val navController = findNavController()
                if (navController.currentBackStackEntry?.destination?.id != null) {
                    findNavController().navigateUp()
                } else
                    navController.popBackStack()
            }
    }

    fun showToast(msg: String, type: Int) {
        // Success 2
        // False  1
        val bundle = Bundle()
        bundle.putString(Params.DIALOG_TOAST_MESSAGE, msg)
        bundle.putInt(Params.DIALOG_TOAST_TYPE, type)
        Utils.startDialogActivity(
            requireActivity(),
            DialogToastFragment::class.java.name,
            Codes.DIALOG_TOAST_REQUEST,
            bundle
        )
    }

    fun pickImage(requestCode: Int, mode: Options.Mode? = null) {
        val options = Options.init()
            .setRequestCode(requestCode) //Request code for activity results
            .setFrontfacing(false) //Front Facing camera on start
            .setExcludeVideos(false) //Option to exclude videos
            .setMode(mode)
        if (PermissionUtil.hasImagePermission(requireActivity())) {
            Pix.start(this, options)
        } else {
            observe(
                PermissionUtil.requestPermission(
                    requireActivity(),
                    PermissionUtil.getImagePermissions()
                )
            ) {
                when (it) {
                    PermissionUtil.AppPermissionResult.AllGood -> Pix.start(
                        this,
                        options
                    )

                    else -> {}
                }
            }
        }
    }

    fun showExplanation(
        title: String, message: String, permissions: Array<String>,
    ) {
        val builder = AlertDialog.Builder(
            requireContext(),
        )
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok) { dialog, id ->
                dialog.dismiss()
                val intent =  Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:" + requireContext().packageName)
                startActivity(intent)
            }
        builder.create().show()
    }
}
