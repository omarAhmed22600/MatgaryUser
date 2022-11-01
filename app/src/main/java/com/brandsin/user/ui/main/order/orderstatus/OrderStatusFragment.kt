package com.brandsin.user.ui.main.order.orderstatus

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.brandsin.user.R
import com.brandsin.user.databinding.HomeFragmentOrderStatusBinding
import com.brandsin.user.model.order.details.OrderDetailsResponse
import com.brandsin.user.network.Status
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.utils.map.observe
import timber.log.Timber

@Suppress("DEPRECATION")
class OrderStatusFragment : BaseHomeFragment()
{
    private lateinit var binding : HomeFragmentOrderStatusBinding
    private lateinit var viewModel: OrderStatusViewModel
    private val fragmentArgs : OrderStatusFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment_order_status, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(OrderStatusViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.getOrderDetails(fragmentArgs.orderId)

        setBarName(requireActivity().getString(R.string.order_status))

        binding.swipeLayout.setOnRefreshListener {
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
                           // binding.imgPending.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_not_completed_bg)
                            ImageViewCompat.setImageTintList(binding.imgPending, ColorStateList.valueOf(
                                ContextCompat.getColor(requireActivity(), R.color.hint_color)))
                          //  binding.imgOnRoad.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_not_completed_bg)
                            ImageViewCompat.setImageTintList(binding.imgOnRoad, null)
                         //   binding.imgDelivered.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_not_completed_bg)
                            ImageViewCompat.setImageTintList(binding.imgDelivered, null)
                         //   binding.lineOnRoad.setImageResource(R.drawable.ic_status_not_completed)
                         //   binding.lineDelivered.setImageResource(R.drawable.ic_status_not_completed)
                            binding.tvPendingin.setTextColor( ContextCompat.getColor(requireActivity(),R.color.hint_color))
                            binding.tvOnRoad.setTextColor( ContextCompat.getColor(requireActivity(),R.color.hint_color))
                            binding.tvDelivered.setTextColor( ContextCompat.getColor(requireActivity(),R.color.hint_color))
                        }
                        "accepted" -> {
                          //  binding.imgPending.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_completed_bg)
                          //  binding.imgOnRoad.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_not_completed_bg)
                            ImageViewCompat.setImageTintList(binding.imgOnRoad, null)
                           // binding.imgDelivered.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_not_completed_bg)
                            ImageViewCompat.setImageTintList(binding.imgDelivered, null)
                         //   binding.lineOnRoad.setImageResource(R.drawable.ic_status_completed)
                         //   binding.lineDelivered.setImageResource(R.drawable.ic_status_not_completed)
                            binding.tvPendingin.setTextColor( ContextCompat.getColor(requireActivity(),R.color.color_primary))
                            binding.tvOnRoad.setTextColor( ContextCompat.getColor(requireActivity(),R.color.hint_color))
                            binding.tvDelivered.setTextColor( ContextCompat.getColor(requireActivity(),R.color.hint_color))
                        }
                        "shipping" -> {
                          //  binding.imgPending.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_completed_bg)
                          //  binding.imgOnRoad.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_completed_bg)
                            ImageViewCompat.setImageTintList(binding.imgOnRoad, ColorStateList.valueOf(
                                ContextCompat.getColor(requireActivity(), R.color.color_primary)))
                        //    binding.imgDelivered.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_not_completed_bg)
                            ImageViewCompat.setImageTintList(binding.imgDelivered, null)
                         //   binding.lineOnRoad.setImageResource(R.drawable.ic_status_completed)
                         //   binding.lineDelivered.setImageResource(R.drawable.ic_status_not_completed)
                            binding.tvPendingin.setTextColor( ContextCompat.getColor(requireActivity(),R.color.color_primary))
                            binding.tvOnRoad.setTextColor( ContextCompat.getColor(requireActivity(),R.color.color_primary))
                            binding.tvDelivered.setTextColor( ContextCompat.getColor(requireActivity(),R.color.hint_color))
                        }
                        "shipped" -> {
                          //  binding.imgPending.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_completed_bg)
                          //  binding.imgOnRoad.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_completed_bg)
                            ImageViewCompat.setImageTintList(binding.imgOnRoad, ColorStateList.valueOf(
                                ContextCompat.getColor(requireActivity(), R.color.color_primary)))
                        //    binding.imgDelivered.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_not_completed_bg)
                            ImageViewCompat.setImageTintList(binding.imgDelivered, null)
                        //    binding.lineOnRoad.setImageResource(R.drawable.ic_status_completed)
                         //   binding.lineDelivered.setImageResource(R.drawable.ic_status_completed)
                            binding.tvPendingin.setTextColor( ContextCompat.getColor(requireActivity(),R.color.color_primary))
                            binding.tvOnRoad.setTextColor( ContextCompat.getColor(requireActivity(),R.color.color_primary))
                            binding.tvDelivered.setTextColor( ContextCompat.getColor(requireActivity(),R.color.hint_color))
                        }
                        "completed" -> {
                       //     binding.imgPending.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_completed_bg)
                       //     binding.imgOnRoad.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_completed_bg)
                            ImageViewCompat.setImageTintList(binding.imgOnRoad, ColorStateList.valueOf(
                                ContextCompat.getColor(requireActivity(), R.color.color_primary)))
                       //     binding.imgDelivered.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_completed_bg)
                            ImageViewCompat.setImageTintList(binding.imgDelivered, ColorStateList.valueOf(
                                ContextCompat.getColor(requireActivity(), R.color.color_primary)))
                        //    binding.lineOnRoad.setImageResource(R.drawable.ic_status_completed)
                        //    binding.lineDelivered.setImageResource(R.drawable.ic_status_completed)
                            binding.tvPendingin.setTextColor( ContextCompat.getColor(requireActivity(),R.color.color_primary))
                            binding.tvOnRoad.setTextColor( ContextCompat.getColor(requireActivity(),R.color.color_primary))
                            binding.tvDelivered.setTextColor( ContextCompat.getColor(requireActivity(),R.color.color_primary))
                        }
                        else -> {
                       //     binding.imgPending.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_not_completed_bg)
                       //     binding.imgOnRoad.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_not_completed_bg)
                            ImageViewCompat.setImageTintList(binding.imgOnRoad, null)
                      //      binding.imgDelivered.background = ContextCompat.getDrawable(requireActivity(), R.drawable.status_not_completed_bg)
                            ImageViewCompat.setImageTintList(binding.imgDelivered, null)
                      //      binding.lineOnRoad.setImageResource(R.drawable.ic_status_not_completed)
                       //     binding.lineDelivered.setImageResource(R.drawable.ic_status_not_completed)
                            binding.tvPendingin.setTextColor( ContextCompat.getColor(requireActivity(),R.color.hint_color))
                            binding.tvOnRoad.setTextColor( ContextCompat.getColor(requireActivity(),R.color.hint_color))
                            binding.tvDelivered.setTextColor( ContextCompat.getColor(requireActivity(),R.color.hint_color))

                        }
                    }
                }
                else -> {
                    Timber.e(it.message)
                }
            }
        }
    }
}