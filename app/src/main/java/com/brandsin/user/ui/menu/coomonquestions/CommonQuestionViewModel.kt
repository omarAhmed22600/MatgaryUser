package com.brandsin.user.ui.menu.coomonquestions

import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.menu.commonquest.CommonQuesResponse
import com.brandsin.user.model.menu.commonquest.CommonQuestionItem
import com.brandsin.user.network.requestCall
import com.brandsin.user.utils.PrefMethods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CommonQuestionViewModel : BaseViewModel()
{
    var quesAdapter = CommonQuesAdapter()

    init {
        getCommonQues()
    }

    fun getCommonQues()
    {
        obsIsEmpty.set(false)
        obsIsFull.set(false)
        obsIsLoading.set(true)
        requestCall<CommonQuesResponse?>({ withContext(Dispatchers.IO) { return@withContext getApiRepo().getCommonQues("faq",
            PrefMethods.getLanguage()) } })
        { res ->
            when (res!!.isSuccess) {
                true -> {
                    res.let {
                        when {
                            it.questionsList!!.isNotEmpty() -> {
                                obsIsLoading.set(false)
                                obsIsFull.set(true)
                                quesAdapter.updateList(res.questionsList as ArrayList<CommonQuestionItem>)
                            }
                            else -> {
                                obsIsFull.set(false)
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}