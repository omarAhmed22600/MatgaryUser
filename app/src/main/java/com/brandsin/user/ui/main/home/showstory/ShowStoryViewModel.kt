package com.brandsin.user.ui.main.home.showstory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bolaware.viewstimerstory.MomentzView
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.FavoriteResponse
import com.brandsin.user.model.MessageResponse
import com.brandsin.user.model.follow.FollowResponse
import com.brandsin.user.model.order.homepage.StoriesItem
import com.brandsin.user.network.ResponseHandler
import com.brandsin.user.network.RetrofitBuilder
import com.brandsin.user.network.toSingleEvent
import com.brandsin.user.utils.PrefMethods
import com.brandsin.user.utils.storyviewer.model.StoryDetailsResponse
import kotlinx.coroutines.launch

class ShowStoryViewModel : BaseViewModel() {

    var storiesList: MutableList<ArrayList<StoriesItem>> = ArrayList()
    var listOfViews: ArrayList<MomentzView> = ArrayList()

    var storyItem: StoriesItem? = null

    /*    fun getListStories(){
            obsIsEmpty.set(false)
            obsIsFull.set(false)
            val baeRepo = BaseRepository()
            val responseCall: Call<ListStoriesResponse?> = baeRepo.apiInterface
                .getListStories(PrefMethods.getStoreData()!!.id!!.toInt())
            responseCall.enqueue(object : Callback<ListStoriesResponse?> {
                override fun onResponse(call: Call<ListStoriesResponse?>, response: Response<ListStoriesResponse?>) {
                    if (response.isSuccessful) {
                        if (response.body()!!.success!!) {
                            if (response.body()!!.data!!.isNotEmpty()) {
                                storiesList = response.body()!!.data as ArrayList<DataItem>
                                addedStoriesAdapter.updateList(storiesList)
                                setShowProgress(false)
                                obsIsEmpty.set(false)
                                obsIsFull.set(true)

                                for (item in storiesList) {
                                    for (xItem in item.stories!!) {
                                        myStory = MyStory()
                                        if (xItem!!.media.isNullOrEmpty()){
                                            myStory.url = ""
                                        }else{
                                            myStory.url = xItem.mediaUrl
                                        }
                                        myStory.date = simpleDateFormat.parse(xItem.createdAt)
                                        myStory.description = xItem.text
                                        myStoriesList.add(myStory)
                                    }
                                }
                                setValue(Codes.SHOW_STORY)
                            }else{
                                setShowProgress(false)
                                obsIsEmpty.set(true)
                                obsIsFull.set(false)
                            }
                        }
                    } else {
                        setValue(response.message())
                    }
                }
                override fun onFailure(call: Call<ListStoriesResponse?>, t: Throwable) {
                    setValue(t.message!!)
                    setShowProgress(false)
                }
            })
        }*/

    val apiInterface = RetrofitBuilder.API_SERVICE

    private val _addAndRemoveFavoriteResponse: MutableLiveData<ResponseHandler<FavoriteResponse?>> =
        MutableLiveData()
    val addAndRemoveFavoriteResponse: LiveData<ResponseHandler<FavoriteResponse?>> =
        _addAndRemoveFavoriteResponse.toSingleEvent()

    private val _getStoryDetailsByIdResponse: MutableLiveData<ResponseHandler<StoryDetailsResponse?>> =
        MutableLiveData()
    val getStoryDetailsByIdResponse: LiveData<ResponseHandler<StoryDetailsResponse?>> =
        _getStoryDetailsByIdResponse.toSingleEvent()

    private val _getFollowResponseResponse: MutableLiveData<ResponseHandler<FollowResponse?>> =
        MutableLiveData()
    val getFollowResponseResponse: LiveData<ResponseHandler<FollowResponse?>> =
        _getFollowResponseResponse.toSingleEvent()

    private val _updateViewStoryResponse: MutableLiveData<ResponseHandler<MessageResponse?>> =
        MutableLiveData()
    val updateViewStoryResponse: LiveData<ResponseHandler<MessageResponse?>> =
        _updateViewStoryResponse.toSingleEvent()

    fun newFollowStore(storeId: Int?) {
        viewModelScope.launch {
            safeApiCall {
                // Make your API call here using Retrofit service or similar
                apiInterface.newFollowStore(
                    storeId,
                    PrefMethods.getUserData()?.id!!,
                    PrefMethods.getLanguage()
                )
            }.collect {
                _getFollowResponseResponse.value = it
            }
        }
    }

    fun addAndRemoveFavorite(storyId: Int?) {
        viewModelScope.launch {
            safeApiCall {
                // Make your API call here using Retrofit service or similar
                apiInterface.addAndRemoveFavorite(
                    PrefMethods.getUserData()!!.id!!,
                    "story",
                    storyId.toString(),
                    PrefMethods.getLanguage()
                )
            }.collect {
                _addAndRemoveFavoriteResponse.value = it
            }
        }
    }

    fun getStoryDetailsById(storyId: Int) {
        viewModelScope.launch {
            safeApiCall {
                // Make your API call here using Retrofit service or similar
                apiInterface.getStoryDetailsById(storyId)
            }.collect {
                _getStoryDetailsByIdResponse.value = it
            }
        }
    }

    fun updateViewStory(storyId: Int) {
        viewModelScope.launch {
            safeApiCall {
                // Make your API call here using Retrofit service or similar
                apiInterface.updateViewStory(storyId, PrefMethods.getUserData()?.id ?: 0)
            }.collect {
                _updateViewStoryResponse.value = it
            }
        }
    }
}