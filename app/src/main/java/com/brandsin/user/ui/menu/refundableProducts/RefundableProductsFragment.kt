package com.brandsin.user.ui.menu.refundableProducts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.brandsin.user.R
import com.brandsin.user.databinding.FragmentRefundableProductsBinding
import com.brandsin.user.model.refundableProduct.RefundableProductItem
import com.brandsin.user.network.ResponseHandler
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.ui.activity.home.HomeActivity
import com.brandsin.user.ui.menu.refundableProducts.adapter.RefundableProductAdapter
import com.brandsin.user.ui.menu.refundableProducts.viewmodel.RefundableProductViewModel

class RefundableProductsFragment : BaseHomeFragment() {

    private var _binding: FragmentRefundableProductsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RefundableProductViewModel by viewModels()

    private lateinit var refundableProductAdapter: RefundableProductAdapter

    private val onItemClickCallBack: (refundableProductItem: RefundableProductItem) -> Unit =
        { refundableProductItem ->
            // Navigate to add refundable
            val bundle = Bundle()
            bundle.putParcelable("REFUNDABLE_PRODUCT_ITEM", refundableProductItem)
            findNavController().navigate(R.id.addRefundableProductFragment, bundle)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRefundableProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBarName(getString(R.string.refundable))

        viewModel.getAllRefundableProducts()

        initRecycler()
        subscribeData()
    }

    private fun initRecycler() {
        binding.rvRefundableProducts.apply {
            refundableProductAdapter =
                RefundableProductAdapter(onItemClickCallBack)
            adapter = refundableProductAdapter
        }
    }

    private fun subscribeData() {
        viewModel.getRefundableProductResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseHandler.Success -> {
                    // bind data to the view
                    binding.shimmer.visibility = View.GONE
                    binding.rvRefundableProducts.visibility = View.VISIBLE
                    refundableProductAdapter.submitData(it.data?.refundableProductItem)
                }

                is ResponseHandler.Error -> {
                    // show error message
                    (requireActivity() as HomeActivity).hideLoading()
                }

                is ResponseHandler.Loading -> {
                    // show a progress bar
                    (requireActivity() as HomeActivity).showLoading()
                }

                is ResponseHandler.StopLoading -> {
                    // show a progress bar
                    (requireActivity() as HomeActivity).hideLoading()
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