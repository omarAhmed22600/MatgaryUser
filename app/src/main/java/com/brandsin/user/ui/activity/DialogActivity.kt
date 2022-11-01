package com.brandsin.user.ui.activity

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.brandsin.user.R
import com.brandsin.user.model.constants.Params
import com.brandsin.user.utils.SingleLiveEvent
import com.brandsin.user.utils.Utils
import timber.log.Timber

class DialogActivity : ParentActivity()
{
    var isProgressShow = SingleLiveEvent<Boolean>()

    private fun addFragment(fragmentString: String?, bundle: Bundle?) {
        try {
            val fragment = Class.forName(fragmentString!!).newInstance() as Fragment
            fragment.arguments = bundle
            Utils.replaceFragment(this, fragment, "")
        } catch (e: IllegalAccessException) {
            Timber.e("Fragment Not Found \n %s", e)
        } catch (e: InstantiationException) {
            Timber.e("Fragment Not Found \n %s", e)
        } catch (e: ClassNotFoundException) {
            Timber.e("Fragment Not Found \n %s", e)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog)
        if (intent.hasExtra(Params.INTENT_PAGE_DIALOG)) {
            addFragment(
                intent.getStringExtra(Params.INTENT_PAGE_DIALOG),
                intent.getBundleExtra(Params.BUNDLE_DIALOG)
            )
        }
    }

    override fun onBackPressed() {
        //return nothing
        return
    }

}