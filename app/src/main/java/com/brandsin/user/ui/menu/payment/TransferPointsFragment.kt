package com.brandsin.user.ui.menu.payment

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.brandsin.user.R
import com.brandsin.user.databinding.FragmentTransferPointsBinding
import com.brandsin.user.ui.activity.home.BaseHomeFragment

class TransferPointsFragment : BaseHomeFragment() {

    private var _binding: FragmentTransferPointsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: PaymentViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransferPointsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBarName(getString(R.string.transfer_points))

        setBtnListener()
    }

    private val PICK_CONTACT_REQUEST = 1

    // Function to open the contact picker
    private fun openContactPicker() {
        val contactPickerIntent =
            Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
        startActivityForResult(contactPickerIntent, PICK_CONTACT_REQUEST)
    }

    private fun setBtnListener() {
        binding.icOpenContact.setOnClickListener {
            openContactPicker()
        }

        binding.btnContinuation.setOnClickListener {
            if (validate()) {
                findNavController().navigate(R.id.confirmTransferPointsFragment)
            }
        }
    }

    private fun validate(): Boolean {
        val isValid = true

        viewModel.recipientName = binding.edtRecipientName.text.toString().trim()
        viewModel.recipientMobileNumber = binding.edtRecipientMobileNumber.text.toString().trim()
        viewModel.numberOfPoints = binding.edtNumberCountPoints.text.toString().trim()

        if (viewModel.recipientName.isNullOrEmpty()) {
            binding.edtRecipientName.error = getString(R.string.enter_recipient_name)
            showToast(getString(R.string.enter_recipient_name), 1)
            return false
        } else {
            binding.edtRecipientName.error = null
        }

        if (!isPhoneNumberStartingWith05(viewModel.recipientMobileNumber!!)) {
            binding.edtRecipientMobileNumber.error =
                getString(R.string.phone_number_must_begin_by_05)
            showToast(getString(R.string.phone_number_must_begin_by_05), 1)
            return false
        } else {
            binding.edtRecipientMobileNumber.error = null
        }

        if (viewModel.recipientMobileNumber!!.length < 10) {
            binding.edtRecipientMobileNumber.error =
                getString(R.string.phone_number_must_consist_of_10_number)
            showToast(getString(R.string.phone_number_must_consist_of_10_number), 1)
            return false
        } else {
            binding.edtRecipientMobileNumber.error = null
        }

        if (viewModel.recipientMobileNumber.isNullOrEmpty()) {
            binding.edtRecipientMobileNumber.error = getString(R.string.required)
            showToast(getString(R.string.required), 1)
            return false
        } else {
            binding.edtRecipientMobileNumber.error = null
        }

        if (viewModel.numberOfPoints.isNullOrEmpty()) {
            binding.edtNumberCountPoints.error = getString(R.string.enter_number_of_points)
            showToast(getString(R.string.enter_number_of_points), 1)
            return false
        } else {
            binding.edtNumberCountPoints.error = null
        }

        return isValid
    }

    private fun isPhoneNumberStartingWith05(phoneNumber: String): Boolean {
        val pattern = "^05\\d{8}\$".toRegex()
        return pattern.matches(phoneNumber)
    }

    @SuppressLint("Range")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_CONTACT_REQUEST && data != null) {
            val contactUri: Uri = data.data!!

            val projection = arrayOf(
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DATA1
            )

            // Query the contact data
            val cursor =
                requireActivity().contentResolver.query(contactUri, projection, null, null, null)

            cursor?.use {
                if (it.moveToFirst()) {
                    // Retrieve the phone number
                    val phoneNumber =
                        it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                    // Use the phoneNumber as needed
                    // Toast.makeText(this, "Selected phone number: $phoneNumber", Toast.LENGTH_SHORT).show()
                    binding.edtRecipientMobileNumber.setText(phoneNumber)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}