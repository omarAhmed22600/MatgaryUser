package com.brandsin.user.ui.menu.notifications

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.brandsin.user.R
import com.brandsin.user.databinding.HomeFragmentNotificationsBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.menu.notifications.NotificationItem
import com.brandsin.user.model.order.homenew.SlidesItem
import com.brandsin.user.ui.activity.auth.AuthActivity
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.utils.PrefMethods
import com.brandsin.user.utils.map.observe
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView

class NotificationFragment : BaseHomeFragment(), Observer<Any?>
{
    private lateinit var viewModel: NotificationViewModel
    private lateinit var binding : HomeFragmentNotificationsBinding
    lateinit var sliderView : SliderView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment_notifications, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(NotificationViewModel::class.java)
        binding.viewModel = viewModel

        setBarName(getString(R.string.notifications))

        viewModel.mutableLiveData.observe(viewLifecycleOwner, this)
        viewModel.notificationAdapter.notificationsLiveData.observe(viewLifecycleOwner, this)

        binding.swipeLayout.setOnRefreshListener {
            binding.swipeLayout.isRefreshing = false
            viewModel.getUserStatus()
        }

        observe(viewModel.slidersResponse) {

            setupSlider(it!!.data!!.slides)

        }
    }

    private fun setupSlider(slides: List<SlidesItem>) {

        viewModel.moreSliderAdapter.updateList(slides)
        sliderView = requireActivity().findViewById(R.id.bannerSlider)
        sliderView.setSliderAdapter(viewModel.moreSliderAdapter)
        sliderView.setIndicatorAnimation(IndicatorAnimationType.WORM)
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        sliderView.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH
        sliderView.indicatorSelectedColor = Color.WHITE
        sliderView.indicatorUnselectedColor = Color.GRAY
        sliderView.scrollTimeInSec = 3
        sliderView.isAutoCycle = true
        sliderView.startAutoCycle()
    }

    override fun onChanged(it: Any?)
    {
        when (it) {
            null -> return
            else -> when (it) {
                Codes.LOGIN_CLICKED -> {
                    PrefMethods.saveIsAskedToLogin(true)
                    requireActivity().startActivity(Intent(requireActivity(),
                        AuthActivity::class.java))
                }
                is NotificationItem -> {
                    viewModel.notificationsList[viewModel.notificationsList.indexOf(it)].readAt == "read"
                    viewModel.notificationAdapter.updateList(viewModel.notificationsList)
                    viewModel.makeReadNotification(it)
                    when {
                        it.orderId != null -> {
                            val action = NotificationFragmentDirections.notificationToOrderDetails(it.orderId)
                            findNavController().navigate(action)
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        when {
            PrefMethods.getIsAskedToLogin() -> {
                viewModel.getUserStatus()
                PrefMethods.saveIsAskedToLogin(false)
            }
        }
    }
}