package com.brandsin.user.ui.location.address

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.brandsin.user.R
import com.brandsin.user.databinding.ProfileFragmentAddressBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Const
import com.brandsin.user.model.constants.Params
import com.brandsin.user.model.location.AddedAddress
import com.brandsin.user.model.location.addresslist.AddressListItem
import com.brandsin.user.network.Status
import com.brandsin.user.ui.activity.auth.AuthActivity
import com.brandsin.user.ui.dialogs.confirm.DialogConfirmFragment
import com.brandsin.user.ui.dialogs.toast.DialogToastFragment
import com.brandsin.user.ui.location.addaddress.AddAddressActivity
import com.brandsin.user.ui.location.changeaddress.ChangeAddressActivity
import com.brandsin.user.utils.MyApp
import com.brandsin.user.utils.PrefMethods
import com.brandsin.user.utils.Utils
import com.brandsin.user.utils.map.observe
import timber.log.Timber

class ListAddressesActivity : AppCompatActivity(), Observer<Any?>
{
    lateinit var binding : ProfileFragmentAddressBinding
    lateinit var viewModel : ListAddressViewModel
    var flag : Int = 1
    lateinit var addressItem : AddressListItem

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.profile_fragment_address)
        viewModel = ViewModelProvider(this).get(ListAddressViewModel::class.java)
        binding.viewModel = viewModel
        
        viewModel.mutableLiveData.observe(this, this)

        when {
            intent.hasExtra(Params.DELIVERY_ADDRESSES_FLAG) -> {
                flag = intent.getIntExtra(Params.DELIVERY_ADDRESSES_FLAG, 1)
            }
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
//                    when (flag) {
//                        3 -> {
//                            startActivity(Intent(this, HomeActivity::class.java))
//                            finishAffinity()
//                        }
//                        2 -> {
//                            val bundle = Bundle()
//                            bundle.putParcelable(Params.ADDRESS_ITEM, addressItem)
//                            val intent = Intent()
//                            intent.putExtras(bundle)
//                            setResult(Codes.SHOW_DELIVERY_ADDRESSES_CODE, intent)
//                            finish()
//                        }
//                        else -> {
//                            val bundle = Bundle()
//                            bundle.putParcelable(Params.ADDRESS_ITEM, it.data as DefaultAddressItem)
//                            val intent = Intent()
//                            intent.putExtras(bundle)
//                            setResult(Codes.SHOW_DELIVERY_ADDRESSES_CODE, intent)
//                            finish()
//                        }
//                    }
                }
                else -> {
                    Timber.e(it.message)
                }
            }
        }

        observe(viewModel.addressAdapter.addressLiveData) {
            when (it) {
                is AddressListItem -> {
                    when (flag) {
                        2 -> {
                            when {
                                PrefMethods.getDefaultAddress() == null -> {
                                    addressItem = it
                                    viewModel.setDefaultAddress(it)
                                }
                                else -> {
                                    val bundle = Bundle()
                                    bundle.putParcelable(Params.ADDRESS_ITEM, it)
                                    val intent = Intent()
                                    intent.putExtras(bundle)
                                    setResult(Codes.SHOW_DELIVERY_ADDRESSES_CODE, intent)
                                    finish()
                                }
                            }
                        }
                        else -> {
                            viewModel.setDefaultAddress(it)
                            val bundle = Bundle()
                            bundle.putParcelable(Params.ADDRESS_ITEM, it)
                            val intent = Intent()
                            intent.putExtras(bundle)
                            setResult(Codes.SHOW_DELIVERY_ADDRESSES_CODE, intent)
                            finish()
                        }
                    }
                }
            }
        }

        observe(viewModel.addressAdapter.changeLiveData) {
            when (it) {
                is AddressListItem -> {
                    startActivityForResult((Intent(this, ChangeAddressActivity::class.java))
                        .putExtra(Params.ADDRESS_ITEM , it), Codes.CHANGE_ADDRESS_REQUEST_CODE)
                }
            }
        }

        observe(viewModel.addressAdapter.removeLiveData) {
            when (it) {
                is AddressListItem -> {
                    val bundle = Bundle()
                    bundle.putString(Params.DIALOG_CONFIRM_MESSAGE, MyApp.getInstance().getString(R.string.delete_address_warninng))
                    bundle.putString(Params.DIALOG_CONFIRM_POSITIVE, MyApp.getInstance().getString(R.string.confirm))
                    bundle.putString(Params.DIALOG_CONFIRM_NEGATIVE, MyApp.getInstance().getString(R.string.ignore))
                    bundle.putParcelable(Params.DIALOG_ADDRESS_ITEM, it)
                    Utils.startDialogActivity(this, DialogConfirmFragment::class.java.name, Codes.DIALOG_CONFIRM_REQUEST, bundle)
                }
            }
        }
    }

    override fun onChanged(t: Any?) {
        if(t == null) return
        when (t) {
            Codes.ADD_ADDRESS_CLICKED -> {
                startActivityForResult((Intent(this, AddAddressActivity::class.java)) ,  Codes.ADD_ADDRESS_REQUEST_CODE)
            }
            Codes.LOGIN_CLICKED -> {
                PrefMethods.saveIsAskedToLogin(true)
                startActivity(Intent(this, AuthActivity::class.java).putExtra(Const.ACCESS_LOGIN , true))
            }
            Codes.BACK_PRESSED -> {
                val intent = Intent()
                intent.putExtra(Params.DIALOG_CLICK_ACTION, 0)
                setResult(Codes.SHOW_DELIVERY_ADDRESSES_CODE, intent)
                finish()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        /* When This page opened navigate user to select new location from The map first */
        when {
            requestCode == Codes.ADD_ADDRESS_REQUEST_CODE && data != null -> {
                when {
                    data.hasExtra(Params.ADDED_ADDRESS) -> {
                        val addressItem = data.getParcelableExtra<AddedAddress>(Params.ADDED_ADDRESS)
                        when {
                            addressItem != null -> {
                                viewModel.getDeliveryAddresses()
                                viewModel.obsIsLoading.set(true)
                                viewModel.obsIsFull.set(false)
                                viewModel.obsIsLogin.set(true)
                                viewModel.obsIsEmpty.set(false)
                            } }
                    } }
            }
        }

        /* When This page opened navigate user to select new location from The map first */
        when {
            requestCode == Codes.CHANGE_ADDRESS_REQUEST_CODE && data != null -> {
                when {
                    data.hasExtra(Params.EDITED_ADDRESS) -> {
                        when {
                            data.getBooleanExtra(Params.EDITED_ADDRESS,false) -> {
                                viewModel.getDeliveryAddresses()
                                viewModel.obsIsLoading.set(true)
                                viewModel.obsIsFull.set(false)
                                viewModel.obsIsLogin.set(true)
                                viewModel.obsIsEmpty.set(false)
                            }
                        }
                    }
                }
            }
        }

        /* Callback of confirming delete address item dialog */
        when {
            requestCode == Codes.DIALOG_CONFIRM_REQUEST && data != null -> {
                when {
                    data.hasExtra(Params.DIALOG_CLICK_ACTION) -> {
                        when {
                            data.getIntExtra(Params.DIALOG_CLICK_ACTION,1) == 1 -> {
                                when {
                                    data.hasExtra(Params.DIALOG_ADDRESS_ITEM) -> {
                                        val itemAddress = data.getParcelableExtra<AddressListItem>(
                                            Params.DIALOG_ADDRESS_ITEM)
                                        viewModel.deleteDeliveryAddress(itemAddress!!)
                                    }
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

    fun showToast(msg : String, type : Int) {
        //succss 2
        //false  1
        val bundle = Bundle()
        bundle.putString(Params.DIALOG_TOAST_MESSAGE, msg)
        bundle.putInt(Params.DIALOG_TOAST_TYPE, type)
        Utils.startDialogActivity(this, DialogToastFragment::class.java.name, Codes.DIALOG_TOAST_REQUEST, bundle)
    }

    override fun onBackPressed()
    {
        super.onBackPressed()

        when(flag) {
            1-> {
                val intent = Intent()
                intent.putExtra(Params.DIALOG_CLICK_ACTION, 0)
                setResult(Codes.SHOW_DELIVERY_ADDRESSES_CODE, intent)
                finish()
            }
            2-> {
                when {
                    // User clicked back without choosing any address >> make him back to CART
                    PrefMethods.getDefaultAddress() == null -> {
                        val intent = Intent()
                        intent.putExtra(Params.DELIVERY_ADDRESS_EMPTY, 1)
                        setResult(Codes.SHOW_DELIVERY_ADDRESSES_CODE, intent)
                        finish()
                    }
                    // User clicked back without choosing any address >> but he has a default address >> So No changes detected
                    else -> {
                        val intent = Intent()
                        intent.putExtra(Params.DELIVERY_ADDRESS_EMPTY, 2)
                        setResult(Codes.SHOW_DELIVERY_ADDRESSES_CODE, intent)
                        finish()
                    }
                }
            }
        }
    }
}