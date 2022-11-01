package com.brandsin.user.ui.main.home.showstory

import com.bolaware.viewstimerstory.MomentzView
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.order.homepage.StoriesItem
import java.util.ArrayList

class ShowStoryViewModel  : BaseViewModel() {

    var storiesList: MutableList<ArrayList<StoriesItem>> = ArrayList()
    var listOfViews : ArrayList<MomentzView> = ArrayList()


//    fun getListStories(){
//        obsIsEmpty.set(false)
//        obsIsFull.set(false)
//        val baeRepo = BaseRepository()
//        val responseCall: Call<ListStoriesResponse?> = baeRepo.apiInterface
//            .getListStories(PrefMethods.getStoreData()!!.id!!.toInt())
//        responseCall.enqueue(object : Callback<ListStoriesResponse?> {
//            override fun onResponse(call: Call<ListStoriesResponse?>, response: Response<ListStoriesResponse?>) {
//                if (response.isSuccessful) {
//                    if (response.body()!!.success!!) {
//                        if (response.body()!!.data!!.isNotEmpty()) {
//                            storiesList = response.body()!!.data as ArrayList<DataItem>
//                            addedStoriesAdapter.updateList(storiesList)
//                            setShowProgress(false)
//                            obsIsEmpty.set(false)
//                            obsIsFull.set(true)
//
//                            for (item in storiesList) {
//                                for (xItem in item.stories!!) {
//                                    myStory = MyStory()
//                                    if (xItem!!.media.isNullOrEmpty()){
//                                        myStory.url = ""
//                                    }else{
//                                        myStory.url = xItem.mediaUrl
//                                    }
//                                    myStory.date = simpleDateFormat.parse(xItem.createdAt)
//                                    myStory.description = xItem.text
//                                    myStoriesList.add(myStory)
//                                }
//                            }
//                            setValue(Codes.SHOW_STORY)
//                        }else{
//                            setShowProgress(false)
//                            obsIsEmpty.set(true)
//                            obsIsFull.set(false)
//                        }
//                    }
//                } else {
//                    setValue(response.message())
//                }
//            }
//            override fun onFailure(call: Call<ListStoriesResponse?>, t: Throwable) {
//                setValue(t.message!!)
//                setShowProgress(false)
//            }
//        })
//    }
}