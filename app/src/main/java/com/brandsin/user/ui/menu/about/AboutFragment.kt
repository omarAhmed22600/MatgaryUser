package com.brandsin.user.ui.menu.about

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.brandsin.user.R
import com.brandsin.user.databinding.HomeFragmentAboutBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.menu.about.AboutItem
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.utils.Utils
import es.dmoral.toasty.Toasty

class AboutFragment : BaseHomeFragment(), Observer<Any?> {

    private lateinit var binding: HomeFragmentAboutBinding

    private lateinit var viewModel: AboutViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment_about, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[AboutViewModel::class.java]
        binding.viewModel = viewModel

        setBarName(getString(R.string.about_app))

        viewModel.mutableLiveData.observe(viewLifecycleOwner, this)
        viewModel.aboutAdapter.aboutLiveData.observe(viewLifecycleOwner, this)

        try {
            val pInfo: PackageInfo =
                requireActivity().packageManager.getPackageInfo(requireActivity().packageName, 0)
            viewModel.obsVersion.set("${getString(R.string.version_number)} ${pInfo.versionName}")
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    override fun onChanged(value: Any?) {
        if (value == null) return
        when (value) {
            Codes.OPEN_FACE -> {
                Utils.openLink(requireActivity(), viewModel.socialLinks.facebook)
            }

            Codes.OPEN_TWITTER -> {
                Utils.openLink(requireActivity(), viewModel.socialLinks.twitter)
            }

            Codes.TIKTOK_CLICKED -> {
                Utils.openLink(requireActivity(), viewModel.socialLinks.tikTok.toString())
            }

            Codes.OPEN_INSTA -> {
                Utils.openInstagram(requireActivity(), viewModel.socialLinks.instagram)
            }

            is AboutItem -> {
                when (value.id) {
                    1 -> {
                        findNavController().navigate(R.id.about_to_common_questions)
                    }

                    2 -> {
                        // findNavController().navigate(R.id.about_to_rate_app)
                        val packageName = "com.brandsin.user"
                        val uri = Uri.parse("market://details?id=$packageName")
                        val myAppLinkToMarket = Intent(Intent.ACTION_VIEW, uri)
                        try {
                            startActivity(myAppLinkToMarket)
                        } catch (e: ActivityNotFoundException) {
                            Toasty.warning(
                                requireActivity(),
                                "Impossible to find an application for the market"
                            ).show()
                        }
                    }
                }
            }
        }
    }
}