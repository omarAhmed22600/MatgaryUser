package com.brandsin.user.ui.main.order.storedetails

import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.brandsin.user.R
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.follow.FollowResponse
import com.brandsin.user.model.order.cart.CartItem
import com.brandsin.user.model.order.cart.CartStoreData
import com.brandsin.user.model.order.cart.UserCart
import com.brandsin.user.model.order.homepage.StoriesItem
import com.brandsin.user.model.order.storedetails.CoversItem
import com.brandsin.user.model.order.storedetails.StoreCategoryItem
import com.brandsin.user.model.order.storedetails.StoreDetailsData
import com.brandsin.user.model.order.storedetails.StoreDetailsResponse
import com.brandsin.user.model.order.storedetails.StoreProductItem
import com.brandsin.user.network.ApiResponse
import com.brandsin.user.network.requestCall
import com.brandsin.user.ui.main.home.story.StoriesAdapter
import com.brandsin.user.ui.main.order.storedetails.banners.BannersAdapter
import com.brandsin.user.ui.main.order.storedetails.categories.StoreCatAdapter
import com.brandsin.user.ui.main.order.storedetails.products.StoreProductsAdapter_V2
import com.brandsin.user.utils.MyApp.Companion.context
import com.brandsin.user.utils.PrefMethods
import com.brandsin.user.utils.SingleLiveEvent
import com.brandsin.user.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StoreDetailsViewModel : BaseViewModel() {

    var catAdapter = StoreCatAdapter()
    var productsAdapter = StoreProductsAdapter_V2()// StoreProductsAdapter()
    val isGrid = MutableLiveData(false)
    var productsList: ArrayList<StoreProductItem> = ArrayList()
    var storeCategoriesList: List<StoreCategoryItem> = ArrayList()

    private var storeData2 = SingleLiveEvent<StoreDetailsData>()

    var storeData = StoreDetailsData()
    var obsMinPrice = ObservableField<String>()

    var obsFollowers = ObservableField<String>()
    var obsDeliveryPrice = ObservableField<String>()
    var obsDeliveryTime = ObservableField<String>()
    var obsRating = ObservableField<Float>()

    var isFollowed = ObservableField<Boolean>()

    lateinit var bannersAdapter: BannersAdapter
    lateinit var storiesAdapter: StoriesAdapter

    var userCart: UserCart? = null
    var cartStore: CartStoreData? = null
    var cartItemsList: ArrayList<CartItem>? = null
    var storiesList = ArrayList<StoriesItem>()
    var followResponse = SingleLiveEvent<FollowResponse>()

    fun setBannerListener(onBannerClickedListener: StoreDetailsFragment) {
        bannersAdapter = BannersAdapter(onBannerClickedListener)
    }

    fun setStoriesListener(onStoryClickedListener: StoreDetailsFragment) {
        storiesAdapter = StoriesAdapter(onStoryClickedListener)
        storiesAdapter.setStoryViewType(3)
    }

    private fun getMinPrice() = when (storeData.minPriceProduct) {
        null -> {
            obsMinPrice.set(""" 0 ${getString(R.string.currency)}""")
        }

        else -> {
            obsMinPrice.set(""" ${storeData.minOrderPrice} ${getString(R.string.currency)}""")
        }
    }

    fun onChangeViewClicked(){
        isGrid.value = isGrid.value!!.not()
    }
    private fun getDeliveryTime() = when (storeData.deliveryTime) {
        null -> {
            obsDeliveryTime.set(""" 0 ${getString(R.string.minute)}""")
        }

        else -> {
            // obsDeliveryTime.set(""" ${storeData.deliveryTime} ${getString(R.string.minute)}""")
            obsDeliveryTime.set(
                "${storeData.deliveryTime}" + " ${
                    Utils.translateDeliveryType(
                        context,
                        storeData.deliveryType.toString()
                    )
                }"
            )
        }
    }

    private fun getDeliveryPrice() = when (storeData.deliveryPrice) {
        null -> {
            obsDeliveryPrice.set("""0 ${getString(R.string.currency)}""")
        }

        else -> {
            obsDeliveryPrice.set("""${storeData.deliveryPrice} ${getString(R.string.currency)}""")
        }
    }

    private fun getRating() = when (storeData.avgRating) {
        null -> {
            obsRating.set(0f)
        }

        else -> {
            obsRating.set(storeData.avgRating!!.toFloat())
        }
    }

    fun getStoreDetails(storeId: Int) {
        obsIsFull.set(false)
        obsIsLoading.set(true)
        requestCall<StoreDetailsResponse?>({
            withContext(Dispatchers.IO) {
                return@withContext getApiRepo().getStoreDetails(
                    storeId,
                    PrefMethods.getUserData()?.id,
                    PrefMethods.getLanguage(),
                    0,
                    50
                )
            }
        })
        { res ->
            when (res!!.isSuccess) {
                true -> {
                    obsIsLoading.set(false)
                    obsIsFull.set(true)

                    storeData = res.storeDetailsData!!
                    storeData2.value = res.storeDetailsData
                    getDeliveryPrice()
                    getMinPrice()
                    getDeliveryTime()
                    getRating()
                    notifyChange()
                    isFollowed.set(res.storeDetailsData.isFollowed)
                    catAdapter.updateList(res.storeDetailsData.storeCategoriesList as MutableList<StoreCategoryItem>)
                    bannersAdapter.updateList(res.storeDetailsData.covers as MutableList<CoversItem>)

                    obsFollowers.set(res.storeDetailsData.followersCount.toString())

                    apiResponseLiveData.value = ApiResponse.success(res)

                    when {
                        res.storeDetailsData.storeProductList!!.isNotEmpty() -> {
                            storeCategoriesList =
                                res.storeDetailsData.storeCategoriesList as ArrayList<StoreCategoryItem>
                            productsList =
                                res.storeDetailsData.storeProductList as ArrayList<StoreProductItem>

                            /*if (PrefMethods.getUserCart()!=null) {
                                for (productsList in productsList) {
                                    for (cartList in PrefMethods.getUserCart()!!.cartItems!!) {
                                        if (cartList.productId == productsList.id) {
                                            productsList.isSelected = true
                                        }
                                    }
                                }
                            }*/

                            // productsAdapter.updateList(productsList)
                            getFilteredProducts(storeCategoriesList[0].id!!)
                            obsHideRecycler.set(true)
                            obsIsEmpty.set(false)
                        }

                        else -> {
                            obsHideRecycler.set(false)
                            obsIsEmpty.set(true)
                        }
                    }

                }

                else -> {}
            }
        }
    }

    fun getFilteredProducts(catId: Int) {
        val filteredList: ArrayList<StoreProductItem> = ArrayList()
        productsList.forEach { productsItem ->
            when {
                productsItem.productCategoriesList!!.isEmpty() -> {
                    filteredList.add(productsItem)
                }

                else -> {
                    productsItem.productCategoriesList.forEach { categoriesItem ->
                        when (categoriesItem!!.categoryId) {
                            catId -> {
                                filteredList.add(productsItem)
                            }
                        }
                    }
                }
            }
        }

        when (filteredList.size) {
            0 -> {
                obsHideRecycler.set(false)
                obsIsEmpty.set(true)
            }

            else -> {
                obsHideRecycler.set(true)
                obsIsEmpty.set(false)
                productsAdapter.updateList(filteredList.filter { it.status != "inactive" } as ArrayList)
            }
        }
    }

    fun onCartClicked() {
        setValue(Codes.CART_CLICKED)
    }

    fun followStore() {
        isFollowed.set(!(isFollowed.get() as Boolean))
        storeData.isFollowed = isFollowed.get()!!
        requestCall<FollowResponse?>({
            withContext(Dispatchers.IO) {
                return@withContext getApiRepo().followStore(
                    storeData.id,
                    PrefMethods.getUserData()!!.id!!,
                    PrefMethods.getLanguage()
                )
            }
        })
        { res ->
            when (res!!.success) {
                true -> {
                    followResponse.value = res
                }

                else -> {}
            }
        }
    }

    fun addProductToCart(item: CartItem) {
        obsIsVisible.set(true)

        when {
            // Cart is empty
            cartItemsList!!.isEmpty() -> {
                cartItemsList!!.add(item)
                cartStore?.run {
                    storeId = storeData.id
                    storeName = storeData.name
                    extraFees = storeData.deliveryPrice?.toDouble() ?: 0.0
                    deliveryTime = storeData.deliveryTime?.toString() ?: ""
                    deliveryType = storeData.deliveryType ?: ""
                    deliveryPrice = storeData.deliveryPrice ?: 0
                    minimumOrder = storeData.minOrderPrice?.toDouble() ?: 0.0
                }

                userCart?.run {
                    cartItems = cartItemsList
                    cartStoreData = cartStore
                }
                PrefMethods.saveUserCart(userCart)
            }

            else -> {
                var i = 0
                while (i < cartItemsList!!.size) {
                    when {
                        // This item is exist in cart >> update quantity
                        cartItemsList!![i].skuCode == item.skuCode && cartItemsList!![i].addonsIds == item.addonsIds -> {
                            cartItemsList!![i].productQuantity =
                                cartItemsList!![i].productQuantity + item.productQuantity // update quantity
                            cartItemsList!![i].productTotalPrice =
                                cartItemsList!![i].productUnitPrice * cartItemsList!![i].productQuantity // update price
                            val sharedCart = PrefMethods.getUserCart()
                            sharedCart?.cartItems?.clear()
                            sharedCart?.cartItems?.addAll(cartItemsList!!)
                            PrefMethods.saveUserCart(sharedCart)
                            return
                        }

                        i == cartItemsList!!.size - 1 -> {
                            // This item not exist in cart
                            cartItemsList!!.add(item)
                            cartStore?.run {
                                storeId = storeData.id
                                storeName = storeData.name
                                extraFees = storeData.deliveryPrice?.toDouble() ?: 0.0
                                deliveryTime = storeData.deliveryTime?.toString() ?: ""
                                deliveryType = storeData.deliveryType ?: ""
                                minimumOrder = storeData.minOrderPrice?.toDouble() ?: 0.0
                            }

                            userCart?.run {
                                cartItems = cartItemsList
                                cartStoreData = cartStore
                            }
                            PrefMethods.saveUserCart(userCart)
                            return
                        }

                        else -> i++
                    }
                }
            }
        }
    }
}