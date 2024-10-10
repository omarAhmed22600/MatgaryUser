package com.brandsin.user.ui.activity.auth

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import com.brandsin.user.ui.activity.BaseFragment

open class BaseAuthFragment : BaseFragment()
{
    override fun onDestroyView() {
        super.onDestroyView()
        if (!isNavigated)
            requireActivity().onBackPressedDispatcher.addCallback(this) {
                val navController = findNavController()
                if (navController.currentBackStackEntry?.destination?.id != null)
                {
                    findNavController().navigateUp()
                }
                else
                    navController.popBackStack()
            }
    }

    fun setBarName(title: String)
    {
        (requireActivity() as AuthActivity).run {
            viewModel?.obsTitle!!.set(title)
        }
    }
}

