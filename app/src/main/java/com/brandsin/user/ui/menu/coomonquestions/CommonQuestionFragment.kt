package com.brandsin.user.ui.menu.coomonquestions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.brandsin.user.R
import com.brandsin.user.databinding.HomeFragmentAboutQuesBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.ui.activity.home.BaseHomeFragment

class CommonQuestionFragment : BaseHomeFragment(), Observer<Any?>
{
    private lateinit var viewModel: CommonQuestionViewModel
    private lateinit var binding: HomeFragmentAboutQuesBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment_about_ques, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(CommonQuestionViewModel::class.java)
        binding.viewModel = viewModel
        binding.swipeLayout.setOnRefreshListener {
            binding.swipeLayout.isRefreshing = false
            viewModel.getCommonQues()
        }
        setBarName(getString(R.string.common_questions))

        viewModel.mutableLiveData.observe(viewLifecycleOwner, this)
    }

    override fun onChanged(it: Any?)
    {
        if(it == null) return
        when (it) {
            Codes.EDIT_CLICKED -> {
                // do what you want
            }
        }
    }
}