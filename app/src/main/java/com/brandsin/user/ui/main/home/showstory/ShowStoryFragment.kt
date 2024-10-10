package com.brandsin.user.ui.main.home.showstory

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.VideoView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bolaware.viewstimerstory.Momentz
import com.bolaware.viewstimerstory.MomentzCallback
import com.brandsin.user.R
import com.brandsin.user.databinding.HomeFragmentShowStoryBinding
import com.brandsin.user.model.order.homepage.StoriesItem
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.utils.MyApp
import com.bumptech.glide.Glide

class ShowStoryFragment : BaseHomeFragment(), Observer<Any?>, MomentzCallback {

    lateinit var binding: HomeFragmentShowStoryBinding
    lateinit var viewModel: ShowStoryViewModel

    private val fragmentArgs: ShowStoryFragmentArgs by navArgs()

    var storiesItem = StoriesItem()
    private var num = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for requireActivity() fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.home_fragment_show_story, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[ShowStoryViewModel::class.java]
        binding.viewModel = viewModel

        storiesItem = fragmentArgs.storiesItem

        viewModel.storiesList = ArrayList()

        //viewModel.storiesList.add(storiesItem)

        viewModel.mutableLiveData.observe(viewLifecycleOwner, this)

        // Momentz(requireActivity(), viewModel.listOfViews, binding.container, this).removeAllViews()
        // show()

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

        Glide.with(MyApp.context).load(storiesItem.store!!.thumbnail)
            .error(R.drawable.app_logo)
            .into(binding.storyStoreImg)

        binding.storyStoreName.text = storiesItem.store!!.name

        binding.storyClose.setOnClickListener {
            num = 0
            done()
        }

        binding.seeStore.setOnClickListener {
            Momentz(requireActivity(), viewModel.listOfViews, binding.container, this).pause(false)
            binding.imageView6.visibility = View.GONE
            binding.textView11.visibility = View.GONE
            binding.btnShowStories.visibility = View.VISIBLE
            binding.btnShowStore.visibility = View.VISIBLE
        }

        binding.btnShowStories.setOnClickListener {
            num = 1
            done()
        }

        binding.btnShowStore.setOnClickListener {
            num = 2
            done()
        }
    }

    override fun onChanged(value: Any?) {
        if (value == null) return
        when (value) {
//            Codes.SHOW_STORY -> {
//                viewModel.setShowProgress(false)
//                show()
//            }
            else -> {
                if (value is String) {
                    showToast(value.toString(), 1)
                }
                viewModel.setShowProgress(false)
            }
        }
    }

    //    fun show(){
//        viewModel.listOfViews = ArrayList()
//        for (item in viewModel.storiesList) {
//            if (item.media.isNullOrEmpty()){
//                var textView = TextView(requireActivity())
//                textView.text = item.text
//                textView.textSize = 20f.toPixel(requireActivity()).toFloat()
//                textView.gravity = Gravity.CENTER
//                textView.setTextColor(Color.parseColor("#ffffff"))
//
//                var momentz = MomentzView(textView, 5)
//                viewModel.listOfViews.add(momentz)
//
//            }else if (item.media[0]!!.mimeType!!.contains("image")){
//
//                var image = ImageView(requireActivity())
//                var momentz = MomentzView(image, 10)
//                Picasso.get()
//                    .load(item.mediaUrl)
//                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
//                    .into(image, object : Callback {
//                        override fun onSuccess() {
//
//                        }
//                        override fun onError(e: Exception?) {
//                            Toast.makeText(requireActivity(),e?.localizedMessage, Toast.LENGTH_LONG).show()
//                            e?.printStackTrace()
//                        }
//                    })
//                viewModel.listOfViews.add(momentz)
//
//            }else if (item.media[0]!!.mimeType!!.contains("video")){
//
//                var video = VideoView(requireActivity())
//                var momentz = MomentzView(video, 60)
//                val str = item.mediaUrl
//                val uri = Uri.parse(str)
//                video.setVideoURI(uri)
//                viewModel.listOfViews.add(momentz)
//
//            }
//        }
//
//        Momentz(requireActivity(), viewModel.listOfViews, binding.container, this).start()
//
//    }
    override fun done() {
        when (num) {
            1 -> {
                view?.post {
                    val action =
                        ShowStoryFragmentDirections.showStoryToAddedStories(
                            storiesItem.storeId!!,
                            storiesItem
                        )
                    findNavController().navigate(action)
                }
            }

            2 -> {
                view?.post {
                    val action =
                        ShowStoryFragmentDirections.showStoryToStoreDetails(storiesItem.storeId!!)
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

    override fun onNextCalled(view: View, momentz: Momentz, index: Int) {
        if (view is VideoView) {
            momentz.pause(true)
            playVideo(view, index, momentz)
        } else if ((view is ImageView) && (view.drawable == null)) {
            // momentz.pause(true)
        }
    }

    private fun playVideo(videoView: VideoView, index: Int, momentz: Momentz) {

        videoView.requestFocus()
        videoView.start()

        videoView.setOnInfoListener(object : MediaPlayer.OnInfoListener {
            override fun onInfo(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                    // Here the video starts
                    momentz.editDurationAndResume(index, (videoView.duration) / 1000)
                    return true
                }
                return false
            }
        })
    }
}