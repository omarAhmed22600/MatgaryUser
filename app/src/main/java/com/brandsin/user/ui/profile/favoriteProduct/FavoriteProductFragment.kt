package com.brandsin.user.ui.profile.favoriteProduct

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.brandsin.user.R
import com.brandsin.user.databinding.FragmentFavoriteProductBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Params
import com.brandsin.user.model.order.storedetails.StoreProductItem
import com.brandsin.user.network.ResponseHandler
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.ui.main.order.storedetails.addons.skus.activity.OrderAddonsActivity
import com.brandsin.user.ui.main.order.storedetails.addons.skus.dialog.DialogOrderAddonsFragment
import com.brandsin.user.ui.profile.favoriteProduct.viewmodel.FavoriteProductViewModel
import com.brandsin.user.utils.Utils

class FavoriteProductFragment : BaseHomeFragment() {

    private var _binding: FragmentFavoriteProductBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FavoriteProductViewModel by viewModels()

    private lateinit var favoriteProductAdapter: FavoriteProductAdapter

    private val onItemClickCallBack: (favoriteProductItem: StoreProductItem) -> Unit =
        { favoriteProductItem ->
            when (favoriteProductItem.type) {
                "simple" -> {
                    val bundle = Bundle()
                    bundle.putParcelable(Params.STORE_PRODUCT_ITEM, favoriteProductItem)
                    Utils.startDialogActivity(
                        requireActivity(),
                        DialogOrderAddonsFragment::class.java.name,
                        Codes.SELECT_ORDER_ADDONS_DIALOG,
                        bundle
                    )
                }

                "variable" -> {
                    val intent = Intent(requireActivity(), OrderAddonsActivity::class.java)
                    intent.putExtra(Params.STORE_PRODUCT_ITEM, favoriteProductItem)
                    intent.putExtra(Params.DIALOG_CLICK_ACTION, 0)
                    startActivityForResult(intent, Codes.SELECT_ORDER_ADDONS_ACTIVITY)
                }
            }
        }

    private val onRemoveFavoriteClickCallBack: (favoriteProductItem: StoreProductItem) -> Unit =
        { favoriteProductItem ->
            // call api addAndRemoveFavorite
            viewModel.addAndRemoveFavorite(favoriteProductItem.id)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBarName(getString(R.string.favorite_product))

        viewModel.getAllFavoriteProduct()

        initRecycler()
        subscribeData()
    }

    private fun initRecycler() {
        binding.rvFavoriteProduct.apply {
            favoriteProductAdapter =
                FavoriteProductAdapter(onItemClickCallBack, onRemoveFavoriteClickCallBack)
            adapter = favoriteProductAdapter
        }
    }

    private fun subscribeData() {
        viewModel.getFavoriteProductResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseHandler.Success -> {
                    // bind data to the view
                    binding.shimmerFavoriteProductLayout.visibility = View.GONE
                    if (it.data?.data.isNullOrEmpty()) {
                        binding.constraintEmptyImage.visibility = View.VISIBLE
                        binding.rvFavoriteProduct.visibility = View.GONE
                    } else {
                        binding.rvFavoriteProduct.visibility = View.VISIBLE
                        binding.constraintEmptyImage.visibility = View.GONE
                        favoriteProductAdapter.submitData(it.data?.data)
                    }

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

        viewModel.addAndRemoveFavoriteResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseHandler.Success -> {
                    showToast(it.data?.message.toString(), 2)
                    viewModel.getAllFavoriteProduct()
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