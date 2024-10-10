package com.brandsin.user.ui.main.homenew.moresub.brand

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.brandsin.user.R
import com.brandsin.user.databinding.FragmentBrandsBinding
import com.brandsin.user.model.order.homenew.BrandItem
import com.brandsin.user.model.order.homenew.HomeNewResponse
import com.brandsin.user.model.order.homepage.HomePageResponse
import com.brandsin.user.model.order.storedetails.StoreDetailsData
import com.brandsin.user.network.ResponseHandler
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.ui.main.homenew.HomeNewFragmentDirections
import com.brandsin.user.ui.main.homenew.moresub.brand.viewmodel.BrandViewModel

class BrandsFragment : BaseHomeFragment() {

    private var _binding: FragmentBrandsBinding? = null
    private val binding get() = _binding!!

    private val viewModel : BrandViewModel by viewModels()

    private lateinit var brandsAdapter: BrandsAdapter

    private val btnBrandItemClickCallBack: (brand: BrandItem) -> Unit =
        { brand ->
            val action = BrandsFragmentDirections.actionBrandsFragmentToNavSearch(
                "brand",
                brand.id.toString(),
                HomePageResponse(),
                HomeNewResponse(),
                StoreDetailsData()
            )
            findNavController().navigate(action)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentBrandsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBarName(getString(R.string.brands))

        viewModel.getItemsOfSectionId(requireArguments().getInt("SECTION_ID")) // section Id

        // Initialize recycler view
        initRecycler()

        subscribeData()
    }

    private fun initRecycler() {
        binding.rvAllBrands.apply {
            brandsAdapter = BrandsAdapter(btnBrandItemClickCallBack)
            adapter = brandsAdapter
        }
    }

    private fun subscribeData(){
        viewModel.getBrandsItemsResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseHandler.Success -> {
                    brandsAdapter.submitData(it.data?.data)
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

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}