package com.brandsin.user.ui.profile.updateprofile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.StrictMode
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.brandsin.user.R
import com.brandsin.user.databinding.ProfileFragmentUpdateProfileBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.ui.activity.home.HomeActivity
import com.brandsin.user.utils.CustomImagePicker
import com.brandsin.user.utils.PrefMethods
import com.brandsin.user.utils.Utils
import com.google.android.material.datepicker.MaterialDatePicker
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream
import java.net.URL


class UpdateProfileFragment : BaseHomeFragment(), Observer<Any?> {

    private lateinit var binding: ProfileFragmentUpdateProfileBinding
    private lateinit var viewModel: UpdateProfileViewModel

    private var img = ""
    private var bitmap: Bitmap? = null

    private val MY_PERMISSIONS_REQUEST_CAMERA = 200
    private val PICK_IMAGE_ID = 234
    private val STORAGE_PERMISSION_CODE = 101

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.profile_fragment_update_profile,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[UpdateProfileViewModel::class.java]
        binding.viewModel = viewModel

        setBarName(getString(R.string.profile_info))

        viewModel.mutableLiveData.observe(viewLifecycleOwner, this)
        viewModel.showProgress().observe(viewLifecycleOwner) { aBoolean ->
            if (!aBoolean!!) {
                binding.progressLayout.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            } else {
                binding.progressLayout.visibility = View.VISIBLE
                requireActivity().window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            }
        }

        viewModel.request.phone_number = PrefMethods.getUserData()!!.phoneNumber
        if (PrefMethods.getUserData()!!.picture != null && PrefMethods.getUserData()!!.picture.toString()
                .isNotEmpty()
        ) {
            Picasso.get()
                .load(PrefMethods.getUserData()!!.picture.toString())
                .error(R.drawable.profile)
                .into(binding.ivUserImg)
            img = convertUrlToBase64(PrefMethods.getUserData()!!.picture).toString()
        } else {
            binding.ivUserImg.setImageResource(R.drawable.profile)
        }

        binding.imgLayout.setOnClickListener {
            if (checkIfAllPermissionsGranted().not())
            {
                checkAndRequestAllPermissions()
            } else {
                val chooseImageIntent = CustomImagePicker.getPickImageIntent(requireActivity())
                startActivityForResult(chooseImageIntent, PICK_IMAGE_ID)
            }
        }
    }

    override fun onChanged(value: Any?) {
        if (value == null) return
        when (value) {
            Codes.EMPTY_FIRST_NAME -> {
                showToast(getString(R.string.please_enter_your_first_name), 1)
            }

            Codes.EMPTY_LAST_NAME -> {
                showToast(getString(R.string.please_enter_your_last_name), 1)
            }

            Codes.EMPTY_PHONE -> {
                showToast(getString(R.string.empty_phone), 1)
            }

            Codes.INVALID_PHONE -> {
                showToast(getString(R.string.invalid_phone), 1)
            }

            Codes.EMAIL_EMPTY -> {
                showToast(getString(R.string.enter_mail), 1)
            }

            Codes.EMAIL_INVALID -> {
                showToast(getString(R.string.email_must_correct), 1)
            }

            Codes.EDIT_PROFILE -> {
                showToast(getString(R.string.successfully_updated), 2)

                startActivity(Intent(requireActivity(), HomeActivity::class.java))
                requireActivity().finishAffinity()
                viewModel.setShowProgress(false)
            }

            Codes.SELECT_BIRTHDAY -> {
                val datePicker =
                    MaterialDatePicker.Builder.datePicker()
                        .setTitleText(getString(R.string.SelectBirthDate))
                        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                        .build()
                datePicker.addOnPositiveButtonClickListener {
                    viewModel.birthDay.set(Utils.convertLongToTime(it))
                    viewModel.request.birthdate = viewModel.birthDay.get()

                }
                datePicker.show(childFragmentManager, "BirthDate")

            }


            else -> {
                if (value is String) {
                    showToast(value.toString(), 1)
                }
                viewModel.setShowProgress(false)
            }
        }
    }

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(permission), requestCode)
        } else {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                //do your stuff
                val chooseImageIntent = CustomImagePicker.getPickImageIntent(requireActivity())
                startActivityForResult(chooseImageIntent, PICK_IMAGE_ID)
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CAMERA), MY_PERMISSIONS_REQUEST_CAMERA
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            when (requestCode) {
                PICK_IMAGE_ID, MY_PERMISSIONS_REQUEST_CAMERA ->
                    if (resultCode != 0) {
                        bitmap = CustomImagePicker.getImageFromResult(requireActivity(), resultCode, data)
                        binding.ivUserImg.setImageBitmap(bitmap)
                        img = convertBitmapToString(bitmap!!)
                        viewModel.request.picture = "data:image/png;base64,$img"
                        viewModel.request.picture_type = ""
                    }

                else ->
                    super.onActivityResult(requestCode, resultCode, data)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun convertBitmapToString(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        val encoded = Base64.encodeToString(byteArray, Base64.DEFAULT)
        return encoded.replace(" ", "").replace("\n", "")
    }

    private fun convertUrlToBase64(url: String?): String {
        val newUrl: URL
        val bitmap: Bitmap
        var base64 = ""
        try {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
            newUrl = URL(url)
            bitmap = BitmapFactory.decodeStream(newUrl.openConnection().getInputStream())
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            base64 = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return base64.replace(" ", "").replace("\n", "")
    }
}