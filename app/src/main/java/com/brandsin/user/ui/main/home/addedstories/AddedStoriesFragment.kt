package com.brandsin.user.ui.main.home.addedstories

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.brandsin.user.R
import com.brandsin.user.databinding.HomeFragmentAddedStoriesBinding
import com.brandsin.user.model.order.homepage.StoriesItem
import com.brandsin.user.network.observe
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.ui.main.order.storedetails.StoreDetailsFragmentDirections
import com.brandsin.user.utils.storyviewer.StoryView

class AddedStoriesFragment : BaseHomeFragment(), Observer<Any?>, StoryView.StoryViewListener {

    lateinit var binding: HomeFragmentAddedStoriesBinding

    lateinit var viewModel: AddedStoriesViewModel

    private val addStoriesArgs: AddedStoriesFragmentArgs by navArgs()

    var storiesItem = StoriesItem()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        setBarName(getString(R.string.added_stories))

        viewModel = ViewModelProvider(this)[AddedStoriesViewModel::class.java]
        binding.viewModel = viewModel

        storiesItem = addStoriesArgs.storiesItem

        viewModel.mutableLiveData.observe(viewLifecycleOwner, this)

        viewModel.setShowProgress(true)
        viewModel.getListStories(addStoriesArgs.storeId)
        viewModel.showProgress().observe(viewLifecycleOwner) { aBoolean ->
            if (!aBoolean!!) {
                binding.progressLayout.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            } else {
                binding.progressLayout.visibility = View.VISIBLE
                requireActivity().window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            }
        }

        observe(viewModel.addedStoriesAdapter.allStories) {
            val stories: MutableList<ArrayList<StoriesItem>> = ArrayList()

            it!!.forEach { value ->
                value.store = storiesItem.store
            }
            stories.add(it)
            val storyView = StoryView(0, stories)
            storyView.setStoryViewListener(this)
            storyView.show(childFragmentManager, "story")
        }
    }

    override fun onDoneClicked(num: Int, storiesItem: StoriesItem) {
        when (num) {
            2 -> {
                view?.post {
                    val action =
                        AddedStoriesFragmentDirections.storeToSelf(storiesItem.storeId!!)
                    findNavController().navigate(action)
                }
            }

            else -> {
                view?.post {
                    findNavController().navigateUp()
                }
            }
        }
    }

    override fun onChanged(value: Any?) {
        if (value == null) return
        when (value) {
            /*Codes.ADD_STORIES -> {
                findNavController().navigate(R.id.add_stories_to_add_story)
            }*/
            else -> {
                if (value is String) {
                    showToast(value.toString(), 1)
                }
                viewModel.setShowProgress(false)
            }
        }
    }
}