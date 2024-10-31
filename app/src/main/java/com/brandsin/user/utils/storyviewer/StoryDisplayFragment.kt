package  com.brandsin.user.utils.storyviewer

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.brandsin.user.R
import com.brandsin.user.databinding.FragmentStoryDisplayBinding
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Params
import com.brandsin.user.model.order.homepage.StoriesItem
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.ui.main.home.showstory.ShowStoryViewModel
import com.brandsin.user.ui.main.order.storedetails.addons.skus.activity.OrderAddonsActivity
import com.brandsin.user.ui.main.order.storedetails.addons.skus.dialog.DialogOrderAddonsFragment
import com.brandsin.user.utils.MyApp
import com.brandsin.user.utils.PrefMethods
import com.brandsin.user.utils.Utils
import com.brandsin.user.utils.storyviewer.customview.StoriesProgressView
import com.brandsin.user.utils.storyviewer.utils.OnSwipeTouchListener
import com.brandsin.user.utils.storyviewer.utils.hide
import com.brandsin.user.utils.storyviewer.utils.show
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.danikula.videocache.HttpProxyCacheServer
import com.google.android.exoplayer2.DefaultRenderersFactory
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import timber.log.Timber

class StoryDisplayFragment(val pageViewOperator: PageViewOperator) : BaseHomeFragment(),
    StoriesProgressView.StoriesListener {

    private var _binding: FragmentStoryDisplayBinding? = null
    private val binding get() = _binding!!

    lateinit var viewModel: ShowStoryViewModel


    val position: Int by
    lazy { arguments?.getInt(EXTRA_POSITION) ?: 0 }

    private val storyUser: ArrayList<StoriesItem> by
    lazy {
        (arguments?.getParcelableArrayList<StoriesItem>(
            EXTRA_STORY_USER
        ) as ArrayList<StoriesItem>)
    }

    private val stories: ArrayList<StoriesItem> by lazy { storyUser }

    private var simpleExoPlayer: ExoPlayer? = null
    private lateinit var mediaDataSourceFactory: DataSource.Factory

    var counter = MutableLiveData(0)
    private var pressTime = 0L
    private var limit = 500L
    private var onResumeCalled = false
    private var onVideoPrepared = false

    private var volume = PrefMethods.getSoundStory()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoryDisplayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.storyDisplayVideo.useController = false
        viewModel = ViewModelProvider(requireActivity()).get(ShowStoryViewModel::class.java)
        setUpUi()
        updateStory()
        counter.observe(viewLifecycleOwner)
        {
            viewModel.currentPosition.value = it
            Timber.e("counter chaged $it")
        }
        if (PrefMethods.getSoundStory() == 0f) { // true
            simpleExoPlayer?.volume = PrefMethods.getSoundStory()
            binding.icSound.setImageResource(R.drawable.ic_sound_off)
        } else {
            simpleExoPlayer?.volume = PrefMethods.getSoundStory()
            binding.icSound.setImageResource(R.drawable.ic_sound_on)
        }
    }

    override fun onStart() {
        super.onStart()
        counter.value = restorePosition()
    }

    override fun onResume() {
        super.onResume()
        onResumeCalled = true
        if (counter!!.value!! < stories.size) {
            if (stories[counter!!.value!!].mediaUrl?.endsWith(".mp4", true) == true && !onVideoPrepared) {
                simpleExoPlayer?.playWhenReady = false
                simpleExoPlayer?.volume = PrefMethods.getSoundStory()
                return
            }
        }

        simpleExoPlayer?.seekTo(5)
        simpleExoPlayer?.playWhenReady = true
        simpleExoPlayer?.volume = PrefMethods.getSoundStory()
        if (counter!!.value!! == 0) {
            binding.storiesProgressView.startStories()
        } else {
            // restart animation
            counter.value = StoryView.progressState.get(arguments?.getInt(EXTRA_POSITION) ?: 0)
            binding.storiesProgressView.startStories(counter!!.value!!)
        }
    }

    override fun onPause() {
        super.onPause()
        simpleExoPlayer?.playWhenReady = false
        simpleExoPlayer?.volume = PrefMethods.getSoundStory()
        binding.storiesProgressView.abandon()
    }

    override fun onComplete() {
        simpleExoPlayer?.release()
        pageViewOperator.nextPageView()
    }

    override fun onPrev() {
        if (counter!!.value!! - 1 < 0) return
        counter.value = counter.value!! - 1
        savePosition(counter!!.value!!)
        updateStory()
    }

    override fun onNext() {
        if (stories.size <= counter!!.value!! + 1) {
            return
        }
        counter.value = counter.value!! + 1
        savePosition(counter!!.value!!)
        updateStory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        simpleExoPlayer?.release()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun updateStory() {
        simpleExoPlayer?.volume = PrefMethods.getSoundStory()

        simpleExoPlayer?.stop()

        viewModel.updateViewStory(stories[counter!!.value!!].id ?: 0)

        if (stories[counter!!.value!!].mediaUrl?.endsWith(".mp4", true) == true) {
            binding.storyDisplayVideo.show()
            binding.storyDisplayImage.hide()
            binding.consTxtStory.hide()
            binding.tvTxtStory.hide()
            binding.icSound.show()
            binding.storyDisplayVideoProgress.show()

            initializePlayer()
        } else if ((stories[counter!!.value!!].mediaUrl?.endsWith(".png", true) == true ||
                    stories[counter!!.value!!].mediaUrl?.endsWith(".jpg", true) == true ||
                    stories[counter!!.value!!].mediaUrl?.endsWith(".jpeg", true) == true)
        ) {
            binding.storyDisplayVideo.hide()
            binding.consTxtStory.hide()
            binding.tvTxtStory.show()
            binding.storyDisplayImage.show()
            binding.icSound.hide()
            binding.storyDisplayVideoProgress.hide()

            // set image of story
            Glide.with(requireContext())
                .load(stories[counter!!.value!!].mediaUrl)
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        exeption: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        pauseCurrentStory()
                        // Handle the case where image loading failed
                        // This method is called if Glide encounter!!.value!!s an error during image loading
                        // You can show an error placeholder or handle it in another way
                        return false // Return false to allow Glide to display its default error drawable
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        dataSource: com.bumptech.glide.load.DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        // Handle the case where image loading is successful
                        // This method is called when the image is successfully loaded
                        // You can perform any additional actions here
                        binding.storyDisplayVideoProgress.hide()
                        resumeCurrentStory()
                        return false // Return false to allow Glide to display the loaded image
                    }
                })
                .into(binding.storyDisplayImage)

        } else if (stories[counter!!.value!!].mediaUrl.isNullOrEmpty() &&
            stories[counter!!.value!!].text?.isNotEmpty() == true
        ) {
            binding.storyDisplayVideo.hide()
            binding.consTxtStory.show()
            binding.tvTxtStory.show()
            binding.icSound.hide()
            binding.storyDisplayImage.hide()
            binding.storyDisplayVideoProgress.hide()
            // set text of story with image of story
            binding.tvTxtStory.text = stories[counter!!.value!!].text.toString()
        } else {
            binding.storyDisplayVideo.hide()
            binding.storyDisplayVideoProgress.hide()
            binding.icSound.hide()
            binding.storyDisplayImage.show()
            Glide.with(requireContext())
                .load(stories[counter!!.value!!].mediaUrl)
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        exeption: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        pauseCurrentStory()
                        // Handle the case where image loading failed
                        // This method is called if Glide encounter!!.value!!s an error during image loading
                        // You can show an error placeholder or handle it in another way
                        return false // Return false to allow Glide to display its default error drawable
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        dataSource: com.bumptech.glide.load.DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        // Handle the case where image loading is successful
                        // This method is called when the image is successfully loaded
                        // You can perform any additional actions here
                        binding.storyDisplayVideoProgress.hide()
                        resumeCurrentStory()
                        return false // Return false to allow Glide to display the loaded image
                    }
                })

                .into(binding.storyDisplayImage)
        }
    }

    private fun initializePlayer() {
        if (simpleExoPlayer == null) {
            simpleExoPlayer = ExoPlayer.Builder(
                requireContext(),
                DefaultRenderersFactory(requireContext())
            ).build()
        } else {
            simpleExoPlayer?.release()
            simpleExoPlayer = null
            simpleExoPlayer = ExoPlayer.Builder(
                requireContext(),
                DefaultRenderersFactory(requireContext())
            ).build()
        }

        val proxy: HttpProxyCacheServer = MyApp.getInstance().getProxy(requireActivity())!!
        val proxyUrl = proxy.getProxyUrl(stories[counter!!.value!!].mediaUrl ?: "")
        val videoUri = Uri.parse(proxyUrl)
        val mediaItem: MediaItem = MediaItem.fromUri(videoUri)
        val httpDataSourceFactory: DefaultHttpDataSource.Factory =
            DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true)
        val defaultDataSourceFactory: DataSource.Factory =
            DefaultDataSourceFactory(requireContext(), httpDataSourceFactory)
        val cacheDataSourceFactory: DataSource.Factory = CacheDataSource.Factory()
            .setCache(MyApp.simpleCache!!)
            .setUpstreamDataSourceFactory(defaultDataSourceFactory)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

        val mediaSource: MediaSource = ProgressiveMediaSource.Factory(cacheDataSourceFactory)
            .createMediaSource(mediaItem)
        simpleExoPlayer?.prepare(mediaSource, false, false)
        if (onResumeCalled) {
            simpleExoPlayer?.playWhenReady = true
            simpleExoPlayer?.volume = PrefMethods.getSoundStory()
        }

        binding.storyDisplayVideo.setShutterBackgroundColor(Color.BLACK)
        binding.storyDisplayVideo.player = simpleExoPlayer

        simpleExoPlayer?.addListener(object : Player.Listener {

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                binding.storyDisplayVideoProgress.hide()
                if (counter!!.value!! == stories.size.minus(1)) {
                    pageViewOperator.nextPageView()
                } else {
                    binding.storiesProgressView.skip()
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onLoadingChanged(isLoading: Boolean) {
                super.onLoadingChanged(isLoading)
                if (isLoading) {
                    binding.storyDisplayVideoProgress.show()
                    pressTime = System.currentTimeMillis()
                    pauseCurrentStory()
                } else {
                    Timber.e("exoplayer onLoadingChanged")
                    binding.storyDisplayVideoProgress.hide()
                    try {
                        binding.storiesProgressView.getProgressWithIndex(counter!!.value!!)
                            .setDuration(simpleExoPlayer?.duration ?: 8000L)
                    } catch (exc: Exception) {
                        Log.d("StoryDisplay", "onLoadingChanged ${exc.localizedMessage}")
                    }
                    onVideoPrepared = true
                    resumeCurrentStory()
                }
            }
        })
    }

    private fun setUpUi() {
        Timber.e("setup called")
        val touchListener = object : OnSwipeTouchListener(requireActivity()) {
            override fun onSwipeTop() {
                //  Toast.makeText(activity, "onSwipeTop", Toast.LENGTH_LONG).show()
            }

            override fun onSwipeBottom() {
                //  Toast.makeText(activity, "onSwipeBottom", Toast.LENGTH_LONG).show()
            }

            override fun onClick(view: View) {
                when (view) {
                    binding.next -> {
                        onResumeCalled = true
                        binding.constraintTagXAndY.hide()
                        if (counter!!.value!! == stories.size - 1) {
                            pageViewOperator.nextPageView()
                        } else {
                            binding.storiesProgressView.skip()
                        }
                    }

                    binding.previous -> {
                        onResumeCalled = true
                        binding.constraintTagXAndY.hide()
                        if (counter!!.value!! == 0) {
                            pageViewOperator.backPageView()
                        } else {
                            binding.storiesProgressView.reverse()
                        }
                    }
                }
            }

            override fun onLongClick() {
                hideStoryOverlay()

                if ((stories[counter!!.value!!].mediaUrl?.endsWith(".png", true) == true ||
                            stories[counter!!.value!!].mediaUrl?.endsWith(".jpg", true) == true ||
                            stories[counter!!.value!!].mediaUrl?.endsWith(".jpeg", true) == true)
                    && stories[counter!!.value!!].text.isNullOrEmpty() && stories[counter!!.value!!].x != null && stories[counter!!.value!!].y != null
                ) {
                    // pauseCurrentStory()
                    onResumeCalled = false
                    binding.storiesProgressView.pause()

                    binding.constraintTagXAndY.show()
                    setPositionWithOffset(
                        binding.constraintTagXAndY,
                        stories[counter!!.value!!].x?.toFloat() ?: 0.0f,
                        stories[counter!!.value!!].y?.toFloat() ?: 0.0f
                    )
                    binding.productName.text = stories[counter!!.value!!].product?.name.toString()
                    binding.productPrice.text =
                        stories[counter!!.value!!].product?.skus?.get(0)?.price.toString() + " " + getString(R.string.currency)

                    Glide.with(requireContext())
                        .load(stories[counter!!.value!!].mediaUrl)
                        .error(R.drawable.app_logo)
                        .into(binding.storyDisplayImage)

                    binding.constraintTagXAndY.setOnClickListener {
                        when (stories[counter!!.value!!].product?.type) {
                            "simple" -> {
                                val bundle = Bundle()
                                bundle.putParcelable(
                                    Params.STORE_PRODUCT_ITEM,
                                    stories[counter!!.value!!].product
                                )
                                Utils.startDialogActivity(
                                    requireActivity(),
                                    DialogOrderAddonsFragment::class.java.name,
                                    Codes.SELECT_ORDER_ADDONS_DIALOG,
                                    bundle
                                )
                            }

                            "variable" -> {
                                val intent = Intent(requireActivity(), OrderAddonsActivity::class.java)
                                intent.putExtra(Params.STORE_PRODUCT_ITEM, stories[counter!!.value!!].product)
                                intent.putExtra(Params.DIALOG_CLICK_ACTION, 0)
                                startActivityForResult(intent, Codes.SELECT_ORDER_ADDONS_ACTIVITY)
                            }
                        }
                    }
                }
            }

            override fun onTouchView(view: View, event: MotionEvent): Boolean {
                super.onTouchView(view, event)
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        pressTime = System.currentTimeMillis()
                        pauseCurrentStory()
                        return false
                    }

                    MotionEvent.ACTION_UP -> {
                        showStoryOverlay()
                        resumeCurrentStory()
                        return limit < System.currentTimeMillis() - pressTime
                    }
                }
                return false
            }
        }
        binding.previous.setOnTouchListener(touchListener)
        binding.next.setOnTouchListener(touchListener)

        binding.storiesProgressView.setStoriesCountDebug(
            stories.size, position = arguments?.getInt(EXTRA_POSITION) ?: -1
        )
        binding.storiesProgressView.setAllStoryDuration(4000L)
        binding.storiesProgressView.setStoriesListener(this)

        Glide.with(this)
            .load(
                storyUser[if (position < storyUser.size) position else storyUser.size - 1].store?.thumbnail
            )
            .circleCrop()
            .into(binding.storyDisplayProfilePicture)

        binding.storyDisplayNick.text =
            storyUser[if (position < storyUser.size) position else storyUser.size - 1].store?.name

        binding.icNormalShare.setOnClickListener {
            generateDynamicLink()
        }

        binding.icSound.setOnClickListener {
            checkVolumeOfStory()
        }
    }

    private fun setPositionWithOffset(view: View, offsetX: Float, offsetY: Float) {
        // Set the position of the view based on the provided offsets
        view.x = offsetX
        view.y = offsetY
    }

    private fun checkVolumeOfStory() {
        // PrefMethods.getSoundStory()
        if (simpleExoPlayer?.volume == 0f) { // true
            volume = 1f
            PrefMethods.setSoundStory(volume)
            simpleExoPlayer?.volume = volume
            binding.icSound.setImageResource(R.drawable.ic_sound_on)
        } else {
            volume = 0f
            PrefMethods.setSoundStory(volume)
            simpleExoPlayer?.volume = volume
            binding.icSound.setImageResource(R.drawable.ic_sound_off)
        }
    }

    private fun generateDynamicLink() {
        val validPosition = if (position < storyUser.size) position else storyUser.size - 1
        val store = storyUser[validPosition].store

        val coverUrl = if (store?.covers != null && position < store.covers.size) {
            store.covers[position]?.url
        } else {
            ""
        }

        FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(
                Uri.parse(
                    "https://dev.brandsin.net/story?story_Id=${storyUser[validPosition].id ?: ""}"
                )
            )
            .setDomainUriPrefix("https://brandsin.page.link")
            .setAndroidParameters(
                DynamicLink.AndroidParameters.Builder(requireContext().packageName)
                    .setFallbackUrl(Uri.parse("YOUR_FALLBACK_URL_HERE"))
                    .setMinimumVersion(1)
                    .build()
            )
            .setSocialMetaTagParameters(
                DynamicLink.SocialMetaTagParameters.Builder()
                    .setTitle(store?.name ?: "")
                    .setDescription("Your Description")
                    .setImageUrl(Uri.parse(coverUrl ?: ""))
                    .build()
            )
            .buildShortDynamicLink()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val shortLink = task.result.shortLink
                    val flowchartLink = task.result.previewLink
                    println("dynamicLink == Short Link: $shortLink")
                    println("dynamicLink == Preview Link: $flowchartLink")

                    val share = Intent(Intent.ACTION_SEND)
                    share.type = "text/Share"
                    val shareBody = shortLink.toString()
                    val shareSub = requireActivity().getString(R.string.app_name)
                    share.putExtra(Intent.EXTRA_SUBJECT, shareSub)
                    share.putExtra(Intent.EXTRA_TEXT, shareBody)
                    startActivity(Intent.createChooser(share, "Share using"))
                } else {
                    println("dynamicLink == exception Link: ${task.exception}")
                }
            }
    }

    private fun showStoryOverlay() {
        if (binding.storyOverlay == null || binding.storyOverlay.alpha != 0F) return

        binding.storyOverlay.animate()
            .setDuration(100)
            .alpha(1F)
            .start()
    }

    private fun hideStoryOverlay() {
        if (binding.storyOverlay == null || binding.storyOverlay.alpha != 1F) return

        binding.storyOverlay.animate()
            .setDuration(200)
            .alpha(0F)
            .start()
    }

    private fun savePosition(pos: Int) {
        StoryView.progressState.put(position, pos)
    }

    private fun restorePosition(): Int {
        return 0 // StoryView.progressState.get(position)
    }

    fun pauseCurrentStory() {
        if (_binding!= null) {
            simpleExoPlayer?.volume = PrefMethods.getSoundStory()
            simpleExoPlayer?.playWhenReady = false
            binding.storiesProgressView.pause()
        }
    }

    fun resumeCurrentStory() {
        if (onResumeCalled) {
            simpleExoPlayer?.volume = PrefMethods.getSoundStory()
            simpleExoPlayer?.playWhenReady = true
            showStoryOverlay()
            binding.storiesProgressView.resume()
        }
    }

    companion object {
        private const val EXTRA_POSITION = "EXTRA_POSITION"
        private const val EXTRA_STORY_USER = "EXTRA_STORY_USER"
        fun newInstance(
            pageViewOperator: PageViewOperator,
            position: Int,
            story: ArrayList<StoriesItem>
        ): StoryDisplayFragment {
            return StoryDisplayFragment(pageViewOperator).apply {
                arguments = Bundle().apply {
                    putInt(EXTRA_POSITION, position)
                    putParcelableArrayList(EXTRA_STORY_USER, story)
                }
            }
        }
    }
}