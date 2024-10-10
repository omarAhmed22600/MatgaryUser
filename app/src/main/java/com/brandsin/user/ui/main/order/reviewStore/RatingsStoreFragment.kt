package com.brandsin.user.ui.main.order.reviewStore

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.brandsin.user.R
import com.brandsin.user.databinding.FragmentRatingsStoreBinding
import com.brandsin.user.network.ResponseHandler
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.ui.main.order.reviewStore.adapter.ImagesRatingsStoreAdapter
import com.brandsin.user.ui.main.order.reviewStore.adapter.RatingsStoreAdapter
import com.brandsin.user.ui.main.order.reviewStore.model.RatingItem
import com.brandsin.user.ui.main.order.reviewStore.viewmodel.RatingsStoreViewModel

class RatingsStoreFragment : BaseHomeFragment() {

    private var _binding: FragmentRatingsStoreBinding? = null
    private val binding get() = _binding!!

    private val viewModel: RatingsStoreViewModel by viewModels()

    private lateinit var ratingsStoreAdapter: RatingsStoreAdapter
    private lateinit var imagesRatingsStoreAdapter: ImagesRatingsStoreAdapter

    private var storeId: Int? = null

    private var hasMedia: Int = 0

    private val onItemClickCallBack: (ratingItem: RatingItem) -> Unit =
        { ratingsStoreItem ->
            if (ratingsStoreItem.video?.isNotEmpty() == true) {
                val videoUrl = ratingsStoreItem.video.toString()
                Log.d("btnWatchingVideo", "dataToPass $videoUrl")

                val bundle = Bundle()
                bundle.putString("videoUrl", videoUrl)
                findNavController().navigate(R.id.videoPreviewFragment, bundle)

            } else if (ratingsStoreItem.image?.isNotEmpty() == true) {
                val imagesAdsUrlList: ArrayList<String> = ArrayList()
                imagesAdsUrlList.add(ratingsStoreItem.image)
                val bundle = Bundle()
                bundle.putStringArrayList("COVERS_URL_LIST", imagesAdsUrlList)
                findNavController().navigate(R.id.imagesPreviewFragment, bundle)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRatingsStoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBarName(getString(R.string.ratings))

        storeId = requireArguments().getInt("STORE_ID")

        // call api to get all normal ratings store is first
        viewModel.getRatingsStoreList(storeId, hasMedia)

        setBtnListener()
        initRecyclers()
        subscribeData()
    }

    @SuppressLint("ResourceAsColor")
    private fun setBtnListener() {
        binding.rating.setOnClickListener {
            binding.rvRatings.visibility = View.VISIBLE
            binding.rvImagesRatings.visibility = View.GONE

            hasMedia = 0

            binding.rating.setBackgroundResource(R.drawable.bg_selected_text_search)
            binding.rating.setTextColor(R.color.color_primary)
            binding.imagesRating.setTextColor(R.color.black)
            binding.imagesRating.setBackgroundResource(R.drawable.bg_unselected_text_search)

            // call api to get all normal ratings store
            viewModel.getRatingsStoreList(storeId,hasMedia)
        }

        binding.imagesRating.setOnClickListener {
            binding.rvImagesRatings.visibility = View.VISIBLE
            binding.rvRatings.visibility = View.GONE

            hasMedia = 1

            binding.imagesRating.setBackgroundResource(R.drawable.bg_selected_text_search)
            binding.imagesRating.setTextColor(R.color.color_primary)
            binding.rating.setTextColor(R.color.black)
            binding.rating.setBackgroundResource(R.drawable.bg_unselected_text_search)

            // call api to get all images ratings store
            viewModel.getRatingsStoreList(storeId, hasMedia)
        }
    }

    private fun initRecyclers() {
        binding.rvRatings.apply {
            ratingsStoreAdapter = RatingsStoreAdapter()
            adapter = ratingsStoreAdapter
        }

        binding.rvImagesRatings.apply {
            imagesRatingsStoreAdapter = ImagesRatingsStoreAdapter(onItemClickCallBack)
            adapter = imagesRatingsStoreAdapter
        }
    }

    private fun subscribeData() {
        viewModel.getRatingsStoreResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseHandler.Success -> {
                    // bind data to the view
                    // binding.shimmerFavoriteProductLayout.visibility = View.GONE
                    // binding.rvFavoriteProduct.visibility = View.VISIBLE
                    ratingsStoreAdapter.submitData(it.data?.ratingItem)
                    imagesRatingsStoreAdapter.submitData(it.data?.ratingItem)
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