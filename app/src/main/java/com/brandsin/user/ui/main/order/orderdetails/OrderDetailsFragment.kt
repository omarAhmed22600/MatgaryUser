package com.brandsin.user.ui.main.order.orderdetails

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.brandsin.user.R
import com.brandsin.user.databinding.HomeFragmentOrderDetailsBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Const
import com.brandsin.user.model.order.details.OfferItem
import com.brandsin.user.model.order.details.OrderDetailsResponse
import com.brandsin.user.network.Status
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.ui.chat.model.MessageModel
import com.brandsin.user.ui.main.order.orderdetails.adapter.GiftCodeAdapter
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class OrderDetailsFragment : BaseHomeFragment(), Observer<Any?>, OnMapReadyCallback {

    private lateinit var binding: HomeFragmentOrderDetailsBinding

    private lateinit var viewModel: OrderDetailsViewModel

    private val fragmentArgs: OrderDetailsFragmentArgs by navArgs()

    private lateinit var giftCodeAdapter: GiftCodeAdapter

    private val onClickIconCopyCallBack: (position: Int, item: OfferItem) -> Unit =
        { _, item ->
            // Navigate to add refundable
            copyToClipboard(item.giftCode ?: "")
        }

    private fun copyToClipboard(textToCopy: String, label: String = "Copied Text") {
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, textToCopy)
        clipboard.setPrimaryClip(clip)

        Toast.makeText(requireContext(), "Text copied to clipboard", Toast.LENGTH_LONG).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.home_fragment_order_details,
            container,
            false
        )
        return binding.root
    }

    @SuppressLint("LogNotTimber")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[OrderDetailsViewModel::class.java]
        binding.viewModel = viewModel
        viewModel.mutableLiveData.observe(viewLifecycleOwner, this)

        viewModel.getOrderDetails(fragmentArgs.orderId)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        setBarName(requireActivity().getString(R.string.order_Details))

        initRecyclerView()
        setBtnListener()
        subscribeData()
    }

    private fun initRecyclerView() {
        binding.rvGiftCode.apply {
            giftCodeAdapter = GiftCodeAdapter(onClickIconCopyCallBack)
            adapter = giftCodeAdapter
        }
    }

    private fun setBtnListener() {
        binding.swipeLayout.setOnRefreshListener {
            binding.swipeLayout.isRefreshing = false
            viewModel.getOrderDetails(fragmentArgs.orderId)
        }

        binding.icChat.setOnClickListener {
            val dateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.US)
            val currentTime = dateFormat.format(Date())
            val messageModel = MessageModel(
                avaterstore = viewModel.orderDetails.order?.store?.thumbnail.toString(),
                avateruser = PrefMethods.getUserData()?.picture.toString(),
                messageType = "private",
                senderName = PrefMethods.getUserData()?.name.toString(),
                storename = viewModel.orderDetails.order?.store?.name.toString(),
                senderId = PrefMethods.getUserData()?.id.toString().trim(),
                storeId = viewModel.orderDetails.order?.store?.userId.toString(),
                message = "",
                messageId = UUID.randomUUID().toString(),
                date = currentTime,
                type = "",
                typeBay = "user"
            )

            // inboxViewModel.messageItem = messageModel

            val bundle = Bundle()
            bundle.putString("Avatar_Store", messageModel.avaterstore)
            bundle.putString("Avatar_User", messageModel.avateruser)
            bundle.putString("Sender_Id", messageModel.senderId)
            bundle.putString("Sender_Name", messageModel.senderName)
            bundle.putString("Store_Id", messageModel.storeId)
            bundle.putString("Store_Name", messageModel.storename)
            findNavController().navigate(R.id.messageFragment, bundle)
        }
    }

    private fun subscribeData() {
        observe(viewModel.apiResponseLiveData) {
            when (it.status) {
                Status.ERROR_MESSAGE -> {
                    showToast(it.message.toString(), 1)
                }

                Status.SUCCESS_MESSAGE -> {
                    showToast(it.message.toString(), 2)
                }

                Status.SUCCESS -> {
                    val orderDetails = it.data as OrderDetailsResponse
                    when (orderDetails.order!!.status) {
                        "pending" -> {
                            // binding.imgPending.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_not_completed_bg)
                            /*ImageViewCompat.setImageTintList(
                                binding.imgPending, ColorStateList.valueOf(
                                    ContextCompat.getColor(requireActivity(), R.color.hint_color)
                                )
                            )*/
                            //  binding.imgOnRoad.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_not_completed_bg)
                            // ImageViewCompat.setImageTintList(binding.imgOnRoad, null)
                            //   binding.imgDelivered.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_not_completed_bg)
                            // ImageViewCompat.setImageTintList(binding.imgDelivered, null)
                            //   binding.lineOnRoad.setImageResource(R.drawable.ic_status_not_completed)
                            //   binding.lineDelivered.setImageResource(R.drawable.ic_status_not_completed)
                            binding.tvPendingin.setTextColor(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color.hint_color
                                )
                            )
                            binding.tvOnRoad.setTextColor(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color.hint_color
                                )
                            )
                            binding.tvDelivered.setTextColor(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color.hint_color
                                )
                            )
                        }

                        "accepted" -> {
                            //  binding.imgPending.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_completed_bg)
                            //  binding.imgOnRoad.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_not_completed_bg)
                            // ImageViewCompat.setImageTintList(binding.imgOnRoad, null)
                            // binding.imgDelivered.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_not_completed_bg)
                            // ImageViewCompat.setImageTintList(binding.imgDelivered, null)
                            //   binding.lineOnRoad.setImageResource(R.drawable.ic_status_completed)
                            //   binding.lineDelivered.setImageResource(R.drawable.ic_status_not_completed)
                            binding.tvPendingin.setTextColor(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color.color_primary
                                )
                            )
                            binding.tvOnRoad.setTextColor(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color.hint_color
                                )
                            )
                            binding.tvDelivered.setTextColor(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color.hint_color
                                )
                            )
                        }

                        "shipping" -> {
                            //  binding.imgPending.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_completed_bg)
                            //  binding.imgOnRoad.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_completed_bg)
                            /*ImageViewCompat.setImageTintList(
                                binding.imgOnRoad, ColorStateList.valueOf(
                                    ContextCompat.getColor(requireActivity(), R.color.color_primary)
                                )
                            )*/
                            //    binding.imgDelivered.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_not_completed_bg)
                            // ImageViewCompat.setImageTintList(binding.imgDelivered, null)
                            //   binding.lineOnRoad.setImageResource(R.drawable.ic_status_completed)
                            //   binding.lineDelivered.setImageResource(R.drawable.ic_status_not_completed)
                            binding.tvPendingin.setTextColor(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color.color_primary
                                )
                            )
                            binding.tvOnRoad.setTextColor(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color.color_primary
                                )
                            )
                            binding.tvDelivered.setTextColor(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color.hint_color
                                )
                            )
                        }

                        "shipped" -> {
                            //  binding.imgPending.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_completed_bg)
                            //  binding.imgOnRoad.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_completed_bg)
                            /*ImageViewCompat.setImageTintList(
                                binding.imgOnRoad, ColorStateList.valueOf(
                                    ContextCompat.getColor(requireActivity(), R.color.color_primary)
                                )
                            )*/
                            //    binding.imgDelivered.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_not_completed_bg)
                            // ImageViewCompat.setImageTintList(binding.imgDelivered, null)
                            //    binding.lineOnRoad.setImageResource(R.drawable.ic_status_completed)
                            //   binding.lineDelivered.setImageResource(R.drawable.ic_status_completed)
                            binding.tvPendingin.setTextColor(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color.color_primary
                                )
                            )
                            binding.tvOnRoad.setTextColor(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color.color_primary
                                )
                            )
                            binding.tvDelivered.setTextColor(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color.hint_color
                                )
                            )
                        }

                        "completed" -> {
                            //     binding.imgPending.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_completed_bg)
                            //     binding.imgOnRoad.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_completed_bg)
                            /*ImageViewCompat.setImageTintList(
                                binding.imgOnRoad, ColorStateList.valueOf(
                                    ContextCompat.getColor(requireActivity(), R.color.color_primary)
                                )
                            )*/
                            //     binding.imgDelivered.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_completed_bg)
                            /*ImageViewCompat.setImageTintList(
                                binding.imgDelivered, ColorStateList.valueOf(
                                    ContextCompat.getColor(requireActivity(), R.color.color_primary)
                                )
                            )*/
                            //    binding.lineOnRoad.setImageResource(R.drawable.ic_status_completed)
                            //    binding.lineDelivered.setImageResource(R.drawable.ic_status_completed)
                            binding.tvPendingin.setTextColor(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color.color_primary
                                )
                            )
                            binding.tvOnRoad.setTextColor(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color.color_primary
                                )
                            )
                            binding.tvDelivered.setTextColor(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color.color_primary
                                )
                            )

                            binding.btnReviewOrder.visibility = View.VISIBLE
                            binding.btnReviewOrder.setOnClickListener {
                                val bundle = Bundle()
                                bundle.putInt(
                                    "ORDER_ID",
                                    orderDetails.order.id ?: fragmentArgs.orderId
                                )
                                findNavController().navigate(R.id.newRateOrderFragment, bundle)
                            }

                            if (orderDetails.offers?.isNotEmpty() == true || orderDetails.offers?.size != 0) {
                                binding.rvGiftCode.visibility = View.VISIBLE

                                giftCodeAdapter.submitData(orderDetails.offers)
                            }
                        }

                        else -> {
                            //     binding.imgPending.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_not_completed_bg)
                            //     binding.imgOnRoad.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_not_completed_bg)
                            ImageViewCompat.setImageTintList(binding.imgOnRoad, null)
                            //      binding.imgDelivered.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_not_completed_bg)
                            ImageViewCompat.setImageTintList(binding.imgDelivered, null)
                            //      binding.lineOnRoad.setImageResource(R.drawable.ic_status_not_completed)
                            //     binding.lineDelivered.setImageResource(R.drawable.ic_status_not_completed)
                            binding.tvPendingin.setTextColor(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color.hint_color
                                )
                            )
                            binding.tvOnRoad.setTextColor(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color.hint_color
                                )
                            )
                            binding.tvDelivered.setTextColor(
                                ContextCompat.getColor(
                                    requireActivity(),
                                    R.color.hint_color
                                )
                            )
                        }
                    }

                    if (orderDetails.order.hasPackaging == 0) {
                        binding.txtPackaging.visibility = View.GONE
                        binding.packagingPrice.visibility = View.GONE
                    }

                    if (orderDetails.shipping?.skuCode == "Receipt_from_store") {
                        binding.tvAddress.text = orderDetails.order.store?.address ?: ""
                        binding.tvAddressDetails.text = orderDetails.order.store?.address ?: ""
                        binding.tvPhoneNumber.text =
                            getString(R.string.phone_number) + " " + viewModel.orderDetails.order?.store?.phoneNumber
                    } else {
                        binding.tvAddress.text = orderDetails.order.streetName ?: ""
                        binding.tvAddressDetails.text = orderDetails.order.streetName ?: ""
                        binding.tvPhoneNumber.text =
                            getString(R.string.phone_number) + " " + viewModel.orderDetails.order?.phoneNumber
                    }

                    binding.addressLayout.setOnClickListener {
                        if (orderDetails.shipping?.skuCode == "Receipt_from_store") {
                            navigateToGoogleMaps(
                                orderDetails.order.store?.lat?.toDouble(),
                                orderDetails.order.store?.lng?.toDouble()
                            )
                        } else {
                            navigateToGoogleMaps(
                                orderDetails.order.lat?.toDouble(),
                                orderDetails.order.lng?.toDouble()
                            )
                        }
                    }
                }

                else -> {
                    Timber.e(it.message)
                }
            }
        }
    }

    private fun navigateToGoogleMaps(latitude: Double?, longitude: Double?) {
        val uri = Uri.parse("google.navigation:q=$latitude,$longitude")

        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
        mapIntent.setPackage("com.google.android.apps.maps")

        if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
            requireActivity().startActivity(mapIntent)
        } else {
            // If Google Maps app is not available, you can open the web version
            val webIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$latitude,$longitude")
            )
            requireActivity().startActivity(webIntent)
        }
    }

    override fun onChanged(value: Any?) {
        if (value == null) return
        when (value) {
            Codes.CONFIRM_ORDER -> {
            }

            Codes.PHONE_CLICKED -> {
                Utils.callPhone(
                    requireActivity(),
                    viewModel.orderDetails.order!!.phoneNumber.toString()
                )
            }

            Codes.WHATSAPP_CLICKED -> {
                val url =
                    "https://api.whatsapp.com/send?phone=" + viewModel.orderDetails.order!!.phoneNumber.toString()
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            }

        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        observe(viewModel.isMapReady) {
            when (it) {
                true -> {
                    googleMap.clear()
                    val latLng = LatLng(
                        viewModel.orderDetails.order?.lat?.toDouble() ?: 0.0,
                        viewModel.orderDetails.order?.lng?.toDouble() ?: 0.0
                    )
                    val option = MarkerOptions().position(latLng)
                    googleMap.addMarker(option)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, Const.zoomLevel))
                }

                else -> {}
            }
        }
    }
}