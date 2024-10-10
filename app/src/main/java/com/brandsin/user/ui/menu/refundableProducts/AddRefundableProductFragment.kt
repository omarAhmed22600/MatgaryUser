package com.brandsin.user.ui.menu.refundableProducts

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.brandsin.user.R
import com.brandsin.user.databinding.FragmentAddRefundableProductBinding
import com.brandsin.user.model.refundableProduct.ReasonReturnItem
import com.brandsin.user.model.refundableProduct.RefundableProductItem
import com.brandsin.user.network.ResponseHandler
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.ui.activity.home.HomeActivity
import com.brandsin.user.ui.menu.refundableProducts.viewmodel.RefundableProductViewModel
import com.brandsin.user.utils.CustomImagePicker
import com.fxn.pix.Options
import java.io.ByteArrayOutputStream

class AddRefundableProductFragment : BaseHomeFragment() {

    private var _binding: FragmentAddRefundableProductBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RefundableProductViewModel by viewModels()

    private var refundableProductItem: RefundableProductItem? = null

    private var reasonReturnList: List<ReasonReturnItem?>? = null

    private var selectedReasonReturnId: Int = 0
    private var pickImage: String? = null
    private var notes: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddRefundableProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBarName(getString(R.string.refundable))

        refundableProductItem = requireArguments().getParcelable("REFUNDABLE_PRODUCT_ITEM")

        viewModel.getAllReasonsReturnList()

