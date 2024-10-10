package com.brandsin.user.ui.menu.contact

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.brandsin.user.R
import com.brandsin.user.databinding.HomeFragmentContactBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.utils.Utils

class ContactFragment : BaseHomeFragment(), Observer<Any?> {

    private lateinit var binding: HomeFragmentContactBinding
    private lateinit var viewModel: ContactViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.home_fragment_contact, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[ContactViewModel::class.java]
        binding.viewModel = viewModel

        binding.swipeLayout.setOnRefreshListener {
            binding.swipeLayout.isRefreshing = false
            viewModel.getPhoneNumber()
            viewModel.getSocialLinks()
        }

        viewModel.mutableLiveData.observe(viewLifecycleOwner, this)
        viewModel.mutableLiveData.observe(viewLifecycleOwner, this)

        setBarName(getString(R.string.contact_us))
    }

    override fun onChanged(value: Any?) {
        if (value == null) return
        when (value) {
            Codes.SHOW_SOCIAL -> {
                if (viewModel.socialLinks.facebook == null) {
                    binding.ivFacebook.visibility = View.GONE
                }
                if (viewModel.socialLinks.twitter == null) {
                    binding.ivTwitter.visibility = View.GONE
                }
                /*if (viewModel.socialLinks.pinterest == null) {
                    binding.ivGmail.visibility = View.GONE
                }*/
                if (viewModel.socialLinks.tikTok == null) {
                    binding.ivTiktok.visibility = View.GONE
                }
            }

            Codes.PHONE_CLICKED -> {
                Utils.callPhone(requireActivity(), viewModel.obsPhoneNumber.get().toString())
            }

            Codes.FACE_CLICKED -> {
//                if (viewModel.socialLinks.facebook!!.contains('/')){
//                    Utils.openFacebook(requireActivity(), viewModel.socialLinks.facebook!!.removeSuffix("/"))
//                }
                Utils.openLink(requireActivity(), viewModel.socialLinks.facebook)

            }

            Codes.OPEN_INSTA -> {
                Utils.openInstagram(requireActivity(), viewModel.socialLinks.instagram)
            }

            Codes.GMAIL_CLICKED -> {
                Utils.openMail(requireActivity(), viewModel.socialLinks.pinterest.toString())
            }

            Codes.TIKTOK_CLICKED -> {
                Utils.openLink(requireActivity(), viewModel.socialLinks.tikTok.toString())
            }

            Codes.WHATSAPP_CLICKED -> {
                val url = "https://api.whatsapp.com/send?phone=" + viewModel.obsPhoneNumber.get()
                    .toString()
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            }

            Codes.TWITTER_CLICKED -> {
                Utils.openLink(requireActivity(), viewModel.socialLinks.twitter)
            }
        }
    }
}