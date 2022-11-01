package com.brandsin.user.ui.main.order.orderdetails

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.brandsin.user.R
import com.brandsin.user.databinding.HomeFragmentOrderDetailsBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Const
import com.brandsin.user.model.order.details.OrderDetailsResponse
import com.brandsin.user.network.Status
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.utils.Utils
import com.brandsin.user.utils.map.observe
import timber.log.Timber

@Suppress("DEPRECATION")
class OrderDetailsFragment : BaseHomeFragment(), Observer<Any?>, OnMapReadyCallback {
    private lateinit var binding: HomeFragmentOrderDetailsBinding
    private lateinit var viewModel: OrderDetailsViewModel
    private val fragmentArgs: OrderDetailsFragmentArgs by navArgs()

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(OrderDetailsViewModel::class.java)
        binding.viewModel = viewModel
        viewModel.mutableLiveData.observe(viewLifecycleOwner, this)

        viewModel.getOrderDetails(fragmentArgs.orderId)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)

        setBarName(requireActivity().getString(R.string.order_Details))

        binding.swipeLayout.setOnRefreshListener {
            binding.swipeLayout.isRefreshing = false
            viewModel.getOrderDetails(fragmentArgs.orderId)
        }
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

                            binding.imgPending.setImageResource(R.drawable.one_undone)
                            binding.imgOnRoad.setImageResource(R.drawable.two_undone)
                            binding.imgDelivered.setImageResource(R.drawable.three_undone)
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
                            binding.imgOnRoad.setImageResource(R.drawable.two_undone)
                            binding.imgDelivered.setImageResource(R.drawable.three_undone)
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
                            binding.imgOnRoad.setImageResource(R.drawable.two_done)
                            binding.imgDelivered.setImageResource(R.drawable.three_done)
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
                            binding.imgOnRoad.setImageResource(R.drawable.two_done)
                            binding.imgDelivered.setImageResource(R.drawable.three_undone)
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
                            binding.imgOnRoad.setImageResource(R.drawable.two_done)
                            binding.imgDelivered.setImageResource(R.drawable.three_done)
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
                        }
                        else -> {
                            binding.imgOnRoad.setImageResource(R.drawable.two_undone)
                            binding.imgDelivered.setImageResource(R.drawable.three_undone)
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
                }
                else -> {
                    Timber.e(it.message)
                }
            }
        }


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
                            ImageViewCompat.setImageTintList(
                                binding.imgPending, ColorStateList.valueOf(
                                    ContextCompat.getColor(requireActivity(), R.color.hint_color)
                                )
                            )
                            //  binding.imgOnRoad.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_not_completed_bg)
                            ImageViewCompat.setImageTintList(binding.imgOnRoad, null)
                            //   binding.imgDelivered.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_not_completed_bg)
                            ImageViewCompat.setImageTintList(binding.imgDelivered, null)
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
                            ImageViewCompat.setImageTintList(binding.imgOnRoad, null)
                            // binding.imgDelivered.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_not_completed_bg)
                            ImageViewCompat.setImageTintList(binding.imgDelivered, null)
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
                            ImageViewCompat.setImageTintList(
                                binding.imgOnRoad, ColorStateList.valueOf(
                                    ContextCompat.getColor(requireActivity(), R.color.color_primary)
                                )
                            )
                            //    binding.imgDelivered.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_not_completed_bg)
                            ImageViewCompat.setImageTintList(binding.imgDelivered, null)
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
                            ImageViewCompat.setImageTintList(
                                binding.imgOnRoad, ColorStateList.valueOf(
                                    ContextCompat.getColor(requireActivity(), R.color.color_primary)
                                )
                            )
                            //    binding.imgDelivered.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_not_completed_bg)
                            ImageViewCompat.setImageTintList(binding.imgDelivered, null)
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
                            ImageViewCompat.setImageTintList(
                                binding.imgOnRoad, ColorStateList.valueOf(
                                    ContextCompat.getColor(requireActivity(), R.color.color_primary)
                                )
                            )
                            //     binding.imgDelivered.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_completed_bg)
                            ImageViewCompat.setImageTintList(
                                binding.imgDelivered, ColorStateList.valueOf(
                                    ContextCompat.getColor(requireActivity(), R.color.color_primary)
                                )
                            )
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
                }
                else -> {
                    Timber.e(it.message)
                }
            }
        }
    }

    override fun onChanged(it: Any?) {
        if (it == null) return
        when (it) {
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

    override fun onMapReady(googleMap: GoogleMap?) {
        observe(viewModel.isMapReady) {
            when (it) {
                true -> {
                    googleMap!!.clear()
                    val latLng = LatLng(
                        viewModel.orderDetails.order!!.lat!!.toDouble(),
                        viewModel.orderDetails.order!!.lng!!.toDouble()
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