package com.brandsin.user.ui.main.search

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.brandsin.user.R
import com.brandsin.user.databinding.HomeFragmentSearchBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Params
import com.brandsin.user.model.order.cart.CartItem
import com.brandsin.user.model.order.cart.CartParcelableClass
import com.brandsin.user.model.order.cart.CartStoreData
import com.brandsin.user.model.order.cart.UserCart
import com.brandsin.user.model.order.homepage.TagsItem
import com.brandsin.user.model.order.storedetails.StoreProductItem
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.ui.dialogs.confirm.DialogConfirmFragment
import com.brandsin.user.ui.dialogs.filter.DialogFilterFragment
import com.brandsin.user.ui.main.order.storedetails.StoreDetailsViewModel
import com.brandsin.user.ui.main.order.storedetails.addons.skus.activity.OrderAddonsActivity
import com.brandsin.user.ui.main.order.storedetails.addons.skus.dialog.DialogOrderAddonsFragment
import com.brandsin.user.utils.MyApp
import com.brandsin.user.utils.PrefMethods
import com.brandsin.user.utils.Utils
import com.brandsin.user.utils.map.observe

class
SearchFragment : BaseHomeFragment(), Observer<Any?> {

    private lateinit var binding: HomeFragmentSearchBinding

    private lateinit var viewModel: SearchViewModel

    private lateinit var storeViewModel: StoreDetailsViewModel

    private val searchArgs: SearchFragmentArgs by navArgs()

    private var storeProductItem: StoreProductItem? = null

    private var searchFor: String = "store"

    private var brandId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment_search, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[SearchViewModel::class.java]
        storeViewModel = ViewModelProvider(this)[StoreDetailsViewModel::class.java]
        binding.viewModel = viewModel

        viewModel.categoryId = searchArgs.categoryId

        when (searchArgs.from) {
            "home" -> {
                searchArgs.data.data?.categories?.forEach { categoriesItem ->
                    if (categoriesItem != null) {
                        categoriesItem.tags?.forEach { tagsItem ->
                            if (tagsItem != null) {
                                viewModel.tagsList.add(tagsItem)
                            }
                        }
                    }
                }
            }

            "homenew" -> {
                searchArgs.dataNew.categories?.forEach { categoriesItem ->
                    if (categoriesItem != null) {
                        categoriesItem.tags?.forEach { tagsItem ->
                            if (tagsItem != null) {
                                viewModel.tagsList.add(tagsItem)
                            }
                        }
                    }
                }
            }

            "brand" -> {
                brandId = searchArgs.categoryId.toInt()
                viewModel.searchProductOrStore(
                    "store",
                    binding.tvSearch.text.toString().trim(),
                    brandId
                )
            }

            "store" -> {
                binding.store.visibility = View.GONE
                binding.products.visibility = View.GONE
                storeViewModel.storeData = searchArgs.dataStoreNew
                viewModel.setIsFromStoreDetails()
                searchArgs.dataStoreNew.storeProductList?.forEach { productItem ->
                    if (productItem != null) {
                        viewModel.storesProductList.add(productItem)
                    }
                }
            }
        }

        viewModel.mutableLiveData.observe(viewLifecycleOwner, this)
        viewModel.productsAdapter.productLiveData.observe(viewLifecycleOwner, this)

        viewModel.showProgress().observe(viewLifecycleOwner) { aBoolean ->
            if (!aBoolean!!) {
                binding.progressLayout.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            } else {
                binding.progressLayout.visibility = View.VISIBLE
                requireActivity().window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            }
        }

        binding.ibBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.ibCancelSearch.setOnClickListener {
            binding.tvSearch.setText("")
        }

        binding.ibSearch.setOnClickListener {
            if (binding.tvSearch.text.toString().trim() == "") {
                showToast(getString(R.string.search_empty), 1)
            } else {
                viewModel.setShowProgress(true)
                // if (searchArgs.from != "store") {
                if (searchFor == "store") {
                    // viewModel.getSearch(binding.tvSearch.text.toString().trim())
                    viewModel.searchProductOrStore(
                        "store",
                        binding.tvSearch.text.toString().trim(),
                        brandId
                    )
                } else {
                    // viewModel.searchProduct(binding.tvSearch.text.toString().trim())
                    viewModel.searchProductOrStore(
                        "product",
                        binding.tvSearch.text.toString().trim(),
                        brandId
                    )
                }
            }
        }

        binding.filterLayout.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable(
                Params.DIALOG_SEARCH_FILTER,
                viewModel.tagsList as ArrayList<TagsItem>
            )
            bundle.putIntegerArrayList(Params.DIALOG_FILTER_TAGS, viewModel.tags)
            bundle.putString(Params.DIALOG_FILTER_SORT, viewModel.sort)
            Utils.startDialogActivity(
                requireActivity(),
                DialogFilterFragment::class.java.name,
                Codes.DIALOG_SEARCH_FILTER,
                bundle
            )
        }

        observe(viewModel.storesAdapter.storesLiveData) {
            if (searchFor == "product")
            {
                val bundle = Bundle()
                bundle.putInt("productID", it?.id?.toInt() ?: -1)

                findNavController().navigate(R.id.dialogOrderAddonsFragment,bundle)
            }
            else {
                val action = SearchFragmentDirections.searchToStoreDetails(it!!.id!!.toInt())
                findNavController().navigate(action)
            }
        }

        setBtnListener()

    }

    @SuppressLint("ResourceAsColor")
    private fun setBtnListener() {
        /*binding.tvSearch.addTextChangedListener {
            if (binding.tvSearch.text.toString().trim().lowercase(Locale.getDefault()) == "") {
                showToast(getString(R.string.search_empty), 1)
            } else {
                //if (searchArgs.from != "store") {
                if (searchFor == "store") {
                    //   viewModel.getSearch(binding.tvSearch.text.toString().trim())
                } else {
                    // viewModel.searchProduct(binding.tvSearch.text.toString().trim())
                    viewModel.searchProductOrStore(
                        "product",
                        binding.tvSearch.text.toString().trim()
                    )
                }
            }
        }*/

        /////////////////////////////////////////////
        binding.tvSearch.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (binding.tvSearch.text.toString().trim() == "") {
                    showToast(getString(R.string.search_empty), 1)
                } else {
                    // if (searchArgs.from != "store") {
                    if (searchFor == "store") {
                        // viewModel.getSearch(binding.tvSearch.text.toString().trim())
                        viewModel.searchProductOrStore(
                            "store",
                            binding.tvSearch.text.toString().trim(),
                            brandId
                        )
                    } else {
                        // viewModel.searchProduct(binding.tvSearch.text.toString().trim())
                        viewModel.searchProductOrStore(
                            "product",
                            binding.tvSearch.text.toString().trim(),
                            brandId
                        )
                    }
                }
                return@OnEditorActionListener true
            }
            false
        })

        binding.store.setOnClickListener {
            searchFor = "store"
            binding.store.setBackgroundResource(R.drawable.bg_selected_text_search)
            binding.store.setTextColor(R.color.color_primary)
            binding.products.setTextColor(R.color.black)
            binding.products.setBackgroundResource(R.drawable.bg_unselected_text_search)
            binding.tvSearch.setText("")
            if (brandId != null) {
                viewModel.searchProductOrStore(
                    searchFor,
                    binding.tvSearch.text.toString().trim(),
                    brandId
                )
            }
        }

        binding.products.setOnClickListener {
            searchFor = "product"
            binding.store.setBackgroundResource(R.drawable.bg_unselected_text_search)
            binding.store.setTextColor(R.color.black)
            binding.products.setTextColor(R.color.color_primary)
            binding.products.setBackgroundResource(R.drawable.bg_selected_text_search)
            binding.tvSearch.setText("")
            if (brandId != null) {
                viewModel.searchProductOrStore(
                    searchFor,
                    binding.tvSearch.text.toString().trim(),
                    brandId
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (PrefMethods.getUserCart() != null) {
            storeViewModel.userCart = UserCart()
            storeViewModel.cartStore = CartStoreData()
            storeViewModel.cartItemsList = ArrayList()

            storeViewModel.userCart = PrefMethods.getUserCart()!!
            storeViewModel.cartStore = storeViewModel.userCart!!.cartStoreData!!
            storeViewModel.cartItemsList =
                storeViewModel.userCart!!.cartItems as ArrayList<CartItem>
            storeViewModel.obsIsVisible.set(true)

            for (productsList in storeViewModel.productsList) {
                for (cartList in PrefMethods.getUserCart()!!.cartItems!!) {
                    if (cartList.productId == productsList.id) {
                        productsList.isSelected = true
                    }
                }
            }
        } else {
            storeViewModel.userCart = UserCart()
            storeViewModel.cartStore = CartStoreData()
            storeViewModel.cartItemsList = ArrayList()
            storeViewModel.obsIsVisible.set(false)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            /* When User back from Addons page */
            Codes.SELECT_ORDER_ADDONS_ACTIVITY, Codes.SELECT_ORDER_ADDONS_DIALOG -> {
                when {
                    data != null -> {
                        when {
                            data.hasExtra(Params.DIALOG_CLICK_ACTION) -> {
                                when {
                                    data.getIntExtra(Params.DIALOG_CLICK_ACTION, 1) == 1 -> {
                                        val cartParcelableItem : CartParcelableClass? =
                                            data.getParcelableExtra(Params.STORE_PRODUCT_ITEM)
                                        val cartItem = cartParcelableItem?.cartItem
                                        val storeProductItem = cartParcelableItem?.storeProductItem
                                        when (storeViewModel.cartStore!!.storeId) {
                                            null -> {
                                                // Cart is empty .. This is the first product added to cart
                                                when {
                                                    cartItem != null -> {
                                                        storeViewModel.addProductToCart(cartItem)
                                                        storeProductItem?.let {
                                                            viewModel.productsAdapter.notifyItemSelected(
                                                                it
                                                            )
                                                            requireActivity().onBackPressed()
                                                        }
                                                    }
                                                }
                                            }

                                            else -> {
                                                when {
                                                    // Cart is Not empty But the saved products NOT has the same store Id >> Clear the cart before adding new products
                                                    storeViewModel.cartStore!!.storeId != storeProductItem!!.storeId -> {
                                                        val bundle = Bundle()
                                                        bundle.putString(
                                                            Params.DIALOG_CONFIRM_MESSAGE,
                                                            MyApp.getInstance()
                                                                .getString(R.string.add_item_to_cart)
                                                        )
                                                        bundle.putString(
                                                            Params.DIALOG_CONFIRM_POSITIVE,
                                                            MyApp.getInstance()
                                                                .getString(R.string.confirm)
                                                        )
                                                        bundle.putString(
                                                            Params.DIALOG_CONFIRM_NEGATIVE,
                                                            MyApp.getInstance()
                                                                .getString(R.string.ignore)
                                                        )
                                                        bundle.putParcelable(
                                                            Params.DIALOG_STORE_ITEM,
                                                            cartParcelableItem
                                                        )
                                                        Utils.startDialogActivity(
                                                            requireActivity(),
                                                            DialogConfirmFragment::class.java.name,
                                                            Codes.DIALOG_CONFIRM_REQUEST,
                                                            bundle
                                                        )
                                                    }

                                                    else -> {
                                                        // Cart is Not empty and the all products has the sam Id
                                                        if (cartItem != null) {
                                                            storeViewModel.addProductToCart(cartItem)
                                                            viewModel.productsAdapter.notifyItemSelected(
                                                                storeProductItem
                                                            )
                                                            requireActivity().onBackPressed()
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    data.getIntExtra(Params.DIALOG_CLICK_ACTION, 1) == 0 -> {
                                        // User clicked back and no changed detected
                                    }
                                }
                            }
                        }
                    }
                }
            }


            Codes.DIALOG_SEARCH_FILTER -> {
                when {
                    data != null -> {
                        if (data.hasExtra(Params.DIALOG_CLICK_ACTION)) {
                            viewModel.tags =
                                data.getIntegerArrayListExtra(Params.DIALOG_FILTER_TAGS) as ArrayList<Int>
                            viewModel.sort =
                                data.getStringExtra(Params.DIALOG_FILTER_SORT).toString()
                            viewModel.setShowProgress(true)
                            if (searchArgs.from != "store") {
                                // viewModel.getSearch(binding.tvSearch.text.toString().trim())
                                viewModel.searchProductOrStore(
                                    "store",
                                    binding.tvSearch.text.toString().trim(),
                                    brandId
                                )
                            } else {
                                // viewModel.searchProduct(binding.tvSearch.text.toString().trim())
                                viewModel.searchProductOrStore(
                                    "product",
                                    binding.tvSearch.text.toString().trim(),
                                    brandId
                                )
                            }
                        }
                    }
                }
            }
            /*       *//* Ask user To Log In *//*
            Codes.DIALOG_NOT_LOGIN_REQUEST -> {
                when {
                    data != null -> {
                        when {
                            data.hasExtra(Params.DIALOG_CLICK_ACTION) -> {
                                when {
                                    data.getIntExtra(Params.DIALOG_CLICK_ACTION,1) == 1 -> {
                                        PrefMethods.saveIsAskedToLogin(true)
                                        startActivity(Intent(requireActivity(), AuthActivity::class.java).putExtra(Const.ACCESS_LOGIN , true))
                                    }
                                }
                            }
                        }
                    }
                }
            }*/
        }

    }

    override fun onChanged(value: Any?) {
        if (value == null) return
        when (value) {

            is StoreProductItem -> {
                storeProductItem = value
                when (value.type) {
                    "simple" -> {
                        val bundle = Bundle()
                        bundle.putParcelable(Params.STORE_PRODUCT_ITEM, value)
                        Utils.startDialogActivity(
                            requireActivity(),
                            DialogOrderAddonsFragment::class.java.name,
                            Codes.SELECT_ORDER_ADDONS_DIALOG,
                            bundle
                        )
                    }

                    "variable" -> {
                        val intent = Intent(requireActivity(), OrderAddonsActivity::class.java)
                        intent.putExtra(Params.STORE_PRODUCT_ITEM, value)
                        startActivityForResult(intent, Codes.SELECT_ORDER_ADDONS_ACTIVITY)
                    }
                }

                // This block of code if you want to check the user is logged in or not
                /* when {
                     PrefMethods.getUserData() == null -> {
                         Utils.startDialogActivity(requireActivity(), DialogNotLoginFragment::class.java.name, Codes.DIALOG_NOT_LOGIN_REQUEST, null)
                     }
                     else -> {
                         when(it.type) {
                             "simple" -> {
                                 val bundle = Bundle()
                                 bundle.putSerializable(Params.STORE_PRODUCT_ITEM, it)
                                 Utils.startDialogActivity(requireActivity(), DialogOrderAddonsFragment::class.java.name, Codes.SELECT_ORDER_ADDONS_DIALOG, bundle)
                             }
                             "variable" -> {
                                 val intent = Intent(requireActivity(), OrderAddonsActivity::class.java)
                                 intent.putExtra(Params.STORE_PRODUCT_ITEM, it)
                                 startActivityForResult(intent, Codes.SELECT_ORDER_ADDONS_ACTIVITY)
                             }
                         }
                     }
                 }*/
            }
        }
    }
}