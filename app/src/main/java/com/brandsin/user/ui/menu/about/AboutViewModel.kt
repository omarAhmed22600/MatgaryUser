package com.brandsin.user.ui.menu.about

import androidx.databinding.ObservableField
import com.brandsin.user.R
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.menu.about.AboutItem
import com.brandsin.user.model.menu.settings.SocialLinks
import com.brandsin.user.model.menu.settings.SocialLinksResponse
import com.brandsin.user.network.requestCall
import com.brandsin.user.utils.MyApp.Companion.context
import com.brandsin.user.utils.PrefMethods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AboutViewModel : BaseViewModel() {
    var aboutAdapter = AboutAdapter()
    var obsVersion = ObservableField<String>()
    var socialLinks = SocialLinks()

    private fun getAboutItems() {
        getSocialLinks()
        val aboutList = arrayListOf(
            AboutItem(1, context.getString(R.string.FAQ)),
            AboutItem(2, context.getString(R.string.share_your_rate_about_app))
        )
        aboutAdapter.updateList(aboutList)
    }

    init {
        getAboutItems()
    }

    fun onFaceClicked() {
        setValue(Codes.OPEN_FACE)
    }

    fun onInstaClicked() {
        setValue(Codes.OPEN_INSTA)
    }

    fun onTwitterClicked() {
        setValue(Codes.OPEN_TWITTER)
    }

    fun onTikTokClicked() {
        setValue(Codes.TIKTOK_CLICKED)
    }

    private fun getSocialLinks() {
        obsIsFull.set(false)
        obsIsLoading.set(true)
        requestCall<SocialLinksResponse?>({
            withContext(Dispatchers.IO) {
                return@withContext getApiRepo().getSocialLinks(
                    "social_links",
                    PrefMethods.getLanguage()
                )
            }
        })
        { res ->
            when (res!!.isSuccess) {
                true -> {
                    obsIsFull.set(true)
                    obsIsLoading.set(false)
                    socialLinks = res.socialLinks!!
                }

                else -> {}
            }
        }
    }
}