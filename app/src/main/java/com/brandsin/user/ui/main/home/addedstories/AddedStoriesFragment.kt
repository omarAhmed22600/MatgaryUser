package com.brandsin.user.ui.main.home.addedstories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.brandsin.user.R
import com.brandsin.user.databinding.HomeFragmentAddedStoriesBinding
import com.brandsin.user.model.order.homepage.StoriesItem
import com.brandsin.user.network.observe
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.utils.storyviewer.StoryView

class AddedStoriesFragment : BaseHomeFragment(), Observer<Any?> {
    lateinit var binding: HomeFragmentAddedStoriesBinding
    lateinit var viewModel: AddedStoriesViewModel
    private val addStoriesArgs: AddedStoriesFragmentArgs by navArgs()
    var storiesItem = com.brandsin.user.model.order.homepage.StoriesItem()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.home_fragment_added_stories,
            container,
            false
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(AddedStoriesViewModel::class.java)

        binding.viewModel = viewModel
        storiesItem = addStoriesArgs.storiesItem

        setBarName(getString(R.string.added_stories))

        viewModel.mutableLiveData.observe(viewLifecycleOwner, this)

        viewModel.setShowProgress(true)
        viewModel.getListStories(addStoriesArgs.storeId)
        viewModel.showProgress().observe(viewLifecycleOwner, { aBoolean ->
            if (!aBoolean!!) {
                binding.progressLayout.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            } else {
                binding.progressLayout.visibility = View.VISIBLE
                requireActivity().window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                );
            }
        })



        observe(viewModel.addedStoriesAdapter.allstories) {
            val stories: MutableList<ArrayList<StoriesItem>> = ArrayList()

            it!!.forEach {
                it.store = storiesItem.store
            }
            stories.add(it)
            var storyv: StoryView = StoryView(0, stories)
            storyv.setStoryViewListener(null)
            storyv.show(childFragmentManager, "story")
        }
    }


    override fun onChanged(it: Any?) {
        if (it == null) return
        when (it) {
//            Codes.ADD_STORIES -> {
//                findNavController().navigate(R.id.add_stories_to_add_story)
//            }
            else -> {
                if (it is String) {
                    showToast(it.toString(), 1)
                }
                viewModel.setShowProgress(false)
            }
        }
    }
}