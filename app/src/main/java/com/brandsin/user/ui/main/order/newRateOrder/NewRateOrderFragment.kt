package com.brandsin.user.ui.main.order.newRateOrder

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresExtension
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.brandsin.user.R
import com.brandsin.user.databinding.FragmentNewRateOrderBinding
import com.brandsin.user.model.order.details.OrderItem
import com.brandsin.user.network.ResponseHandler
import com.brandsin.user.network.Status
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.ui.activity.home.HomeActivity
import com.brandsin.user.ui.main.order.newRateOrder.model.RatingItem
import com.brandsin.user.ui.main.order.newRateOrder.model.RatingRequest
import com.brandsin.user.ui.main.order.newRateOrder.viewmodel.NewRateOrderViewModel
import com.brandsin.user.ui.main.order.orderdetails.OrderDetailsViewModel
import com.brandsin.user.utils.MyApp
import com.brandsin.user.utils.PrefMethods
import com.brandsin.user.utils.map.observe
import com.bumptech.glide.Glide
import timber.log.Timber

class NewRateOrderFragment : BaseHomeFragment() {

    private var _binging: FragmentNewRateOrderBinding? = null
    private val binding get() = _binging!!

    private val viewModel: NewRateOrderViewModel by viewModels()
    private lateinit var viewModelOrderDetails: OrderDetailsViewModel

    private var orderId: Int? = null

    private var pickImage: String? = null
    private var rateType: String? = "store"
    private var title: String? = null
    private var body: String? = null
    private var rate: Int? = null
    private var image: String? = null
    private var video: String? = null

    private lateinit var ratingsProductOrderAdapter: RatingsProductOrderAdapter

    private var selectedOrder: OrderItem? = null
    private var selectedOrderPosition = -1
    private val btnAddPhotoClickCallBack: (order: OrderItem, position: Int) -> Unit =
        { order, position ->
            selectedOrder = order
            selectedOrderPosition = position
            pickImageAndVideoFromGallery()
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binging = FragmentNewRateOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModelOrderDetails = ViewModelProvider(this)[OrderDetailsViewModel::class.java]

        setBarName(getString(R.string.rate_order))

        orderId = requireArguments().getInt("ORDER_ID")

        viewModelOrderDetails.getOrderDetails(orderId ?: 0)

        setBtnListener()
        subscribeData()
    }

