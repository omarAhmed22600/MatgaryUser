package com.brandsin.user.ui.main.order.storedetails.addons.ratingsProduct

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.brandsin.user.R
import com.brandsin.user.databinding.FragmentRatingsProductBinding
import com.brandsin.user.model.order.storedetails.StoreProductItem
import com.brandsin.user.network.ResponseHandler
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.ui.main.order.reviewStore.model.RatingItem
import com.brandsin.user.ui.main.order.storedetails.addons.addons.VideoPreviewFragment
import com.brandsin.user.ui.main.order.storedetails.addons.ratingsProduct.viewmodel.RatingsProductViewModel
import com.brandsin.user.ui.main.order.storedetails.addons.skus.activity.adapter.RatingsProductAdapter
import com.brandsin.user.ui.main.search.ImagesPreviewFragment
import com.brandsin.user.utils.Utils
import com.bumptech.glide.Glide

class RatingsProductFragment : BaseHomeFragment() {

    private var _binding: FragmentRatingsProductBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RatingsProductViewModel by viewModels()

    private lateinit var ratingsProductAdapter: RatingsProductAdapter

    private var productItem: StoreProductItem? = null

    private val onItemClickCallBack: (ratingsItem: RatingItem) -> Unit =
        { ratingsItem ->
            if (ratingsItem.video?.isNotEmpty() == true) {
                val videoUrl = ratingsItem.video.toString()

                val bundle = Bundle()
                bundle.putString("videoUrl", videoUrl)
                val fragment = VideoPreviewFragment()
                fragment.arguments = bundle

                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()

            } else if (ratingsItem.image?.isNotEmpty() == true) {
                val imagesAdsUrlList: ArrayList<String> = ArrayList()
                imagesAdsUrlList.add(ratingsItem.image)
                val bundle = Bundle()
                bundle.putStringArrayList("COVERS_URL_LIST", imagesAdsUrlList)

                val fragment = ImagesPreviewFragment.newInstance(bundle)

                // Pass a tag to addToBackStack
                // val transactionTag = "YourTransactionTag"

                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null) // Optional: Add the transaction to the back stack
                    .commit()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRatingsProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // setBarName(getString(R.string.ratings))

        productItem= requireArguments().getParcelable("PRODUCT_ITEM")

        // call api to get all normal ratings store is first
        viewModel.getRatingsProductList(productItem?.id)

        initView()
        setBtnListener()
        subscribeData()
    }

    private fun setBtnListener() {
        binding.icBack.setOnClickListener {
            requireActivity().finish()
        }
    }

    private fun initView() {
        Glide.with(requireContext())
            .load(productItem?.image)
            .error(R.drawable.app_logo)
            .into(binding.imgProduct)

        binding.title.text = productItem?.name.toString()
        binding.content.text = Utils.html2text(productItem?.description.toString())
        binding.avgRatings.text = productItem?.avgRating.toString()
        binding.ratingBar.rating = productItem?.avgRating.toString().toFloat()

        binding.rvRatingsProduct.apply {
            ratingsProductAdapter = RatingsProductAdapter(onItemClickCallBack)
            adapter = ratingsProductAdapter
        }
    }

    @SuppressLint("SetTextI18n")
    private fun subscribeData() {
        viewModel.getRatingsProductResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseHandler.Success -> {
                    // bind data to the view
                    // binding.shimmerFavoriteProductLayout.visibility = View.GONE
                    // binding.rvFavoriteProduct.visibility = View.VISIBLE
                    binding.numberOfRatings.text =
                        it.data?.ratingItem?.size.toString() + " " + getString(R.string.review)

                    ratingsProductAdapter.submitData(it.data?.ratingItem)
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