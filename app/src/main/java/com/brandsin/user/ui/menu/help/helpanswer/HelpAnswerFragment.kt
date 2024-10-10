package com.brandsin.user.ui.menu.help.helpanswer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import com.brandsin.user.R
import com.brandsin.user.databinding.HomeFragmentHelpAnswerBinding
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.utils.Utils

class HelpAnswerFragment : BaseHomeFragment() {

    private lateinit var binding: HomeFragmentHelpAnswerBinding

    private val fragmentArgs: HelpAnswerFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.home_fragment_help_answer, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBarName(getString(R.string.help))

        val helpQues = fragmentArgs.helpQues
        val phone = fragmentArgs.phoneNumber

        binding.tvTitle.text = helpQues.title
        binding.tvAnswer.text = helpQues.content

        binding.btnCall.setOnClickListener {
            Utils.callPhone(requireActivity(), phone)
        }
    }
}
