package com.brandsin.user.utils.storyviewer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.FavoriteResponse
import com.brandsin.user.model.follow.FollowResponse
import com.brandsin.user.network.ResponseHandler
import com.brandsin.user.network.RetrofitBuilder
import com.brandsin.user.network.requestCall
import com.brandsin.user.network.toSingleEvent
import com.brandsin.user.utils.PrefMethods
import com.brandsin.user.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StoryDisplayViewModel : BaseViewModel()