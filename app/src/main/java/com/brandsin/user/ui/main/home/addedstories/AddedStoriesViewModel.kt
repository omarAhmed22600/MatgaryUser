package com.brandsin.user.ui.main.home.addedstories

import com.brandsin.user.database.BaseRepository
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.order.homepage.StoriesItem
import com.brandsin.user.model.order.homepage.addedstories.liststories.DataItem
import com.brandsin.user.model.order.homepage.addedstories.liststories.ListStoriesResponse

import omari.hamza.storyview.model.MyStory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class AddedStoriesViewModel: BaseViewModel() {

    var storiesList: ArrayList<DataItem> = ArrayList()
    var addedStoriesAdapter = AddedStoriesAdapter()
    var allstoriesList: MutableList<ArrayList<StoriesItem>> = ArrayList()
    var myStory = MyStory()
    var myStoryItem = StoriesItem()
    var myStoriesList: ArrayList<MyStory> = ArrayList()
    var simpleDateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss")

    fun getListStories(storeId: Int) {
        obsIsEmpty.set(false)
        obsIsFull.set(false)
        val baeRepo = BaseRepository()
        val responseCall: Call<ListStoriesResponse?> = baeRepo.apiInterface
            .getListStories(storeId)
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
                                    myStoryItem=StoriesItem()
                                    if (xItem.media.isNullOrEmpty()){
                                        myStory.url = ""
                                    }else{
                                        myStory.url = xItem.mediaUrl

                                    }
                                    myStoryItem.mediaUrl= myStory.url
                                    myStory.date = simpleDateFormat.parse(xItem.createdAt)
                                    myStoryItem.createdAt=xItem.createdAt
                                    myStory.description = xItem.text
                                    myStoryItem.text= xItem.text
                                    myStoriesList.add(myStory)
                                //    allstoriesList.add(storiesList.get(0).stories)
                                }
                            }
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
    }
}