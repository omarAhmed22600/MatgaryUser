package com.brandsin.user.ui.main.search

import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.order.homepage.TagsItem
import com.brandsin.user.model.order.storedetails.StoreProductItem
import com.brandsin.user.model.search.SearchDataResponse
import com.brandsin.user.model.search.SearchStoresOrProducts
import com.brandsin.user.network.requestCall
import com.brandsin.user.ui.main.order.storedetails.products.StoreProductsAdapter
import com.brandsin.user.ui.main.search.stores.StoresAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchViewModel : BaseViewModel() {

    var storesAdapter = StoresAdapter()
    var productsAdapter = StoreProductsAdapter()

    private var storesList = mutableListOf<SearchStoresOrProducts>()
    var storesProductList = mutableListOf<StoreProductItem>()
    var tagsList = mutableListOf<TagsItem>()

    var latitude = ""
    var longitude = ""
    var isFromStoreDetails = false
    var categoryId = ""
    var keywordSearch = ""
    var sort = ""
    var tags = ArrayList<Int>()

    fun setIsFromStoreDetails() {
        isFromStoreDetails = true
        obsIsLoading.set(false)
        obsIsFull.set(true)
        obsIsEmpty.set(false)
        obsIsLoadingStores.set(false)
        obsHideRecycler.set(true)
        productsAdapter.updateList(storesProductList as ArrayList<StoreProductItem>)
    }

    /*fun searchProduct(keyword: String) {
        val newStoresProductList = mutableListOf<StoreProductItem>()
        for (product in storesProductList) {
            if (product.name!!.contains(keyword))
                newStoresProductList.add(product)
        }

        setShowProgress(false)
        if (newStoresProductList.isNotEmpty()) {
            obsIsLoading.set(false)
            obsIsFull.set(true)
            obsIsEmpty.set(false)
            obsIsLoadingStores.set(false)
            obsHideRecycler.set(true)

        } else {
            obsIsLoading.set(false)
            obsIsEmpty.set(true)
            obsIsFull.set(false)
        }
        productsAdapter.updateList(newStoresProductList as ArrayList<StoreProductItem>)
    }

    fun getSearch(keyword: String) {
//        if (sort.isEmpty()) {
        latitude = PrefMethods.getUserLocation()!!.lat.toString()
        longitude = PrefMethods.getUserLocation()!!.lng.toString()
//        }else{
//             latitude = ""
//             longitude = ""
//        }
        keywordSearch = keyword

        obsIsEmpty.set(false)

        requestCall<HomePageResponse?>({
            withContext(Dispatchers.IO) {
                return@withContext getApiRepo().getSearch( // call api search
                    latitude,
                    longitude,
                    "5",
                    PrefMethods.getLanguage(),
                    categoryId,
                    keywordSearch,
                    sort,
                    tags
                )
            }
        })
        { res ->
            setShowProgress(false)
            when (res!!.success) {
                true -> {
                    if (res.data!!.shops!!.isNotEmpty()) {

                        obsIsLoading.set(false)
                        obsIsFull.set(true)
                        obsIsLoadingStores.set(false)
                        obsHideRecycler.set(true)

                        *//* Stores List *//*
                        storesList = res.data.shops!! as MutableList<ShopsItem>
                        storesAdapter.updateList(storesList)
                    } else {
                        obsIsLoading.set(false)
                        obsIsEmpty.set(true)
                        obsIsFull.set(false)
                    }
                }

                else -> {
                    obsIsLoading.set(false)
                    obsIsEmpty.set(true)
                    obsIsFull.set(false)
                }
            }
        }
    }*/


    fun searchProductOrStore(searchFor: String, keyword: String, brandId: Int?) {
        obsIsEmpty.set(false)
        setShowProgress(true)

        requestCall<SearchDataResponse?>({
            withContext(Dispatchers.IO) {
                return@withContext getApiRepo().searchProductOrStore(
                    // call api search
                    searchFor,
                    keyword,
                    brandId
                )
            }
        })
        { res ->
            setShowProgress(false)
            when (res!!.success) {
                true -> {
                    if (res.data!!.isNotEmpty()) {

                        obsIsLoading.set(false)
                        obsIsFull.set(false)
                        obsIsLoadingStores.set(false)
                        obsHideRecycler.set(true)

                        /* Stores List */
                        storesList = res.data as MutableList<SearchStoresOrProducts>
                        storesAdapter.updateList(storesList)
                    } else {
                        obsIsLoading.set(false)
                        obsIsLoadingStores.set(false)
                        obsIsEmpty.set(true)
                        obsIsFull.set(false)
                        obsHideRecycler.set(false)
                    }
                }

                else -> {
                    obsIsLoading.set(false)
                    obsIsLoadingStores.set(false)
                    obsIsEmpty.set(true)
                    obsIsFull.set(false)
                    obsHideRecycler.set(false)
                }
            }
        }
    }

}