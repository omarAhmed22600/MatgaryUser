package com.brandsin.user.ui.main.homenew.more

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.R
import com.brandsin.user.databinding.ItemHomeMoreBinding
import com.brandsin.user.model.menu.offers.OffersItemDetails
import com.brandsin.user.model.order.homenew.BrandItem
import com.brandsin.user.model.order.homenew.CategoriesItem
import com.brandsin.user.model.order.homenew.GiftItem
import com.brandsin.user.model.order.homenew.SectionsItem
import com.brandsin.user.model.order.homenew.SlidesItem
import com.brandsin.user.model.order.homenew.StoresDataItem
import com.brandsin.user.model.order.storedetails.StoreProductItem
import com.brandsin.user.ui.main.homenew.category.CategoriesAdapter
import com.brandsin.user.ui.main.homenew.moresub.GiftsAdapter
import com.brandsin.user.ui.main.homenew.moresub.OfferAdapter
import com.brandsin.user.ui.main.homenew.moresub.ProductAdapter
import com.brandsin.user.ui.main.homenew.moresub.brand.BrandsAdapter
import com.brandsin.user.utils.SingleLiveEvent
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView

class MoreAdapter : RecyclerView.Adapter<MoreAdapter.MoreHolder>() {

    private var moreList = mutableListOf<SectionsItem>()
    var moreLiveData = SingleLiveEvent<SectionsItem>()
    var moreSubLiveData = SingleLiveEvent<StoresDataItem>()
    var moreProductLiveData = SingleLiveEvent<StoreProductItem>()
    var moreGiftLiveData = SingleLiveEvent<GiftItem>()
    var moreOfferLiveData = SingleLiveEvent<OffersItemDetails>()
    var categoriesClickedLiveData = SingleLiveEvent<CategoriesItem>()
    var moreSliderLiveData = SingleLiveEvent<SlidesItem>()
    var moreBrandsLiveData = SingleLiveEvent<Int>()
    var brandItemLiveData = SingleLiveEvent<BrandItem>()
    var selectedPosition = 0

    private lateinit var brandsAdapter: BrandsAdapter
    private lateinit var giftsAdapter: GiftsAdapter
    private lateinit var productAdapter: ProductAdapter
    private lateinit var offerAdapter: OfferAdapter
    private lateinit var categoriesAdapter: CategoriesAdapter

    private val btnProductItemClickCallBack: (productItem: StoreProductItem) -> Unit =
        { productItem ->
            moreProductLiveData.value = productItem
        }

    private val btnOfferItemClickCallBack: (offerItem: OffersItemDetails) -> Unit =
        { offerItem ->
            moreOfferLiveData.value = offerItem
        }

    private val btnGiftItemClickCallBack: (giftItem: GiftItem) -> Unit =
        { giftItem ->
            moreGiftLiveData.value = giftItem
        }

    private val btnBrandItemClickCallBack: (brand: BrandItem) -> Unit =
        { brand ->
            brandItemLiveData.value = brand
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoreHolder {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val binding: ItemHomeMoreBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.item_home_more, parent, false)
        return MoreHolder(binding)
    }

