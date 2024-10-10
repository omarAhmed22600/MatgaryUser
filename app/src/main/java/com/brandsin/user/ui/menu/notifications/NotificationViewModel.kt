package com.brandsin.user.ui.menu.notifications

import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.menu.notifications.NotificationItem
import com.brandsin.user.model.menu.notifications.NotificationResponse
import com.brandsin.user.model.menu.notifications.ReadNotificationRequest
import com.brandsin.user.model.menu.notifications.ReadNotificationResponse
import com.brandsin.user.model.sliders.SlidersResponse
import com.brandsin.user.network.requestCall
import com.brandsin.user.ui.main.homenew.moreslider.MoreSliderAdapter
import com.brandsin.user.utils.PrefMethods
import com.brandsin.user.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NotificationViewModel : BaseViewModel()
{
    var notificationAdapter  = NotificationsAdapter()
    var notificationsList : ArrayList<NotificationItem> = ArrayList()
    var slidersResponse = SingleLiveEvent<SlidersResponse>()
    var moreSliderAdapter = MoreSliderAdapter()

    init {
        getUserStatus()
    }

    fun getUserStatus() {
        when {
            PrefMethods.getUserData() != null -> {
                obsIsLogin.set(true)
                getNotifications()
            }
            else -> {
                obsIsLogin.set(false)
            }
        }
    }

    fun onLoginClicked() {
        setValue(Codes.LOGIN_CLICKED)
    }

    private fun getNotifications()
    {
        obsIsEmpty.set(false)
        obsIsFull.set(false)
        obsIsLoading.set(true)
        requestCall<NotificationResponse?>({
            withContext(Dispatchers.IO) {
                return@withContext getApiRepo().getNotifications(10 , 0, PrefMethods.getUserData()!!.id!!)
            }
        })
        { res ->
            getSlider("notifications")
            obsIsLoading.set(false)
            when (res!!.isSuccess) {
                true -> {
                    res.let {
                        when {
                            it.notificationsList!!.isNotEmpty() -> {
                                obsIsFull.set(true)
                                obsIsLoadingStores.set(false)
                                obsHideRecycler.set(true)
                                notificationsList = res.notificationsList as ArrayList<NotificationItem>
                                notificationAdapter.updateList(notificationsList)
                            }
                            else -> {
                                obsIsEmpty.set(true)
                                obsIsFull.set(false)
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }

    fun getSlider(key: String) {
        requestCall<SlidersResponse?>({
            withContext(Dispatchers.IO) {
                return@withContext getApiRepo().getSlider(key, PrefMethods.getLanguage())
            }
        })
        { res ->
            when (res!!.success) {
                true -> {
                    setShowProgress(false)
                    obsIsLoading.set(false)
                    obsIsFull.set(true)
                    obsIsLoadingStores.set(false)
                    obsHideRecycler.set(true)
                    res.data.let {
                        slidersResponse.value = res
                        if (it!!.slides.isNotEmpty()) {

                            /*slider*/
                            moreSliderAdapter.updateList(it.slides)


                        }
                    }
                }
                else -> {
                    obsIsLoading.set(false)
                    obsIsEmpty.set(true)
                    obsIsFull.set(false)
                }
            }
        }
    }


    fun makeReadNotification(item : NotificationItem)
    {
        val readRequest = ReadNotificationRequest(item.id, item.orderId, item.userId)
        requestCall<ReadNotificationResponse?>({ withContext(Dispatchers.IO) { return@withContext getApiRepo().readNotification(readRequest) } })
        { res ->

            obsIsLoading.set(false)
            when (res!!.isSuccess) {
                true -> {
                }
                else -> {}
            }
        }
    }
}