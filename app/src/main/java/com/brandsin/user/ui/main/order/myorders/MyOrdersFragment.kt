package com.brandsin.user.ui.main.order.myorders

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.brandsin.user.R
import com.brandsin.user.database.BaseRepository
import com.brandsin.user.databinding.HomeFragmentMyordersBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Const
import com.brandsin.user.model.constants.Params
import com.brandsin.user.model.order.cancel.CancelOrderResponse
import com.brandsin.user.model.order.cart.CartItem
import com.brandsin.user.model.order.cart.CartStoreData
import com.brandsin.user.model.order.cart.UserCart
import com.brandsin.user.model.order.myorders.MyOrderItem
import com.brandsin.user.model.order.reorder.ReOrderResponse
import com.brandsin.user.network.Status
import com.brandsin.user.ui.activity.auth.AuthActivity
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.ui.dialogs.removecartitem.DialogRemoveFragment
import com.brandsin.user.utils.MyApp
import com.brandsin.user.utils.PrefMethods
import com.brandsin.user.utils.Utils
import com.brandsin.user.utils.map.observe
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class MyOrdersFragment : BaseHomeFragment(), Observer<Any?>
{
    private lateinit var binding : HomeFragmentMyordersBinding
    private lateinit var viewModel: MyOrdersViewModel

    var userCart : UserCart? = null
    var cartStore : CartStoreData? = null
    var cartItemsList : ArrayList<CartItem>? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment_myorders, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(MyOrdersViewModel::class.java)
        binding.viewModel = viewModel

        setBarName(getString(R.string.my_orders))

        viewModel.mutableLiveData.observe(viewLifecycleOwner, this)

        binding.swipeLayout.setOnRefreshListener {
            binding.swipeLayout.isRefreshing = false
            viewModel.obsIsFull.set(false)
            viewModel.obsIsLoading.set(true)
            viewModel.getMyOrders()
        }

        observe(viewModel.ordersAdapter.detailsLiveData) {
            val action = MyOrdersFragmentDirections.myOrdersToOrderDetails(it!!.id!!.toInt())
            findNavController().navigate(action)
        }

        observe(viewModel.ordersAdapter.statusLiveData){
            when (it!!.status) {
                "pending" -> {
                    val bundle = Bundle()
                    bundle.putString(Params.DIALOG_CONFIRM_MESSAGE, MyApp.getInstance().getString(R.string.delete_order_warning))
                    bundle.putString(Params.DIALOG_CONFIRM_POSITIVE, MyApp.getInstance().getString(R.string.confirm))
                    bundle.putString(Params.DIALOG_CONFIRM_NEGATIVE, MyApp.getInstance().getString(R.string.ignore))
                    bundle.putSerializable(Params.DIALOG_ORDER_ITEM, it)
                    Utils.startDialogActivity(requireActivity(), DialogRemoveFragment::class.java.name, Codes.DIALOG_CONFIRM_REQUEST, bundle)
                }
                "completed" -> {
                    if (it.isRated !=null && it.isRated){

                        viewModel.obsIsVisible.set(true)
                        getReOrder(it.id!!)

                    }else{
                        val action = MyOrdersFragmentDirections.myOrdersToRateOrder(it.id!!.toInt())
                        findNavController().navigate(action)
                    }
                }
                else -> {
                    val action = MyOrdersFragmentDirections.myOrdersToOrderStatus(it.id!!.toInt())
                    findNavController().navigate(action)
                }
            }
        }

        observe(viewModel.ordersAdapter.orderItemLiveData){
            val action = MyOrdersFragmentDirections.myOrdersToOrderStatus(it!!.id!!.toInt())
            findNavController().navigate(action)
        }


        observe(viewModel.apiResponseLiveData) {
            when (it.status) {
                Status.ERROR_MESSAGE -> {
                    showToast(it.message.toString(), 1)
                }
                Status.SUCCESS_MESSAGE -> {
                    showToast(it.message.toString(), 2)
                }
                Status.SUCCESS -> {
                    when (it.data) {
                        is CancelOrderResponse -> {
                            showToast(it.data.message.toString(), 2)
                            viewModel.obsIsFull.set(false)
                            viewModel.obsIsLoading.set(true)
                            viewModel.getMyOrders()
                        }
                    }
                }
                else -> {
                    Timber.e(it.message)
                }
            }
        }
    }

    override fun onChanged(it: Any?)
    {
        if(it == null) return
        when (it)
        {
            Codes.LOGIN_CLICKED -> {
                PrefMethods.saveIsAskedToLogin(true)
                startActivity(Intent(requireActivity(), AuthActivity::class.java).putExtra(Const.ACCESS_LOGIN , true))
            }
        }
    }

    override fun onResume() {
        super.onResume()

        when {
            PrefMethods.getIsAskedToLogin() -> {
                viewModel.getUserStatus()
                PrefMethods.saveIsAskedToLogin(false)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Codes.DIALOG_CONFIRM_REQUEST && data != null) {
            if (data.hasExtra(Params.DIALOG_CLICK_ACTION)) {
                when {
                    data.getIntExtra(Params.DIALOG_CLICK_ACTION,1) == 1 -> {
                        when {
                            data.hasExtra(Params.DIALOG_ORDER_ITEM) -> {
                                val itemOrder = data.getSerializableExtra(Params.DIALOG_ORDER_ITEM) as MyOrderItem
                                viewModel.cancelOrder(itemOrder)
                            }
                        }
                    }
                }
            }
        }
    }

    fun getReOrder(orderId: Int){
        val baeRepo = BaseRepository()
        val responseCall: Call<ReOrderResponse?> = baeRepo.apiInterface
            .getReOrder(orderId,PrefMethods.getLanguage())
        responseCall.enqueue(object : Callback<ReOrderResponse?> {
            override fun onResponse(call: Call<ReOrderResponse?>, response: Response<ReOrderResponse?>) {
                viewModel.obsIsVisible.set(false)
                if (response.isSuccessful) {
                    if (response.body()!!.success!!) {

                        userCart = UserCart()
                        cartStore = CartStoreData()
                        cartItemsList = ArrayList<CartItem>()

                        response.body()!!.items!!.forEach { index ->
                            val item = CartItem()
                            item.run {
                                if (index != null) {
                                    productId = index.productId
                                    productName = index.productName
                                    productDescription = index.description
                                    productImage = index.productImage
                                    productQuantity = index.quantity!!
                                    productUnitPrice = index.salePrice!!.toDouble() // unit price
                                    if (index.addons!!.isEmpty()){
                                        addonsPrice = 0.0 // addons
                                        addonsIds = ArrayList<String>()
                                        addonsNames = ArrayList<String>()
                                    }else{
                                        addonsIds = ArrayList<String>()
                                        addonsNames = ArrayList<String>()
                                        index.addons.forEach { addon ->
                                            addonsPrice += addon!!.price!! // addons
                                            addonsIds!!.add(addon.id.toString())
                                            addonsNames!!.add(addon.name.toString())
                                        }
                                    }

                                    productItemPrice = productUnitPrice + addonsPrice // addons + unit price
                                    productTotalPrice = productItemPrice * index.quantity // item price * quantity
                                    skuCode = index.skuCode
                                    skuName = ""
                                    if (index.userNotes != null) {
                                        userNotes = index.userNotes.toString()
                                    }

                                    type = index.productType
                                    isOffer = false

                                }
                            }

                            cartItemsList!!.add(item)
                        }

                        cartStore?.run {
                            storeId = response.body()!!.order!!.store!!.id
                            storeName = response.body()!!.order!!.store!!.name
                            extraFees = response.body()!!.order!!.store!!.deliveryPrice!!.toDouble()
//                            if (response.body()!!.order!!.store!!.taxes!!.isNotEmpty()) {
//                                tax = response.body()!!.order!!.store!!.taxes!![0]!!.rate!!.toDouble()
//                            }
                            deliveryTime = response.body()!!.order!!.store!!.deliveryTime.toString()
                            if (response.body()!!.order!!.store!!.minOrderPrice != null){
                                minimumOrder = response.body()!!.order!!.store!!.minOrderPrice!!.toDouble()
                            }
                        }

                        userCart?.run {
                            cartItems = cartItemsList
                            cartStoreData = cartStore
                        }
                        PrefMethods.saveUserCart(userCart)

                        findNavController().navigate(R.id.my_orders_to_cart)

                    }
                } else {
                    showToast(response.message(),1)
                }
            }
            override fun onFailure(call: Call<ReOrderResponse?>, t: Throwable) {
                viewModel.obsIsVisible.set(false)
                showToast(t.message!!,1)
            }
        })
    }

}