        setBtnListener()
        subscribeData()
    }

    private fun setBtnListener() {
        binding.btnAddPhotoAndVideo.setOnClickListener {
            // checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 8888)
            // val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            // intent.addCategory(Intent.CATEGORY_OPENABLE)
            // intent.type = "image/* video/*"
            // startActivityForResult(intent, 8888)

            launcher.launch(
                PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageAndVideo).build()
            )
        }

        binding.imgAddPhotoAndVideo.setOnClickListener {
            launcher.launch(
                PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageAndVideo).build()
            )
        }

        binding.videoAddPhotoAndVideo.setOnClickListener {
            launcher.launch(
                PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageAndVideo).build()
            )
        }

        binding.cvChooseReasonForReturn.setOnClickListener {
            // open dialog choose reason for return

            val customDialog =
                ReasonReturnDialog(requireContext(), reasonReturnList) { selectedItem ->
                    // Handle the selected item
                    // For example, you can display a Toast with the selected item
                    binding.chooseReasonForReturn.text = selectedItem.name
                    selectedReasonReturnId = selectedItem.id ?: 0
                }

            customDialog.show()
        }

        binding.btnSubmit.setOnClickListener {
            if (validate()) {
                viewModel.addRefundableProduct(
                    refundableProductItem,
                    pickImage,
                    notes,
                    selectedReasonReturnId
                )
            }
        }
    }

    private fun validate(): Boolean {
        val isValid = true
        notes = binding.edtNotes.text.toString().trim()

        if (pickImage.isNullOrEmpty()) {
            showToast(getString(R.string.add_a_photo_or_video), 1)
            return false
        }

        if (selectedReasonReturnId == 0) {
            binding.chooseReasonForReturn.error =
                getString(R.string.choose_the_reason_for_the_return)
            showToast(getString(R.string.choose_the_reason_for_the_return), 1)
            return false
        } else {
            binding.chooseReasonForReturn.error = null
        }

        if (notes.isNullOrEmpty()) {
            binding.edtNotes.error = getString(R.string.please_enter_notes)
            showToast(getString(R.string.please_enter_notes), 1)
            return false
        } else {
            binding.edtNotes.error = null
        }

        return isValid
    }

    private fun subscribeData() {
        viewModel.getReasonsReturnResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseHandler.Success -> {
                    // bind data to the view
                    viewModel.obsIsLoading.set(false)
                    reasonReturnList = it.data?.reasonReturnItem
                }

                is ResponseHandler.Error -> {
                    // show error message
                    (requireActivity() as HomeActivity).hideLoading()
                }

                is ResponseHandler.Loading -> {
                    // show a progress bar
                    (requireActivity() as HomeActivity).showLoading()
                }

                is ResponseHandler.StopLoading -> {
                    // show a progress bar
                    (requireActivity() as HomeActivity).hideLoading()
                }

                else -> {}
            }
        }

        viewModel.addRefundableProductResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseHandler.Success -> {
                    viewModel.obsIsLoading.set(false)
                    // bind data to the view
                    if (it.data?.success == true) {
                        showToast(it.data.message ?: "", 2)
                        val bundle = Bundle()
                        bundle.putString("ORDER_NUMBER", it.data.orderNumber)
                        findNavController().navigate(R.id.refundableSuccessFragment, bundle)
                    } else {
                        showToast(it.data?.message ?: "", 1)
                    }
                }

                is ResponseHandler.Error -> {
                    // show error message
                    (requireActivity() as HomeActivity).hideLoading()
                }

                is ResponseHandler.Loading -> {
                    // show a progress bar
                    (requireActivity() as HomeActivity).showLoading()
                }

                is ResponseHandler.StopLoading -> {
                    // show a progress bar
                    (requireActivity() as HomeActivity).hideLoading()
                }

                else -> {}
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.btnAddPhotoAndVideo.visibility = View.VISIBLE
    }

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                permission
            ) == PackageManager.PERMISSION_DENIED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(permission),
                requestCode
            )
        } else {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                //do your stuff
                val chooseImageIntent = CustomImagePicker.getPickImageIntent(requireActivity())
                startActivityForResult(chooseImageIntent, 234)
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.CAMERA), 202
                )
            }
        }
    }

    private var launcher = registerForActivityResult<PickVisualMediaRequest, Uri>(
        ActivityResultContracts.PickVisualMedia()
    ) { result ->
        binding.btnAddPhotoAndVideo.visibility = View.INVISIBLE
        //binding.imgAddPhotoAndVideo.visibility = View.VISIBLE

        if (result != null) {
            pickImage = getPathFromUri(result)
        } else {
            // getPathFromUri(getImageUri(uriToBitmap(result)!!)) // /storage/emulated/0/Pictures/Title (3).jpg
        }

        if (pickImage?.endsWith(".mp4", true) == true) {
            binding.videoAddPhotoAndVideo.visibility = View.VISIBLE
            binding.imgAddPhotoAndVideo.visibility = View.GONE
            binding.videoAddPhotoAndVideo.setVideoURI(result)
        } else if (pickImage?.endsWith(".png", true) == true
            || pickImage?.endsWith(".jpg", true) == true
            || pickImage?.endsWith(".jpeg", true) == true
        ) {
            binding.videoAddPhotoAndVideo.visibility = View.GONE
            binding.imgAddPhotoAndVideo.visibility = View.VISIBLE
            binding.imgAddPhotoAndVideo.setImageURI(result)
        }
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            when (requestCode) {
                234, 202 ->
                    if (resultCode != 0) {
                        binding.btnAddPhotoAndVideo.visibility = View.INVISIBLE
                        binding.imgAddPhotoAndVideo.visibility = View.VISIBLE
                        binding.imgAddPhotoAndVideo.setImageURI(data?.data)

                        val bitmap =
                            CustomImagePicker.getImageFromResult(requireActivity(), resultCode, data)
                        binding.imgAddPhotoAndVideo.setImageBitmap(bitmap)

                        pickImage = if (data?.data != null) {
                            getPathFromUri(data.data)
                        } else {
                            getPathFromUri(getImageUri(bitmap)) // /storage/emulated/0/Pictures/Title (3).jpg
                        }
                    }

                else ->
                    super.onActivityResult(requestCode, resultCode, data)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }*/

    private fun getPathFromUri(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor =
            uri?.let {
                requireActivity().contentResolver.query(
                    it,
                    projection,
                    null,
                    null,
                    null
                )
            }
        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        val path = columnIndex?.let { cursor.getString(it) }
        cursor?.close()
        return path
    }

    private fun getImageUri(inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            requireActivity().contentResolver, inImage,
            "Title", null
        )
        return Uri.parse(path)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}