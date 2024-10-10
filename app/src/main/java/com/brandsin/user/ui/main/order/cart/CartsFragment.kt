package com.brandsin.user.ui.main.order.cart

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.brandsin.user.R
import com.brandsin.user.databinding.HomeFragmentCartBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Const
import com.brandsin.user.model.constants.Params
import com.brandsin.user.model.order.cart.CartItem
import com.brandsin.user.ui.activity.auth.AuthActivity
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.ui.dialogs.removecartitem.DialogRemoveFragment
import com.brandsin.user.utils.MyApp
import com.brandsin.user.utils.PrefMethods
import com.brandsin.user.utils.Utils

class CartsFragment : BaseHomeFragment(), Observer<Any?> {

    private lateinit var binding: HomeFragmentCartBinding

    private lateinit var viewModel: CartsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment_cart, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[CartsViewModel::class.java]
        binding.viewModel = viewModel

        viewModel.mutableLiveData.observe(viewLifecycleOwner, this)

        binding.ibBack.setOnClickListener {
            // requireActivity().onBackPressed()
            findNavController().navigateUp()
        }

        viewModel.cartsAdapter.removeCartLiveData.observe(viewLifecycleOwner, this)
        viewModel.cartsAdapter.increaseLiveData.observe(viewLifecycleOwner) {
            it.let { item ->
                viewModel.inCreaseCartCount(item!!)
            }
        }

        viewModel.cartsAdapter.decreaseLiveData.observe(viewLifecycleOwner) {
            it.let { item ->
                viewModel.deCreaseCartCount(item!!)
            }
        }
    }

    override fun onChanged(value: Any?) {
        if (value == null) return
        when (value) {
            Codes.LOWER_ORDER_COST -> {
                showToast(getString(R.string.lower_order_cost), 1)
                /*val bundle = Bundle()
                bundle.putString(Params.DIALOG_CONFIRM_MESSAGE, MyApp.getInstance().getString(R.string.lower_order_cost))
                bundle.putString(Params.DIALOG_CONFIRM_POSITIVE, MyApp.getInstance().getString(R.string.confirm))
                bundle.putString(Params.DIALOG_CONFIRM_NEGATIVE, MyApp.getInstance().getString(R.string.ignore))
                Utils.startDialogActivity(requireActivity(), DialogConfirmFragment::class.java.name, Codes.DIALOG_CONFIRM_REQUEST, bundle)*/
            }

            Codes.LOGIN_CLICKED -> {
                PrefMethods.saveIsAskedToLogin(true)
                startActivity(
                    Intent(
                        requireActivity(),
                        AuthActivity::class.java
                    ).putExtra(Const.ACCESS_LOGIN, true)
                )
            }

            Codes.MAKE_ORDER_CLICKED -> {
                val action = CartsFragmentDirections.cartToConfirmOrder(viewModel.userCartData)
                findNavController().navigate(action)
            }

            Codes.ADD_MORE_CLICKED -> {
                val action =
                    CartsFragmentDirections.cartToStoreDetails(viewModel.userCartData.cartStoreData!!.storeId!!.toInt())
                findNavController().navigate(action)
            }

            is CartItem -> {
                val bundle = Bundle()
                bundle.putString(
                    Params.DIALOG_CONFIRM_MESSAGE,
                    MyApp.getInstance().getString(R.string.delete_cart_warning)
                )
                bundle.putString(
                    Params.DIALOG_CONFIRM_POSITIVE,
                    MyApp.getInstance().getString(R.string.confirm)
                )
                bundle.putString(
                    Params.DIALOG_CONFIRM_NEGATIVE,
                    MyApp.getInstance().getString(R.string.ignore)
                )
                bundle.putParcelable(Params.DIALOG_CART_ITEM, value)
                Utils.startDialogActivity(
                    requireActivity(),
                    DialogRemoveFragment::class.java.name,
                    Codes.DIALOG_CONFIRM_REQUEST,
                    bundle
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Codes.DIALOG_CONFIRM_REQUEST && data != null) {
            if (data.hasExtra(Params.DIALOG_CLICK_ACTION)) {
                when {
                    data.getIntExtra(Params.DIALOG_CLICK_ACTION, 1) == 1 -> {
                        when {
                            data.hasExtra(Params.DIALOG_CART_ITEM) -> {
                                val itemCart: CartItem? =
                                    data.getParcelableExtra(Params.DIALOG_CART_ITEM)
                                if (itemCart != null) {
                                    viewModel.removeItemFromCart(itemCart)
                                }
                            }
                        }
                    }
                }
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
}