    override fun onBindViewHolder(holder: MoreHolder, position: Int) {
        val itemViewModel = ItemMoreViewModel(moreList[position])
        holder.binding.viewModel = itemViewModel

        initGiftsRecycler(holder.binding)
        initProductsRecycler(holder.binding)
        initOfferRecycler(holder)
        initCategoriesRecycler(holder)
        initBrandsRecycler(holder)

        when (itemViewModel.itemMore.type) {
            "products" -> {
                holder.binding.rvProducts.visibility = View.VISIBLE
                holder.binding.rvCategories.visibility = View.GONE
                holder.binding.rvOffers.visibility = View.GONE
                holder.binding.rvBrand.visibility = View.GONE
                holder.binding.rvGifts.visibility = View.GONE
                holder.binding.rvMoreSub.visibility = View.GONE
                holder.binding.sliderItem.visibility = View.GONE

                // submit data in products adapter
                productAdapter.submitData(itemViewModel.itemMore.productItem)
            }

            "offers" -> {
                holder.binding.rvOffers.visibility = View.VISIBLE
                holder.binding.rvProducts.visibility = View.GONE
                holder.binding.rvBrand.visibility = View.GONE
                holder.binding.rvCategories.visibility = View.GONE
                holder.binding.rvGifts.visibility = View.GONE
                holder.binding.rvMoreSub.visibility = View.GONE
                holder.binding.sliderItem.visibility = View.GONE

                // submit data in offers adapter
                offerAdapter.submitData(itemViewModel.itemMore.offersItem)
            }

            "categories" -> {
                holder.binding.rvCategories.visibility = View.VISIBLE
                holder.binding.rvProducts.visibility = View.GONE
                holder.binding.rvOffers.visibility = View.GONE
                holder.binding.rvBrand.visibility = View.GONE
                holder.binding.rvGifts.visibility = View.GONE
                holder.binding.rvMoreSub.visibility = View.GONE
                holder.binding.sliderItem.visibility = View.GONE

                // submit data in offers adapter
                categoriesAdapter.updateList(itemViewModel.itemMore.categoriesModels as MutableList<CategoriesItem>)
            }

            "brands" -> {
                holder.binding.constraintBrands.visibility = View.VISIBLE
                holder.binding.rvBrand.visibility = View.VISIBLE
                holder.binding.rvOffers.visibility = View.VISIBLE
                holder.binding.rvCategories.visibility = View.GONE
                holder.binding.rvProducts.visibility = View.GONE
                holder.binding.rvGifts.visibility = View.GONE
                holder.binding.rvMoreSub.visibility = View.GONE
                holder.binding.sliderItem.visibility = View.GONE

                // Create a sublist containing the first 8 items
                val sublist = itemViewModel.itemMore.brandItem?.subList(
                    0,
                    minOf(itemViewModel.itemMore.brandItem!!.size, 8)
                )

                if (itemViewModel.itemMore.brandItem!!.size > 8) {
                    holder.binding.btnMoreBrands.visibility = View.VISIBLE

                    holder.binding.btnMoreBrands.setOnClickListener {
                        moreBrandsLiveData.value = itemViewModel.itemMore.id ?: 0
                    }

                } else {
                    holder.binding.btnMoreBrands.visibility = View.GONE
                }

                // submit data in brands adapter
                brandsAdapter.submitData(sublist)
            }

            "gifts" -> {
                holder.binding.rvGifts.visibility = View.VISIBLE
                holder.binding.rvProducts.visibility = View.GONE
                holder.binding.rvOffers.visibility = View.GONE
                holder.binding.rvBrand.visibility = View.GONE
                holder.binding.rvCategories.visibility = View.GONE
                holder.binding.rvMoreSub.visibility = View.GONE
                holder.binding.sliderItem.visibility = View.GONE

                // submit data in brands adapter
                giftsAdapter.submitData(itemViewModel.itemMore.giftItem)
            }

            "slider" -> { // && itemViewModel.itemMore.slider != null
                itemViewModel.moreSliderList =
                    itemViewModel.itemMore.slider?.slides as MutableList<SlidesItem>
                itemViewModel.moreSliderAdapter.updateList(itemViewModel.moreSliderList)

                holder.binding.bannerSlider.setSliderAdapter(itemViewModel.moreSliderAdapter)
                holder.binding.bannerSlider.setIndicatorAnimation(IndicatorAnimationType.WORM)
                holder.binding.bannerSlider.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
                holder.binding.bannerSlider.autoCycleDirection =
                    SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH
                holder.binding.bannerSlider.indicatorSelectedColor = Color.WHITE
                holder.binding.bannerSlider.indicatorUnselectedColor = Color.GRAY
                holder.binding.bannerSlider.scrollTimeInSec = 3
                holder.binding.bannerSlider.isAutoCycle = true
                holder.binding.bannerSlider.startAutoCycle()

                holder.binding.rvMoreSub.visibility = View.GONE
                holder.binding.rvProducts.visibility = View.GONE
                holder.binding.sliderItem.visibility = View.VISIBLE
            }
        }

        if (!itemViewModel.itemMore.storesData.isNullOrEmpty()) {
            itemViewModel.moreSubList =
                itemViewModel.itemMore.storesData as MutableList<StoresDataItem>
            itemViewModel.moreSubAdapter.updateList(itemViewModel.moreSubList)

            holder.binding.rvMoreSub.visibility = View.VISIBLE
            holder.binding.sliderItem.visibility = View.GONE
            holder.binding.rvProducts.visibility = View.GONE
        }

        itemViewModel.moreSubAdapter.moreSubLiveData.observeForever {
            when (it) {
                is StoresDataItem -> {
                    moreSubLiveData.value = it
                }
            }
        }

        itemViewModel.moreSliderAdapter.moreSliderLiveData.observeForever {
            when (it) {
                is SlidesItem -> {
                    moreSliderLiveData.value = it
                }
            }
        }

        categoriesAdapter.categoriesLiveData.observeForever {
            when (it) {
                is CategoriesItem -> {
                    categoriesClickedLiveData.value = it
                }
            }
        }
    }

    private fun initProductsRecycler(holder: ItemHomeMoreBinding) {
        holder.rvProducts.apply {
            productAdapter = ProductAdapter(btnProductItemClickCallBack)
            adapter = productAdapter
        }
    }

    private fun initCategoriesRecycler(holder: MoreHolder) {
        holder.binding.rvCategories.apply {
            categoriesAdapter = CategoriesAdapter()
            adapter = categoriesAdapter
        }
    }

    private fun initOfferRecycler(holder: MoreHolder) {
        holder.binding.rvOffers.apply {
            offerAdapter = OfferAdapter(btnOfferItemClickCallBack)
            adapter = offerAdapter
        }
    }

    private fun initGiftsRecycler(holder: ItemHomeMoreBinding) {
        holder.rvGifts.apply {
            giftsAdapter = GiftsAdapter(btnGiftItemClickCallBack)
            adapter = giftsAdapter
        }
    }

    private fun initBrandsRecycler(holder: MoreHolder) {
        holder.binding.rvBrand.apply {
            brandsAdapter = BrandsAdapter(btnBrandItemClickCallBack)
            adapter = brandsAdapter
        }
    }

    override fun getItemCount(): Int {
        return moreList.size
    }

    fun updateList(models: MutableList<SectionsItem>) {
        moreList = models
        notifyDataSetChanged()
    }

    inner class MoreHolder(val binding: ItemHomeMoreBinding) : RecyclerView.ViewHolder(binding.root)
}
