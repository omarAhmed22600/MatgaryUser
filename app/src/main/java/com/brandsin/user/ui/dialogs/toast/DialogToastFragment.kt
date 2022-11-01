package com.brandsin.user.ui.dialogs.toast

import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.brandsin.user.R
import com.brandsin.user.databinding.DialogToastBinding
import com.brandsin.user.model.constants.Params
import com.brandsin.user.ui.activity.DialogActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DialogToastFragment : DialogFragment()
{
    private lateinit var binding: DialogToastBinding
    var message = ""
    var toastType = 1

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        if (arguments != null)
        {
            if (requireArguments().containsKey(Params.DIALOG_TOAST_MESSAGE))
            {
                message = requireArguments().getString(Params.DIALOG_TOAST_MESSAGE, "")
                toastType = requireArguments().getInt(Params.DIALOG_TOAST_TYPE, 1)
            }
        }
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        binding = DialogToastBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        binding.tvMessage.text = message

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            val window: Window = requireActivity().window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(requireActivity(), R.color.offers_bg_color)
        }

        /*
        * toastType == 1 >> failed toast
        * toastType == 2 >> success toast
        */
        if (toastType == 1)
        {
            binding.ivToast.setImageResource(R.drawable.ic_toast_failed)
            binding.toastLayout.background = ContextCompat.getDrawable(requireActivity(), R.drawable.toast_failed_bg)
        }
        else
        {
            binding.ivToast.setImageResource(R.drawable.ic_toast_success)
            binding.toastLayout.background = ContextCompat.getDrawable(requireActivity(), R.drawable.toast_success_bg)
        }

        lifecycleScope.launch {
            delay(2000)
            (activity as DialogActivity).finish()
        }
    }
}
