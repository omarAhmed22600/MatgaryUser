package com.brandsin.user.ui.main.order.storedetails.information

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.brandsin.user.R
import com.brandsin.user.databinding.HomeFragmentStoreInformationBinding
import com.brandsin.user.model.order.storedetails.StoreDetailsData
import com.brandsin.user.ui.activity.home.BaseHomeFragment

class StoreInformationFragment : BaseHomeFragment(), Observer<Any?> {
    private lateinit var viewModel: StoreInformationsViewModel
    lateinit var binding: HomeFragmentStoreInformationBinding
    private val storeInformationArgs: StoreInformationFragmentArgs by navArgs()
    var storeDetailsData = StoreDetailsData()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.home_fragment_store_information,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(StoreInformationsViewModel::class.java)

        binding.viewModel = viewModel
        storeDetailsData = storeInformationArgs.storeDetailsData
        viewModel.mutableLiveData.observe(viewLifecycleOwner, this)
        viewModel.storeData =storeDetailsData
        setBarName(getString(R.string.store_information))


        //viewModel.setShowProgress(true)
//        viewModel.getListStories(addStoriesArgs.storeId)
//        viewModel.showProgress().observe(viewLifecycleOwner, { aBoolean ->
//            if (!aBoolean!!) {
//                binding.progressLayout.visibility = View.GONE
//                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
//            } else {
//                binding.progressLayout.visibility = View.VISIBLE
//                requireActivity().window.setFlags(
//                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
//                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
//                );
//            }
//        })




    }


    override fun onChanged(it: Any?) {
        if (it == null) return
        when (it) {
//            Codes.ADD_STORIES -> {
//                findNavController().navigate(R.id.add_stories_to_add_story)
//            }
            else -> {
                if (it is String) {
                    showToast(it.toString(), 1)
                }
                viewModel.setShowProgress(false)
            }
        }
    }
}