    @RequiresExtension(extension = Build.VERSION_CODES.R, version = 2)
    @SuppressLint("ResourceAsColor")
    private fun setBtnListener() {
        binding.imgStore.setOnClickListener {
            val action =
                NewRateOrderFragmentDirections.actionNewRateOrderToNavStoreDetails(
                    viewModelOrderDetails.orderDetails.order?.storeId
                        ?: viewModelOrderDetails.orderDetails.order?.store?.id!!
                )
            findNavController().navigate(action)
        }

        binding.rvRatingsProductOrder.apply {
            ratingsProductOrderAdapter =
                RatingsProductOrderAdapter(btnAddPhotoClickCallBack)
            adapter = ratingsProductOrderAdapter
        }

        binding.storeRating.setOnClickListener {
            binding.constraintStoreRating.visibility = View.VISIBLE
            binding.constraintProductRating.visibility = View.GONE
            binding.constraintDriverRating.visibility = View.GONE

            when (rateType) {
                "product" -> {
                    for (i in ratingsProductOrderAdapter.getOrderList()!!) {
                        if ((i?.rate ?: 0) > 0 || i?.title?.isNotEmpty() == true ||
                            i?.body?.isNotEmpty() == true
                        ) {
                            showAlertDialog()
                        } else {
                            binding.storeRating.setBackgroundResource(R.drawable.bg_selected_text_search)
                            binding.storeRating.setTextColor(R.color.color_primary)
                            binding.productRating.setTextColor(R.color.black)
                            binding.productRating.setBackgroundResource(R.drawable.bg_unselected_text_search)
                            binding.driverRating.setTextColor(R.color.black)
                            binding.driverRating.setBackgroundResource(R.drawable.bg_unselected_text_search)
                        }
                    }
                }

                "driver" -> {
                    if (binding.driverRatingbar.rating.toInt() > 0) {
                        showAlertDialog()
                    } else {
                        binding.storeRating.setBackgroundResource(R.drawable.bg_selected_text_search)
                        binding.storeRating.setTextColor(R.color.color_primary)
                        binding.productRating.setTextColor(R.color.black)
                        binding.productRating.setBackgroundResource(R.drawable.bg_unselected_text_search)
                        binding.driverRating.setTextColor(R.color.black)
                        binding.driverRating.setBackgroundResource(R.drawable.bg_unselected_text_search)
                    }
                }
            }

            rateType = "store"
        }

        binding.productRating.setOnClickListener {
            binding.constraintStoreRating.visibility = View.GONE
            binding.constraintProductRating.visibility = View.VISIBLE
            binding.constraintDriverRating.visibility = View.GONE

            when (rateType) {
                "store" -> {
                    if (binding.storeRatingbar.rating.toInt() > 0) {
                        showAlertDialog()
                    } else {
                        binding.productRating.setBackgroundResource(R.drawable.bg_selected_text_search)
                        binding.productRating.setTextColor(R.color.color_primary)
                        binding.storeRating.setTextColor(R.color.black)
                        binding.storeRating.setBackgroundResource(R.drawable.bg_unselected_text_search)
                        binding.driverRating.setTextColor(R.color.black)
                        binding.driverRating.setBackgroundResource(R.drawable.bg_unselected_text_search)
                    }
                }

                "driver" -> {
                    if (binding.driverRatingbar.rating.toInt() > 0) {
                        showAlertDialog()
                    } else {
                        binding.productRating.setBackgroundResource(R.drawable.bg_selected_text_search)
                        binding.productRating.setTextColor(R.color.color_primary)
                        binding.storeRating.setTextColor(R.color.black)
                        binding.storeRating.setBackgroundResource(R.drawable.bg_unselected_text_search)
                        binding.driverRating.setTextColor(R.color.black)
                        binding.driverRating.setBackgroundResource(R.drawable.bg_unselected_text_search)
                    }
                }
            }

            rateType = "product"
        }

        binding.driverRating.setOnClickListener {
            binding.constraintStoreRating.visibility = View.GONE
            binding.constraintProductRating.visibility = View.GONE
            binding.constraintDriverRating.visibility = View.VISIBLE

            when (rateType) {
                "store" -> {
                    if (binding.storeRatingbar.rating.toInt() > 0) {
                        showAlertDialog()
                    } else {
                        binding.driverRating.setBackgroundResource(R.drawable.bg_selected_text_search)
                        binding.driverRating.setTextColor(R.color.color_primary)
                        binding.storeRating.setTextColor(R.color.black)
                        binding.storeRating.setBackgroundResource(R.drawable.bg_unselected_text_search)
                        binding.productRating.setTextColor(R.color.black)
                        binding.productRating.setBackgroundResource(R.drawable.bg_unselected_text_search)
                    }
                }

                "product" -> {
                    for (i in ratingsProductOrderAdapter.getOrderList()!!) {
                        if ((i?.rate ?: 0) > 0 || i?.title?.isNotEmpty() == true
                            || i?.body?.isNotEmpty() == true
                            || title?.isNotEmpty() == true || body?.isNotEmpty() == true
                        ) {
                            showAlertDialog()
                        } else {
                            binding.driverRating.setBackgroundResource(R.drawable.bg_selected_text_search)
                            binding.driverRating.setTextColor(R.color.color_primary)
                            binding.storeRating.setTextColor(R.color.black)
                            binding.storeRating.setBackgroundResource(R.drawable.bg_unselected_text_search)
                            binding.productRating.setTextColor(R.color.black)
                            binding.productRating.setBackgroundResource(R.drawable.bg_unselected_text_search)
                        }
                    }
                }
            }

            rateType = "driver"
        }

        binding.btnAddPhotoAndVideo.setOnClickListener {
            pickImageAndVideoFromGallery()
        }

        binding.btnSendFeedback.setOnClickListener {
            if (rateType == "store") {
                if (validateRatingStore()) {
                    callAddRatingOrderToStoreOrProduct()
                }
            } else if (rateType == "product") {
                if (validateRatingProduct()) {
                    callAddRatingOrderToStoreOrProduct()
                }
            } else {
                if (validateRatingDriver()) {
                    callAddRatingOrderToStoreOrProduct()
                }
            }
        }

        binding.imgAddPhotoAndVideo.setOnClickListener {
            pickImageAndVideoFromGallery()
        }

        binding.videoAddPhotoAndVideo.setOnClickListener {
            pickImageAndVideoFromGallery()
        }
    }

