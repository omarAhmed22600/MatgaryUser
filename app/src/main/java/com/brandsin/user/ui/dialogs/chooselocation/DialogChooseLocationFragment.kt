package com.brandsin.user.ui.dialogs.chooselocation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.brandsin.user.databinding.DialogChooseLocationBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Params
import com.brandsin.user.model.location.addresslist.AddressListItem
import com.brandsin.user.network.ResponseHandler
import com.brandsin.user.ui.dialogs.chooselocation.adapter.ChooseAddressAdapter
import com.brandsin.user.ui.dialogs.chooselocation.viewmodel.ChooseAddressViewModel
import com.brandsin.user.utils.PrefMethods

class DialogChooseLocationFragment : DialogFragment() {

    private lateinit var binding: DialogChooseLocationBinding

    private val viewModel: ChooseAddressViewModel by viewModels()

    private lateinit var chooseAddressAdapter: ChooseAddressAdapter

    private val onItemClickCallBack: (address: AddressListItem) -> Unit = { address ->
        PrefMethods.saveDefaultAddress(address)
        requireActivity().finish()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DialogChooseLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // call get all addresses api
        viewModel.getAllAddresses()

        initRecycler()
        setBtnListener()
        subscribeData()
    }

    private fun initRecycler() {
        binding.rvListAddresses.apply {
            chooseAddressAdapter = ChooseAddressAdapter(onItemClickCallBack)
            adapter = chooseAddressAdapter
        }
    }

    private fun setBtnListener() {
        binding.consYourLocation.setOnClickListener {
            val intent = Intent()
            intent.putExtra(Params.DIALOG_CLICK_ACTION, 0)
            requireActivity().setResult(Codes.DIALOG_ORDER_SUCCESS, intent)
            requireActivity().finish()
        }

        binding.consYourAddress.setOnClickListener {
            val intent = Intent()
            intent.putExtra(Params.USER_LOCATION, 1)
            requireActivity().setResult(Codes.GETTING_USER_LOCATION, intent)
            requireActivity().finish()
        }
    }

    private fun subscribeData() {
        viewModel.getListAddressResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseHandler.Success -> {
                    // bind data to the view
                    chooseAddressAdapter.submitData(it.data?.addressesList)
                }

                is ResponseHandler.Error -> {
                    // show error message
                    viewModel.obsIsEmpty.set(true)
                }

                is ResponseHandler.Loading -> {
                    // show a progress bar
                    viewModel.obsIsLoading.set(true)
                }

                is ResponseHandler.StopLoading -> {
                    // show a progress bar
                    viewModel.obsIsLoading.set(false)
                }

                else -> {}
            }
        }
    }
}