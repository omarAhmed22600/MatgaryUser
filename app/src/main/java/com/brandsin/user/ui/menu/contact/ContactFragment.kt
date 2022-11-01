package com.brandsin.user.ui.menu.contact

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.brandsin.user.R
import com.brandsin.user.databinding.HomeFragmentContactBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.utils.Utils

class ContactFragment : BaseHomeFragment(), Observer<Any?>
{
    private lateinit var binding : HomeFragmentContactBinding
    private lateinit var viewModel: ContactViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment_contact, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(ContactViewModel::class.java)
        binding.viewModel = viewModel

        binding.swipeLayout.setOnRefreshListener {
            binding.swipeLayout.isRefreshing = false
            viewModel.getPhoneNumber()
            viewModel.getSocialLinks()
        }

        viewModel.mutableLiveData.observe(viewLifecycleOwner, this)

        setBarName(getString(R.string.contact_us))
    }

    override fun onChanged(it: Any?)
    {
        if(it == null) return
        when (it) {
            Codes.PHONE_CLICKED -> {
                Utils.callPhone(requireActivity(), viewModel.obsPhoneNumber.get().toString())
            }
            Codes.FACE_CLICKED -> {
                Utils.openFacebook(requireActivity(), viewModel.socialLinks.facebook)
            }
            Codes.GMAIL_CLICKED -> {
                Utils.openMail(requireActivity(), viewModel.socialLinks.pinterest.toString())
            }
            Codes.TIKTOK_CLICKED -> {
                Utils.openInstagram(requireActivity(), viewModel.socialLinks.tikTok.toString())
            }
            Codes.WHATSAPP_CLICKED -> {
                val url = "https://api.whatsapp.com/send?phone=" + viewModel.obsPhoneNumber.get().toString()
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(url)
                startActivity(i)
            }
            Codes.TWITTER_CLICKED -> {
                Utils.openTwitter(requireActivity(), viewModel.socialLinks.twitter)
            }
        }
    }
}