    private fun pickImageAndVideoFromGallery() {
//        val pickPhotoIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        val photoPickerIntent = Intent(Intent.ACTION_PICK)
//        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        // val picKCameraIntent = Intent(MediaStore.ACTION_PICK_IMAGES)
//        val takeVideoIntent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
//        // pickPhotoIntent.type = "*/*"
//        pickPhotoIntent.putExtra(Intent.EXTRA_INTENT, cameraIntent)
//        // photoPickerIntent.putExtra(Intent.EXTRA_INTENT, picKCameraIntent)
//        pickPhotoIntent.putExtra(Intent.EXTRA_INTENT, takeVideoIntent)
//        pickPhotoIntent.putExtra(Intent.EXTRA_INTENT, pickPhotoIntent)
//        pickPhotoIntent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
//        startActivityForResult(pickPhotoIntent, 8888)

        // checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, 8888)

        launcher.launch(
            PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageAndVideo).build()
        )
    }

    private fun callAddRatingOrderToStoreOrProduct() {
        if (pickImage.isNullOrEmpty()) {
            when (rateType) {
                "store" -> {
                    val reviewItem = RatingItem(
                        itemType = rateType, // "store" , product, driver
                        itemId = viewModelOrderDetails.orderDetails.order?.storeId, // 56
                        userId = PrefMethods.getUserData()?.id,
                        rate = binding.storeRatingbar.rating.toInt(), // 5
                        title = binding.etAddressEvaluateStore.text.toString()
                            .trim(), // "Excellent store"
                        body = binding.etWhatDidYouLikeInStore.text.toString()
                            .trim(), // A positive review for the store.
                        image = "", // image
                        video = "" // video
                    )

                    val ratingRequest = RatingRequest(items = listOf(reviewItem))

                    viewModel.addRateStoreOrProductOrDriver(ratingRequest)
                }

                "product" -> {
                    ratingsProductOrderAdapter.getOrderList()
                    var ratingRequest: RatingRequest? = null
                    ratingsProductOrderAdapter.getOrderList()?.forEach {
                        val reviewItem = RatingItem(
                            itemType = rateType, // "store" , product, driver
                            itemId = it?.productId, // 56
                            userId = PrefMethods.getUserData()?.id,
                            rate = it?.rate, // 5
                            title = it?.title ?: "", // "Excellent store"
                            body = it?.body ?: "", // A positive review for the store.
                            image = it?.pickImage ?: "",
                            video = it?.video ?: ""
                        )
                        ratingRequest = RatingRequest(items = listOf(reviewItem))
                    }

                    viewModel.addRateStoreOrProductOrDriver(ratingRequest!!)
                }

                "driver" -> {
                    val reviewItem = RatingItem(
                        itemType = rateType, // "store" , product, driver
                        itemId = 1, // 56
                        userId = PrefMethods.getUserData()?.id,
                        rate = binding.driverRatingbar.rating.toInt(), // 5
                        title = "", // "Excellent store"
                        body = "", // A positive review for the store.
                        image = "",
                        video = ""
                    )

                    val ratingRequest = RatingRequest(items = listOf(reviewItem))

                    viewModel.addRateStoreOrProductOrDriver(ratingRequest)
                }
            }
        } else {
            viewModel.uploadMultiFiles(pickImage!!)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun subscribeData() {
        observe(viewModelOrderDetails.apiResponseLiveData) { it ->
            when (it.status) {
                Status.ERROR_MESSAGE -> {
                    showToast(it.message.toString(), 1)
                }

                Status.SUCCESS_MESSAGE -> {
                    showToast(it.message.toString(), 2)
                }

                Status.SUCCESS -> {
                    Glide.with(requireActivity())
                        .load(viewModelOrderDetails.orderDetails.order?.store?.image)
                        .error(R.drawable.app_logo)
                        .into(binding.imgStore)

                    binding.storeName.text = viewModelOrderDetails.orderDetails.order?.store?.name
                    // binding.orderStatus.text = viewModelOrderDetails.orderDetails.order?.status

                    when {
                        viewModelOrderDetails.orderDetails.order?.status!!.contains("pending") -> {
                            binding.orderStatus.text =
                                MyApp.getInstance().getString(R.string.pending)
                        }

                        viewModelOrderDetails.orderDetails.order?.status!!.contains("completed") -> {
                            binding.orderStatus.text =
                                MyApp.getInstance().getString(R.string.completed_new)
                        }

                        viewModelOrderDetails.orderDetails.order?.status!!.contains("canceled") ||
                                viewModelOrderDetails.orderDetails.order?.status!!.contains(
                                    "rejected"
                                ) -> {
                            binding.orderStatus.text =
                                MyApp.getInstance().getString(R.string.cancelled)
                        }

                        else -> binding.orderStatus.text =
                            viewModelOrderDetails.orderDetails.order?.status
                    }

                    binding.orderNum.text =
                        "${getString(R.string.order_number)}:   " + viewModelOrderDetails.orderDetails.order?.orderNumber
                    binding.orderTime.text =
                        "${getString(R.string.order_date)}:   " + viewModelOrderDetails.orderDetails.order?.deliveryTime

                    if (viewModelOrderDetails.orderDetails.items?.isNotEmpty() == true &&
                        viewModelOrderDetails.orderDetails.items?.size!! > 0
                    ) {
                        ratingsProductOrderAdapter.submitData(viewModelOrderDetails.orderDetails.items)
                    } else if (viewModelOrderDetails.orderDetails.offers?.isNotEmpty() == true) {
                        viewModelOrderDetails.orderDetails.offers?.forEach { offerItem ->
                            val item = OrderItem()
                            item.id = offerItem?.id ?: 0
                            item.amount = offerItem?.amount ?: ""
                            // item.product_description = it.offer!!.description
                            item.quantity = offerItem?.quantity ?: 0
                            item.skuCode = offerItem?.sku_code ?: ""
                            item.productName = offerItem?.description ?: "" // it.offer?.name ?: ""
                            item.type = offerItem?.type ?: "" // item.userNotes = it.user_notes
                            item.image = offerItem?.image ?: "" // it.offer?.image ?: ""
                            item.storeId = null // item.addons = null
                            viewModelOrderDetails.orderDetails.items?.add(item)
                        }

                        ratingsProductOrderAdapter.submitData(viewModelOrderDetails.orderDetails.items)
                    }

                }

                else -> {
                    Timber.e(it.message)
                }
            }
        }

        viewModel.addRateStoreOrProductOrDriverResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseHandler.Success -> {
                    // bind data to the view
                    // binding.shimmerFavoriteProductLayout.visibility = View.GONE
                    // binding.rvFavoriteProduct.visibility = View.VISIBLE
                    Timber.e("it.data?.message ${it.data?.message.toString()}")
                    showToast(it.data?.message.toString(), 2)
                    findNavController().navigateUp()
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

        viewModel.uploadMultiFilesResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseHandler.Success -> {
                    // bind data to the view
                    // response
                    if (it.data?.success == true) {
                        saveData(it.data.data?.get(0))
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

    private fun saveData(uploadFile: String?) {
        if (uploadFile?.endsWith(".mp4", true) == true) {
            video = uploadFile
            // selectedOrder?.video = uploadFile
            // ratingsProductOrderAdapter.notifyItemChanged(selectedOrderPosition, selectedOrder)
            selectedOrder?.video = uploadFile.toString()
            ratingsProductOrderAdapter.notifyItemChanged(
                selectedOrderPosition,
                selectedOrder
            )

        } else if (uploadFile?.endsWith(".png", true) == true
            || uploadFile?.endsWith(".jpg", true) == true
        ) {
            image = uploadFile
            // selectedOrder?.pickImage = uploadFile
            // ratingsProductOrderAdapter.notifyItemChanged(selectedOrderPosition, selectedOrder)
            selectedOrder?.pickImage = uploadFile.toString()
            ratingsProductOrderAdapter.notifyItemChanged(
                selectedOrderPosition,
                selectedOrder
            )
        }

        when (rateType) {
            "store" -> {
                val reviewItem = RatingItem(
                    itemType = rateType, // "store" , product, driver
                    itemId = viewModelOrderDetails.orderDetails.order?.store?.id, // 56
                    userId = PrefMethods.getUserData()?.id,
                    rate = binding.storeRatingbar.rating.toInt(), // 5
                    title = binding.etAddressEvaluateStore.text.toString().trim(),
                    body = binding.etWhatDidYouLikeInStore.text.toString().trim(),
                    image = image ?: "", // image
                    video = video ?: "" // video
                )

                val ratingRequest = RatingRequest(items = listOf(reviewItem))

                viewModel.addRateStoreOrProductOrDriver(ratingRequest)
            }

            "product" -> {
                ratingsProductOrderAdapter.getOrderList()
                var ratingRequest: RatingRequest? = null
                ratingsProductOrderAdapter.getOrderList()?.forEach {
                    val reviewItem = RatingItem(
                        itemType = rateType, // "store" , product, driver
                        itemId = it?.productId, // 56
                        userId = PrefMethods.getUserData()?.id,
                        rate = it?.rate, // 5
                        title = it?.title, // "Excellent store"
                        body = it?.body, // A positive review for the store.
                        image = it?.pickImage ?: "",
                        video = it?.video ?: ""
                    )
                    ratingRequest = RatingRequest(items = listOf(reviewItem))
                }

                viewModel.addRateStoreOrProductOrDriver(ratingRequest!!)
            }

            "driver" -> {
                val reviewItem = RatingItem(
                    itemType = rateType, // "store" , product, driver
                    itemId = 1, // 56
                    userId = PrefMethods.getUserData()?.id,
                    rate = binding.driverRatingbar.rating.toInt(), // 5
                    title = "", // "Excellent store"
                    body = "", // A positive review for the store.
                    image = "",
                    video = ""
                )

                val ratingRequest = RatingRequest(items = listOf(reviewItem))

                viewModel.addRateStoreOrProductOrDriver(ratingRequest)
            }
        }
    }

    private fun validateRatingStore(): Boolean {
        val isValid = true
        title = binding.etAddressEvaluateStore.text.toString().trim()
        body = binding.etWhatDidYouLikeInStore.text.toString().trim()
        rate = binding.storeRatingbar.rating.toInt()

        if (rate == 0) {
            showToast(
                getString(R.string.please_enter_the_store_rating) + " " + viewModelOrderDetails.orderDetails.order?.store?.name.toString(),
                1
            )
            return false
        }

        /*if (title.isNullOrEmpty()) {
            binding.etAddressEvaluateStore.error =
                getString(R.string.please_enter_write_a_store_review)
            showToast(
                getString(R.string.please_enter_write_a_store_review),
                1
            )
            return false
        } else {
            binding.etAddressEvaluateStore.error = null
        }

        if (body.isNullOrEmpty()) {
            binding.etWhatDidYouLikeInStore.error = getString(R.string.required)
            showToast(getString(R.string.required), 1)
            return false
        } else {
            binding.etWhatDidYouLikeInStore.error = null
        }*/

        return isValid
    }

    private fun validateRatingDriver(): Boolean {
        val isValid = true
        rate = binding.driverRatingbar.rating.toInt()

        if (rate == 0) {
            showToast(getString(R.string.required), 1)
            return false
        }
        return isValid
    }

    private fun validateRatingProduct(): Boolean {
        val isValid = true

        for (i in ratingsProductOrderAdapter.getOrderList()!!) {
            if (i?.rate == 0) {
                showToast(
                    getString(R.string.please_enter_the_product_rating) + " " + i.productName.toString(),
                    1
                )
                return false
            }
            if (i?.title.isNullOrEmpty()) {
                showToast(getString(R.string.please_enter_write_a_product_review), 1)
                return false
            }
            if (i?.body.isNullOrEmpty()) {
                showToast(getString(R.string.required), 1)
                return false
            }
        }

        return isValid
    }

    private var launcher = registerForActivityResult<PickVisualMediaRequest, Uri>(
        ActivityResultContracts.PickVisualMedia()
    ) { result ->
        binding.btnAddPhotoAndVideo.visibility = View.GONE
        binding.imgAddPhotoAndVideo.visibility = View.VISIBLE

        if (result != null) {
            pickImage = getPathFromUri(result)
        } else {
            // getPathFromUri(getImageUri(uriToBitmap(result)!!)) // /storage/emulated/0/Pictures/Title (3).jpg
        }

        if (pickImage?.endsWith(".mp4", true) == true) {
            binding.videoAddPhotoAndVideo.visibility = View.VISIBLE
            binding.imgAddPhotoAndVideo.visibility = View.GONE
            binding.videoAddPhotoAndVideo.setVideoURI(result)
            selectedOrder?.video = pickImage
            ratingsProductOrderAdapter.notifyItemChanged(
                selectedOrderPosition,
                selectedOrder
            )
        } else if (pickImage?.endsWith(".png", true) == true
            || pickImage?.endsWith(".jpg", true) == true
            || pickImage?.endsWith(".jpeg", true) == true
        ) {
            binding.videoAddPhotoAndVideo.visibility = View.GONE
            binding.imgAddPhotoAndVideo.visibility = View.VISIBLE
            binding.imgAddPhotoAndVideo.setImageURI(result)
            selectedOrder?.pickImage = pickImage
            ratingsProductOrderAdapter.notifyItemChanged(
                selectedOrderPosition,
                selectedOrder
            )
        }
    }

    private fun getPathFromUri(uri: Uri?): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor =
            uri?.let { requireActivity().contentResolver.query(it, projection, null, null, null) }
        val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor?.moveToFirst()
        val path = columnIndex?.let { cursor.getString(it) }
        cursor?.close()
        return path
    }

    @SuppressLint("ResourceAsColor")
    private fun showAlertDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())

        // Set the title and message for the AlertDialog
        // alertDialogBuilder.setTitle("Alert Dialog Title")
        alertDialogBuilder.setMessage(getString(R.string.do_you_want_to_save_data))

        // Set positive button and its click listener
        alertDialogBuilder.setPositiveButton(getString(R.string.ok)) { dialog: DialogInterface, which: Int ->
            // Handle positive button click (if needed)
            when (rateType) {
                "store" -> {
                    binding.storeRating.setBackgroundResource(R.drawable.bg_selected_text_search)
                    binding.storeRating.setTextColor(R.color.color_primary)
                    binding.productRating.setTextColor(R.color.black)
                    binding.productRating.setBackgroundResource(R.drawable.bg_unselected_text_search)
                    binding.driverRating.setTextColor(R.color.black)
                    binding.driverRating.setBackgroundResource(R.drawable.bg_unselected_text_search)
                }

                "product" -> {
                    binding.productRating.setBackgroundResource(R.drawable.bg_selected_text_search)
                    binding.productRating.setTextColor(R.color.color_primary)
                    binding.storeRating.setTextColor(R.color.black)
                    binding.storeRating.setBackgroundResource(R.drawable.bg_unselected_text_search)
                    binding.driverRating.setTextColor(R.color.black)
                    binding.driverRating.setBackgroundResource(R.drawable.bg_unselected_text_search)
                }

                else -> {
                    binding.driverRating.setBackgroundResource(R.drawable.bg_selected_text_search)
                    binding.driverRating.setTextColor(R.color.color_primary)
                    binding.storeRating.setTextColor(R.color.black)
                    binding.storeRating.setBackgroundResource(R.drawable.bg_unselected_text_search)
                    binding.productRating.setTextColor(R.color.black)
                    binding.productRating.setBackgroundResource(R.drawable.bg_unselected_text_search)
                }
            }
            dialog.dismiss()
        }

        // Set negative button and its click listener
        alertDialogBuilder.setNegativeButton(getString(R.string.cancel)) { dialog: DialogInterface, which: Int ->
            // Handle negative button click (if needed)
            when (rateType) {
                "store" -> {
                    binding.storeRating.setBackgroundResource(R.drawable.bg_selected_text_search)
                    binding.storeRating.setTextColor(R.color.color_primary)
                    binding.productRating.setTextColor(R.color.black)
                    binding.productRating.setBackgroundResource(R.drawable.bg_unselected_text_search)
                    binding.driverRating.setTextColor(R.color.black)
                    binding.driverRating.setBackgroundResource(R.drawable.bg_unselected_text_search)
                }

                "product" -> {
                    binding.productRating.setBackgroundResource(R.drawable.bg_selected_text_search)
                    binding.productRating.setTextColor(R.color.color_primary)
                    binding.storeRating.setTextColor(R.color.black)
                    binding.storeRating.setBackgroundResource(R.drawable.bg_unselected_text_search)
                    binding.driverRating.setTextColor(R.color.black)
                    binding.driverRating.setBackgroundResource(R.drawable.bg_unselected_text_search)
                }

                else -> {
                    binding.driverRating.setBackgroundResource(R.drawable.bg_selected_text_search)
                    binding.driverRating.setTextColor(R.color.color_primary)
                    binding.storeRating.setTextColor(R.color.black)
                    binding.storeRating.setBackgroundResource(R.drawable.bg_unselected_text_search)
                    binding.productRating.setTextColor(R.color.black)
                    binding.productRating.setBackgroundResource(R.drawable.bg_unselected_text_search)
                }
            }
            dialog.dismiss()
        }

        // Set neutral button and its click listener
        /*alertDialogBuilder.setNeutralButton("Neutral") { dialog: DialogInterface, which: Int ->
            // Handle neutral button click (if needed)
            dialog.dismiss()
        }*/

        // Create and show the AlertDialog
        val alertDialog: AlertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}