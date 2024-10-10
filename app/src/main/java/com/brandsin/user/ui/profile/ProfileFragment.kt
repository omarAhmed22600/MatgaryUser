package com.brandsin.user.ui.profile

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.brandsin.user.R
import com.brandsin.user.databinding.ProfileFragmentEditBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Params
import com.brandsin.user.model.location.addresslist.AddressListItem
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.ui.activity.home.HomeActivity
import com.brandsin.user.ui.location.address.ListAddressesActivity
import com.brandsin.user.utils.PrefMethods
import timber.log.Timber
import java.util.Locale

class ProfileFragment : BaseHomeFragment() {
    lateinit var binding: ProfileFragmentEditBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.profile_fragment_edit, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBarName(getString(R.string.edit_profile))

        binding.layoutEdit.setOnClickListener {
            findNavController().navigate(R.id.profile_to_update)
        }

        binding.addressLayout.setOnClickListener {
            startActivityForResult(
                (Intent(requireActivity(), ListAddressesActivity::class.java))
                    .putExtra(Params.DELIVERY_ADDRESSES_FLAG, 3), Codes.SHOW_DELIVERY_ADDRESSES_CODE
            )
        }

        binding.changePassLayout.setOnClickListener {
            findNavController().navigate(R.id.profile_to_change_pass)
        }

        when {
            PrefMethods.getLanguage() == "ar" -> {
                binding.tvLanguage.text = "English"
            }

            else -> {
                binding.tvLanguage.text = "العربية"
            }
        }

        binding.languageLayout.setOnClickListener {
            when {
                PrefMethods.getLanguage() == "ar" -> {
                    setLanguage("en")
                }

                else -> {
                    setLanguage("ar")
                }
            }
        }

        binding.ibSwitch.isChecked = PrefMethods.getIsNotificationsEnabled()!!

        binding.notificationLayout.setOnClickListener {
            when {
                binding.ibSwitch.isChecked -> {
                    binding.ibSwitch.isChecked = false
                    PrefMethods.setIsNotificationsEnabled(false)
                }

                else -> {
                    binding.ibSwitch.isChecked = true
                    PrefMethods.setIsNotificationsEnabled(true)
                }
            }
        }
        binding.ibSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                PrefMethods.setIsNotificationsEnabled(true)
            } else {
                PrefMethods.setIsNotificationsEnabled(false)
            }
        }
    }

    private fun setLanguage(language: String?) {
        val mainConfig = Configuration(resources.configuration)
        val locale = Locale(language)
        Locale.setDefault(locale)
        mainConfig.setLocale(locale)
        resources.updateConfiguration(mainConfig, null)
        PrefMethods.setLanguage(language.toString())
        val intent = Intent(activity, HomeActivity::class.java)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        /* When This page opened navigate user to select his default address from delivery addresses list */
        when (requestCode) {
            Codes.SHOW_DELIVERY_ADDRESSES_CODE -> {
                when (data) {
                    null -> {
                        Timber.e("no changes detected")
                    }

                    else -> {
                        when {
                            data.hasExtra(Params.ADDRESS_ITEM) -> {
                                val addressItem =
                                    data.getParcelableExtra<AddressListItem>(Params.ADDRESS_ITEM)
                                PrefMethods.saveDefaultAddress(addressItem)
                            }
                        }
                    }
                }
            }
        }
    }
}