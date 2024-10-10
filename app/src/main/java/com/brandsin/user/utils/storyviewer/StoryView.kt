package com.brandsin.user.utils.storyviewer

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.brandsin.user.R
import com.brandsin.user.databinding.DialogStoriesBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Params
import com.brandsin.user.model.order.homepage.StoriesItem
import com.brandsin.user.network.ResponseHandler
import com.brandsin.user.ui.dialogs.toast.DialogToastFragment
import com.brandsin.user.ui.main.home.showstory.ShowStoryViewModel
import com.brandsin.user.utils.MyApp
import com.brandsin.user.utils.PrefMethods
import com.brandsin.user.utils.PullDismissLayout
import com.brandsin.user.utils.Utils
import com.brandsin.user.utils.storyviewer.callBack.TouchCallbacks
import com.brandsin.user.utils.storyviewer.customview.StoryPagerAdapter
import com.brandsin.user.utils.storyviewer.utils.CubeOutTransformer
import com.bumptech.glide.Glide
import com.danikula.videocache.HttpProxyCacheServer
import com.google.android.exoplayer2.upstream.DataSpec
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class StoryView(
    var currentPage: Int,
    private val storiesList: MutableList<ArrayList<StoriesItem>>
) : DialogFragment(), PullDismissLayout.Listener, Observer<Any?>, PageViewOperator, TouchCallbacks {

    lateinit var binding: DialogStoriesBinding
    private lateinit var pagerAdapter: StoryPagerAdapter

    //  var storiesItem = StoriesItem
    // private var currentPage: Int = 0
    lateinit var viewModel: ShowStoryViewModel

    //Touch Events
    private var isDownClick = false
    private var elapsedTime: Long = 0
    private var timerThread: Thread? = null
    private var isPaused = false
    private var width = 0
    private var height: Int = 0
    private var xValue = 0f
    private var yValue: Float = 0f
    private var num = 0
    private val isRtl = false
    private var storyViewListener: StoryViewListener? = null

    /**
     * Change ViewPage sliding programmatically(not using reflection).
     * https://tech.dely.jp/entry/2018/12/13/110000
     * What for?
     * setCurrentItem(int, boolean) changes too fast. And it cannot set animation duration.
     */
    private var prevDragPosition = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // Inflate the layout for requireActivity() fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_stories, container, false)
        return binding.root
    }

    fun setStoryViewListener(storyViewListener: StoryViewListener?) {
        this.storyViewListener = storyViewListener
    }

    interface StoryViewListener {
        fun onDoneClicked(num: Int, storiesItem: StoriesItem)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val displayMetrics = DisplayMetrics()
        requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
        width = displayMetrics.widthPixels
        height = displayMetrics.heightPixels

        viewModel = ViewModelProvider(this)[ShowStoryViewModel::class.java]
        binding.viewModel = viewModel

        // storiesItem = fragmentArgs.storiesItem
        viewModel.storiesList = ArrayList()
        viewModel.storiesList = storiesList
        // viewModel.storiesList.add(storiesItem)

        viewModel.mutableLiveData.observe(viewLifecycleOwner, this)

        initView()
        setUpPager()
        // Get field from view
//        readArguments()
//        setupViews(view)
//        setupStories()

        (view.findViewById<View>(R.id.pull_dismiss_layout) as PullDismissLayout).setListener(this)

        (view.findViewById<View>(R.id.pull_dismiss_layout) as PullDismissLayout)
            .setmTouchCallbacks(this)

//        binding.storyClose.setOnClickListener {
//            num = 0
//            done()
//        }

        if (storyViewListener == null) {
            binding.seeStore.visibility = View.GONE
        }

        setBtnListener()
        subscribeData()
    }

    private fun initView() {
        Glide.with(this)
            .load(viewModel.storiesList[currentPage][0].store?.thumbnail)
            .error(R.drawable.app_logo)
            .into(binding.imgStore)

        binding.storeName.text =
            viewModel.storiesList[currentPage][0].store?.name

        binding.favoriteCount.text =
            viewModel.storiesList[currentPage][0].favCount.toString()

        binding.followersCount.text =
            viewModel.storiesList[currentPage][0].views.toString()

        if (viewModel.storiesList[currentPage][0].isFavorite == true) {
            binding.imgFavorite.setImageResource(R.drawable.ic_primary_favorite)
        } else {
            binding.imgFavorite.setImageResource(R.drawable.ic_normal_favorite)
        }

        if (viewModel.storiesList[currentPage][0].store?.isFollowed == true) {
            binding.storeFollow.text = getString(R.string.followed)
        } else {
            binding.storeFollow.text = getString(R.string.follow)
        }
    }

    private fun setBtnListener() {
        binding.btnShowStories.setOnClickListener {
            num = 1
            dismiss()
            dismissAllowingStateLoss()
            storyViewListener?.onDoneClicked(
                num,
                viewModel.storiesList[currentPage][0]
            )
        }

        binding.close.setOnClickListener {
            dismiss()
            dismissAllowingStateLoss()
        }

        // binding.btnShowStore.setOnClickListener {}
        binding.imgStore.setOnClickListener {
            num = 2
            dismiss()
            dismissAllowingStateLoss()
            storyViewListener?.onDoneClicked(
                num,
                viewModel.storiesList[currentPage][0]
            )
        }

        binding.seeStore.setOnClickListener {
            // Momentz(requireActivity(), listOf(), binding.container, this).pause(false)
            binding.imageView6.visibility = View.GONE
            binding.textView11.visibility = View.GONE
            binding.btnShowStories.visibility = View.VISIBLE
            binding.btnShowStore.visibility = View.VISIBLE
        }

        binding.linFavoriteCount.setOnClickListener {
            // call api store follow
            if (PrefMethods.getLoginState(context)) {
                viewModel.addAndRemoveFavorite(viewModel.storiesList[currentPage][0].id)
            } else {
                showToast("please login first", 1)
            }
        }

        binding.storeFollow.setOnClickListener {
            // call api store follow
            if (PrefMethods.getLoginState(context)) {
                viewModel.newFollowStore(viewModel.storiesList[currentPage][0].storeId)
            } else {
                showToast("please login first", 1)
            }
        }
    }

    private fun subscribeData() {
        /*observe(viewModel.followResponse) {
            when (it!!.success) {
                true -> {
                    showToast(it.message.toString(), 2)
                    // viewModel.getStoreDetails(storeArgs.storeId)
                }

                else -> {
                    Timber.e(it.message)
                }
            }
        }*/

        viewModel.getFollowResponseResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseHandler.Success -> {
                    // showToast(it.data?.message.toString(), 2)
                    Toast.makeText(
                        requireActivity(),
                        it.data?.message.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }

                is ResponseHandler.Error -> {
                    // show error message
                    // viewModel.obsIsEmpty.set(true)
                }

                is ResponseHandler.Loading -> {
                    // show a progress bar
                    // viewModel.obsIsLoading.set(true)
                }

                is ResponseHandler.StopLoading -> {
                    // show a progress bar
                    // viewModel.obsIsLoading.set(false)
                }

                else -> {}
            }
        }

        viewModel.addAndRemoveFavoriteResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseHandler.Success -> {
                    // showToast(it.data?.message.toString(), 2)
                    // viewModel.getAllFavoriteProduct()
                    if (it.data?.message.toString() == getString(R.string.added_story_to_my_favorite)) {
                        binding.imgFavorite.setImageResource(R.drawable.ic_primary_favorite)
                    } else {
                        binding.imgFavorite.setImageResource(R.drawable.ic_normal_favorite)
                    }

                    binding.animationFavorite.visibility = View.VISIBLE
                    binding.animationFavorite.playAnimation()
                }

                is ResponseHandler.Error -> {
                    // show error message
                    viewModel.obsIsEmpty.set(true)
                }

                is ResponseHandler.Loading -> {
                    // show a progress bar
                    viewModel.obsIsLoading.set(true)
                }

                is ResponseHandler.StopLoading -> {
                    // show a progress bar
                    viewModel.obsIsLoading.set(false)
                }

                else -> {}
            }
        }
    }

    fun showToast(msg: String, type: Int) {
        // Success 2
        // False  1
        val bundle = Bundle()
        bundle.putString(Params.DIALOG_TOAST_MESSAGE, msg)
        bundle.putInt(Params.DIALOG_TOAST_TYPE, type)
        Utils.startDialogActivity(
            requireActivity(),
            DialogToastFragment::class.java.name,
            Codes.DIALOG_TOAST_REQUEST,
            bundle
        )
    }

    /*override fun onNextCalled(view: View, momentz: Momentz, index: Int) {
        if (view is VideoView) {
            momentz.pause(true)
            //   playVideo(view, index, momentz)
        } else if ((view is ImageView) && (view.drawable == null)) {
//            momentz.pause(true)

        }
    }*/

    override fun onDismissed() {
        dismissAllowingStateLoss()
    }

    override fun onShouldInterceptTouchEvent(): Boolean {
        return false
    }

    private fun setUpPager() {
        // val storyUserList = StoryGenerator.generateStories()
        //preLoadStories(viewModel.storiesList[currentPage])

        pagerAdapter = StoryPagerAdapter(
            this,
            childFragmentManager,
            viewModel.storiesList
        )
        binding.viewPagerFixedViewPager.adapter = pagerAdapter
        binding.viewPagerFixedViewPager.currentItem = currentPage
        binding.viewPagerFixedViewPager.setPageTransformer(
            true,
            CubeOutTransformer()
        )
        binding.viewPagerFixedViewPager.addOnPageChangeListener(object : PageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPage = position
                Log.d("TAG", "onPageSelected: $currentPage")
            }

            override fun onPageScrollCanceled() {
                currentFragment().resumeCurrentStory()
            }
        })
    }

    override fun backPageView() {
        //if (viewPagerFixedViewPager != null) {
        if (binding.viewPagerFixedViewPager.currentItem > 0) {
            try {
                fakeDrag(false)
            } catch (e: Exception) {
                //NO OP
            }
        }
        //    }
    }

    override fun nextPageView() {
        // if (viewPagerFixedViewPager != null) {
        if (binding.viewPagerFixedViewPager.currentItem + 1 < binding.viewPagerFixedViewPager.adapter?.count ?: 0) {
            try {
                fakeDrag(true)
            } catch (e: Exception) {
                //NO OP
            }
        } else {
            dismiss()
            dismissAllowingStateLoss()
            // there is no next story
            // Toast.makeText(context, "All stories displayed.", Toast.LENGTH_LONG).show()
        }
        //   }
    }

    private fun fakeDrag(forward: Boolean) {
        //if (viewPagerFixedViewPager != null) {
        if (prevDragPosition == 0 && binding.viewPagerFixedViewPager.beginFakeDrag()) {
            ValueAnimator.ofInt(0, binding.viewPagerFixedViewPager.width).apply {
                duration = 400L
                interpolator = FastOutSlowInInterpolator()
                addListener(object : Animator.AnimatorListener {
                    override fun onAnimationRepeat(animation: Animator) {}

                    override fun onAnimationEnd(animation: Animator) {
                        removeAllUpdateListeners()
                        if (binding.viewPagerFixedViewPager.isFakeDragging) {
                            binding.viewPagerFixedViewPager.endFakeDrag()
                        }
                        prevDragPosition = 0
                    }

                    override fun onAnimationCancel(animation: Animator) {
                        removeAllUpdateListeners()
                        if (binding.viewPagerFixedViewPager.isFakeDragging) {
                            binding.viewPagerFixedViewPager.endFakeDrag()
                        }
                        prevDragPosition = 0
                    }

                    override fun onAnimationStart(animation: Animator) {}
                })
                addUpdateListener {
                    if (!binding.viewPagerFixedViewPager.isFakeDragging) return@addUpdateListener
                    val dragPosition: Int = it.animatedValue as Int
                    val dragOffset: Float =
                        ((dragPosition - prevDragPosition) * if (forward) -1 else 1).toFloat()
                    prevDragPosition = dragPosition
                    binding.viewPagerFixedViewPager.fakeDragBy(dragOffset)
                }
            }.start()
        }
        //  }
    }

    private fun preLoadStories(storyUserList: List<StoriesItem>) {
        val imageList = mutableListOf<StoriesItem>()
        val videoList = mutableListOf<StoriesItem>()

//        storyUserList.forEach {
//
//            if (it.media.isNullOrEmpty()) {
//                var textView = TextView(requireActivity())
//                textView.text = it.text
//                textView.textSize = 20f.toPixel(requireActivity()).toFloat()
//                textView.gravity = Gravity.CENTER
//                textView.setTextColor(Color.parseColor("#ffffff"))
//
//                var momentz = MomentzView(textView, 5)
//                viewModel.listOfViews.add(momentz)
//
//            } else if (it.media[0]!!.mimeType!!.contains("image")) {
//
//                var image = ImageView(requireActivity())
//                var momentz = MomentzView(image, 10)
//                Picasso.get()
//                    .load(it.mediaUrl)
//                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
//                    .into(image, object : Callback {
//                        override fun onSuccess() {
//
//                        }
//
//                        override fun onError(e: Exception?) {
//                            Toast.makeText(
//                                requireActivity(),
//                                e?.localizedMessage,
//                                Toast.LENGTH_LONG
//                            ).show()
//                            e?.printStackTrace()
//                        }
//                    })
//                viewModel.listOfViews.add(momentz)
//                imageList.add(it)
//            } else if (it.media[0]!!.mimeType!!.contains("video")) {
//
//                var video = VideoView(requireActivity())
//                var momentz = MomentzView(video, 60)
//                val str = it.mediaUrl
//                val uri = Uri.parse(str)
//                video.setVideoURI(uri)
//                viewModel.listOfViews.add(momentz)
//                videoList.add(it)
//            }
//
////            storyUser.stories.forEach { story ->
////                if (story.isVideo()) {
////                    videoList.add(story.url)
////                } else {
////                    imageList.add(story.url)
////                }
////            }
//        }
        // preLoadVideos(videoList)
        // preLoadImages(imageList)
        //preLoadImages(imageList)
    }

    private fun preLoadVideos(videoList: MutableList<StoriesItem>) {

        videoList.map { data ->
            GlobalScope.async {
                Log.d("TAG", "preLoadVideos: " + data.mediaUrl)
                val proxy: HttpProxyCacheServer = MyApp.getInstance().getProxy(requireActivity())!!
                val proxyUrl = proxy.getProxyUrl(data.mediaUrl.toString())
                val dataUri = Uri.parse(proxyUrl)
                val dataSpec = DataSpec(
                    dataUri, 0,
                    DataSpec.FLAG_DONT_CACHE_IF_LENGTH_UNKNOWN.toLong(), null
                )
//                val dataSource =
//                    DefaultDataSourceFactory(
//                        context,
//                        Util.getUserAgent(context, getString(R.string.app_name))
//                    ).createDataSource()

//                val bandwidthMeter = DefaultBandwidthMeter.Builder(context)
//                    .build()
//
//                val defaultDataSourceFactory = DefaultDataSourceFactory(
//                    context,
//                    bandwidthMeter,
//                    DefaultHttpDataSourceFactory(Util.getUserAgent(context, Util.getUserAgent(context, getString(R.string.app_name))), bandwidthMeter)
//                )
//
//                val preloadDataSource =
//                    CacheDataSource(
//                        MyApp.simpleCache,
//                        defaultDataSourceFactory.createDataSource()
//                    )
//                val listener =
//                    CacheUtil.ProgressListener { requestLength: Long, bytesCached: Long, _: Long ->
//                        val downloadPercentage = (bytesCached * 100.0
//                                / requestLength)
//                        Log.d("preLoadVideos", "downloadPercentage: $downloadPercentage")
//                    }
//
//                try {
//                    //enableCache(context!!,dataSource,500)
//                    CacheUtil.cache(
//                        dataSpec,
//                        MyApp.simpleCache,
//                        CacheUtil.DEFAULT_CACHE_KEY_FACTORY,
//                        preloadDataSource,
//                        listener,
//                        null
//                    )
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val params: WindowManager.LayoutParams? = dialog?.window?.attributes
        params?.width = ViewGroup.LayoutParams.MATCH_PARENT
        params?.height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog?.window?.attributes = params
    }

//    Override fun pauseStories() {
//        storiesProgressView.pause();
//    }

    private fun preLoadImages(imageList: MutableList<StoriesItem>) {
        imageList.forEach { imageStory ->
            Glide.with(this).load(imageStory.mediaUrl).preload()
        }
    }

    private fun currentFragment(): StoryDisplayFragment {
        return pagerAdapter.findFragmentByPosition(
            binding.viewPagerFixedViewPager,
            currentPage
        ) as StoryDisplayFragment
    }


    companion object {
        val progressState = SparseIntArray()
    }

    override fun touchPull() {
        elapsedTime = 0
        //stopTimer()
        // binding.storiesProgressView.pause()
    }

    override fun touchDown(xValue: Float, yValue: Float) {
        this.xValue = xValue
        this.yValue = yValue
        if (!isDownClick) {
            //  runTimer()
        }
    }

    override fun touchUp() {
        if (isDownClick && elapsedTime < 500) {
            // stopTimer()
            if ((height - yValue).toInt() <= 0.8 * height) {
                if (!TextUtils.isEmpty("storiesList.get(counter).getDescription()")
                    && (height - yValue).toInt() >= 0.2 * height
                    || TextUtils.isEmpty("storiesList.get(counter).getDescription()")
                ) {
                    if (xValue.toInt() <= width / 2) {
                        //Left
                        if (isRtl) {
                            //nextStory()
                        } else {
                            // previousStory()
                        }
                    } else {
                        //Right
                        if (isRtl) {
                            //  previousStory()
                        } else {
                            //nextStory()
                        }
                    }
                }
            }
        } else {
            //  stopTimer()
            //setHeadingVisibility(View.VISIBLE)
            // binding.storiesProgressView.resume()
        }
        elapsedTime = 0
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
                    //   showToast(it.toString(), 1)
                }
                viewModel.setShowProgress(false)
            }
        }
    }

}