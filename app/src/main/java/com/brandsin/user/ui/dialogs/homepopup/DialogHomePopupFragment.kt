package com.brandsin.user.ui.dialogs.homepopup

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

import com.brandsin.user.databinding.DialogHomePopupBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Params
import com.brandsin.user.model.order.homenew.HomeNewResponse
import com.brandsin.user.model.order.homenew.PopupsItem
import com.brandsin.user.utils.PrefMethods
import com.brandsin.user.utils.map.observe
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView

class DialogHomePopupFragment : DialogFragment(), Observer<Any?>
{
    lateinit  var  binding: DialogHomePopupBinding
    lateinit var viewModel: DialogHomePopupViewModel
    var homeNewResponse = HomeNewResponse()
    var popupList  = mutableListOf<PopupsItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when {
            arguments != null -> {
                when {
                    requireArguments().containsKey(Params.DIALOG_HOME_POPUP) -> {
                        homeNewResponse = (requireArguments().getSerializable(Params.DIALOG_HOME_POPUP) as HomeNewResponse?)!!
                        popupList = homeNewResponse.popups as MutableList<PopupsItem>
                    }
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = DialogHomePopupBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(DialogHomePopupViewModel::class.java)
        binding.viewModel = viewModel

        setupSlider(popupList)

        viewModel.mutableLiveData.observe(this, this)

        observe(viewModel.sliderAdapter.popupSliderLiveData) {
            when(it) {
                is PopupsItem -> {
                    PrefMethods.saveHomePopup(false)
                    val intent = Intent()
                    intent.putExtra(Params.DIALOG_CLICK_ACTION, 1)
                    intent.putExtra(Params.DIALOG_HOME_POPUP, it)
                    requireActivity().setResult(Codes.DIALOG_HOME_POPUP, intent)
                    requireActivity().finish()
                }
            }
        }

        binding.close.setOnClickListener {
            PrefMethods.saveHomePopup(false)
            requireActivity().finish()
        }

        return binding.root
    }

    override fun onChanged(it: Any?) {
        when (it) {
            null -> return
            else -> when (it) {

            }
        }
    }

    private fun setupSlider(sliders: List<PopupsItem?>?) {
        viewModel.sliderAdapter.updateList(sliders as List<PopupsItem>)
        binding.bannerSlider.setSliderAdapter(viewModel.sliderAdapter)
        binding.bannerSlider.setIndicatorAnimation(IndicatorAnimationType.WORM)
        binding.bannerSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        binding.bannerSlider.autoCycleDirection = SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH
        binding.bannerSlider.indicatorSelectedColor = Color.WHITE
        binding.bannerSlider.indicatorUnselectedColor = Color.GRAY
        binding.bannerSlider.scrollTimeInSec = 3
        binding.bannerSlider.isAutoCycle = true
        binding.bannerSlider.startAutoCycle()
    }
}