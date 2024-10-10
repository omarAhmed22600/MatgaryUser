package  com.brandsin.user.ui.menu.favourits.storyView

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
import android.widget.Toast
import androidx.fragment.app.viewModels
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
import com.brandsin.user.utils.storyviewer.PageViewOperator
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

class FavoriteStoryDisplayFragment(val pageViewOperator: PageViewOperator) : BaseHomeFragment(),
    StoriesProgressView.StoriesListener {

    private var _binding: FragmentStoryDisplayBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ShowStoryViewModel by viewModels()

    private val position: Int by lazy {
        arguments?.getInt(EXTRA_POSITION) ?: 0
    }

    private val storyUser: StoriesItem by lazy {
        (arguments?.getParcelable<StoriesItem>(
            EXTRA_STORY_USER
        ) as StoriesItem)
    }

    private val stories: StoriesItem by lazy { storyUser }

    private var simpleExoPlayer: ExoPlayer? = null

    private var counter = position
    private var pressTime = 0L
    private var limit = 500L
    private var onResumeCalled = false
    private var onVideoPrepared = false

    private var volume = 1f

    companion object {
        private const val EXTRA_POSITION = "EXTRA_POSITION"
        private const val EXTRA_STORY_USER = "EXTRA_STORY_USER"
        fun newInstance(
            pageViewOperator: PageViewOperator,
            position: Int,
            story: StoriesItem
        ): FavoriteStoryDisplayFragment {
            return FavoriteStoryDisplayFragment(pageViewOperator).apply {
                arguments = Bundle().apply {
                    putInt(EXTRA_POSITION, position)
                    putParcelable(EXTRA_STORY_USER, story)
                }
            }
        }
    }

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
        setUpUi()
        updateStory()
        // subscribeData()

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
        counter = restorePosition()
    }

    override fun onResume() {
        super.onResume()
        onResumeCalled = true
        /*if (counter < stories.size) {
            if (stories[counter].mediaUrl?.endsWith(".mp4", true) == true && !onVideoPrepared) {
                simpleExoPlayer?.playWhenReady = false
                simpleExoPlayer?.volume = volume
                return
            }
        }*/

        simpleExoPlayer?.seekTo(5)
        simpleExoPlayer?.playWhenReady = true
        simpleExoPlayer?.volume = PrefMethods.getSoundStory()

        binding.storiesProgressView.startStories()

        /*if (counter == 1) {
            binding.storiesProgressView.startStories()
        } else {
            // restart animation
            counter = FavoriteStoryView.progressState.get(arguments?.getInt(EXTRA_POSITION) ?: 0)
            binding.storiesProgressView.startStories(counter)
        }*/
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
        /*if (counter - 1 < 0) return*/
        --counter
        savePosition(counter)
        updateStory()
    }

    override fun onNext() {
        /*if (1 <= counter + 1) {
            return
        }*/
        ++counter
        savePosition(counter)
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

        viewModel.updateViewStory(stories.id ?: 0)

        /*
         simpleExoPlayer null
         D  stories[counter] StoriesItem(inOffersPage=1, storeId=65, createdAt=2023-07-28 13:11:29, id=436, title=مُد للعود, text=null, store=Store(minPriceProduct=25, image=https://brandsin.net/media/user_v1oz1Yz27j/4665/conversions/image_350x350_0.jpeg, thumbnail=https://brandsin.net/media/user_v1oz1Yz27j/4665/conversions/image_350x350_0.jpeg, images=[{id=4600.0, url=https://brandsin.net/media/user_v1oz1Yz27j/4600/Certificate.jpg}], thumbnailId=4665, avgRating=5.0, name=مُـد للعود, id=65, commercialRegister={id=4666.0, url=https://brandsin.net/media/user_v1oz1Yz27j/4666/image.jpeg}, covers=[CoversItem(id=4660, url=https://brandsin.net/media/user_v1oz1Yz27j/4660/image.jpeg), CoversItem(id=4661, url=https://brandsin.net/media/user_v1oz1Yz27j/4661/image.jpeg), CoversItem(id=4662, url=https://brandsin.net/media/user_v1oz1Yz27j/4662/image.jpeg), CoversItem(id=4663, url=https://brandsin.net/media/user_v1oz1Yz27j/4663/image.jpeg), CoversItem(id=4664, url=https://brandsin.net/media/user_v1oz1Yz27j/4664/image.jpeg)]), media=null, mediaUrl=https://brandsin.net/media/user_/4731/story.mp4, views=85, isPinned=1, isPinnedHomepage=1)
         D  simpleExoPlayer null
         */
        if (stories.mediaUrl?.endsWith(".mp4", true) == true
            && stories.text.isNullOrEmpty()
        ) {
            binding.storyDisplayVideo.show()
            binding.storyDisplayImage.hide()
            binding.consTxtStory.hide()
            binding.tvTxtStory.hide()
            binding.icSound.show()
            binding.storyDisplayVideoProgress.show()
            initializePlayer()
        } else if (stories.mediaUrl?.isNotEmpty() == true && stories.text?.isNotEmpty() == true) {
            binding.storyDisplayVideo.hide()
            binding.consTxtStory.show()
            binding.consTxtStory.setBackgroundColor(Color.TRANSPARENT)
            binding.tvTxtStory.show()
            binding.storyDisplayImage.show()
            binding.icSound.hide()
            binding.storyDisplayVideoProgress.show()
            // set text of story with image of story
            Glide.with(requireContext())
                .load(stories.mediaUrl)
                .error(R.drawable.app_logo)
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        // Handle the case where image loading failed
                        // This method is called if Glide encounters an error during image loading
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
                        return false // Return false to allow Glide to display the loaded image
                    }

                })
                .into(binding.storyDisplayImage)
            binding.tvTxtStory.text = stories.text.toString()
        } else if ((stories.mediaUrl?.endsWith(".png", true) == true ||
                    stories.mediaUrl?.endsWith(".jpg", true) == true ||
                    stories.mediaUrl?.endsWith(".jpeg", true) == true)
            && stories.text.isNullOrEmpty()
        ) {
            binding.storyDisplayVideo.hide()
            binding.consTxtStory.hide()
            binding.tvTxtStory.show()
            binding.storyDisplayImage.show()
            binding.icSound.hide()
            binding.storyDisplayVideoProgress.show()
            // set text of story with image of story
            Glide.with(requireContext())
                .load(stories.mediaUrl)
                .error(R.drawable.app_logo)
                .addListener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        // Handle the case where image loading failed
                        // This method is called if Glide encounters an error during image loading
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
                        return false // Return false to allow Glide to display the loaded image
                    }

                })
                .into(binding.storyDisplayImage)
            binding.tvTxtStory.text = stories.text.toString()
        } else if (stories.mediaUrl.isNullOrEmpty() && stories.text?.isNotEmpty() == true) {
            binding.storyDisplayVideo.hide()
            binding.consTxtStory.show()
            binding.tvTxtStory.show()
            binding.icSound.hide()
            binding.storyDisplayImage.hide()
            binding.storyDisplayVideoProgress.hide()
            // set text of story with image of story
            binding.tvTxtStory.text = stories.text.toString()
        } else {
            binding.storyDisplayVideo.hide()
            binding.storyDisplayVideoProgress.hide()
            binding.icSound.hide()
            binding.storyDisplayImage.show()
            Glide.with(requireContext())
                .load(stories.mediaUrl)
                .error(R.drawable.app_logo)
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
        val proxyUrl = proxy.getProxyUrl(stories.mediaUrl ?: "")

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
                pageViewOperator.nextPageView()

                /*if (counter == stories.size.minus(1)) {
                    pageViewOperator.nextPageView()
                } else {
                    binding.storiesProgressView.skip()
                }*/
            }

            @Deprecated("Deprecated in Java")
            override fun onLoadingChanged(isLoading: Boolean) {
                super.onLoadingChanged(isLoading)
                if (isLoading) {
                    binding.storyDisplayVideoProgress.show()
                    pressTime = System.currentTimeMillis()
                    pauseCurrentStory()
                } else {
                    binding.storyDisplayVideoProgress.hide()
                    try {
                        binding.storiesProgressView
                            .getProgressWithIndex(counter)
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
        val touchListener = object : OnSwipeTouchListener(requireActivity()) {
            override fun onSwipeTop() {
                Toast.makeText(activity, "onSwipeTop", Toast.LENGTH_LONG).show()
            }

            override fun onSwipeBottom() {
                Toast.makeText(activity, "onSwipeBottom", Toast.LENGTH_LONG).show()
            }

            override fun onClick(view: View) {
                when (view) {
                    binding.next -> {
                        pageViewOperator.nextPageView()

                        /*if (counter == position - 1) {
                            pageViewOperator.nextPageView()
                        } else {
                            binding.storiesProgressView.skip()
                        }*/
                    }

                    binding.previous -> {
                        if (counter == 0) {
                            pageViewOperator.backPageView()
                        } else {
                            binding.storiesProgressView.reverse()
                        }
                    }
                }
            }

            override fun onLongClick() {
                // hideStoryOverlay()
                // pauseCurrentStory()
                // onResumeCalled = false
                binding.storiesProgressView.pause()
                binding.storiesProgressView.abandon()

                if ((stories.mediaUrl?.endsWith(".png", true) == true ||
                            stories.mediaUrl?.endsWith(".jpg", true) == true ||
                            stories.mediaUrl?.endsWith(".jpeg", true) == true)
                    && stories.text.isNullOrEmpty() && stories.x != null && stories.y != null
                    && stories.x != 0.0 && stories.y != 0.0
                ) {
                    println("show == else if else if else if else if else if") // X Offset: 216, Y Offset: 930
                    println("show == X: ${stories.x?.toFloat() ?: 0.0f}, Y: ${stories.y?.toFloat() ?: 0.0f}")
                    setPositionWithOffset(
                        binding.constraintTagXAndY,
                        stories.x?.toFloat() ?: 00.00f, //
                        stories.y?.toFloat() ?: 00.00f //
                    )
                    binding.constraintTagXAndY.show()
                    binding.productName.text = stories.product?.name.toString()
                    binding.productPrice.text =
                        stories.product?.skus?.get(0)?.price.toString() + " " + getString(R.string.currency)

                    binding.constraintTagXAndY.setOnClickListener {
                        when (stories.product?.type) {
                            "simple" -> {
                                val bundle = Bundle()
                                bundle.putParcelable(Params.STORE_PRODUCT_ITEM, stories.product)
                                Utils.startDialogActivity(
                                    requireActivity(),
                                    DialogOrderAddonsFragment::class.java.name,
                                    Codes.SELECT_ORDER_ADDONS_DIALOG,
                                    bundle
                                )
                            }

                            "variable" -> {
                                val intent =
                                    Intent(requireActivity(), OrderAddonsActivity::class.java)
                                intent.putExtra(Params.STORE_PRODUCT_ITEM, stories.product)
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
            1, position = arguments?.getInt(EXTRA_POSITION) ?: -1
        )
        binding.storiesProgressView.setAllStoryDuration(4000L)
        binding.storiesProgressView.setStoriesListener(this)

        Glide.with(this)
            .load(storyUser.store?.thumbnail)
            .circleCrop()
            .into(binding.storyDisplayProfilePicture)

        binding.storyDisplayNick.text = storyUser.store?.name

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
        FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(
                Uri.parse(
                    "https://dev.brandsin.net/story?story_Id=${
                        storyUser.id ?: ""
                    }"
                )
            ) // Replace with your deep link URL
            .setDomainUriPrefix("https://brandsin.page.link") // Replace with your Firebase Dynamic Links domain
            .setAndroidParameters(
                DynamicLink.AndroidParameters.Builder(requireContext().packageName)
                    .setFallbackUrl(Uri.parse("YOUR_FALLBACK_URL_HERE"))
                    .setMinimumVersion(1) // Optional: Minimum app version required
                    .build()
            )
            .setSocialMetaTagParameters(
                DynamicLink.SocialMetaTagParameters.Builder()
                    .setTitle(
                        storyUser.store?.name ?: ""
                    ) // "Your Title"
                    .setDescription("Your Description")
                    .setImageUrl(
                        Uri.parse(
                            storyUser.store?.covers?.get(position)?.url ?: ""
                        )
                    ) // Optional: Image URL for sharing ("https://www.example.com/image.png")
                    .build()
            )
            .buildShortDynamicLink()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val shortLink = task.result.shortLink
                    val flowchartLink = task.result.previewLink
                    // Handle the short link (e.g., display it or share it)
                    println("dynamicLink == Short Link: $shortLink")
                    println("dynamicLink == Preview Link: $flowchartLink")
 
                    // Handle the short link (e.g., share it)
                    // shortLink.toString() contains the shortened URL
                    val share = Intent(Intent.ACTION_SEND)
                    share.type = "text/Share"
                    val shareBody = shortLink.toString()
                    val shareSub = requireActivity().getString(R.string.app_name)
                    share.putExtra(Intent.EXTRA_SUBJECT, shareSub)
                    share.putExtra(Intent.EXTRA_TEXT, shareBody)
                    startActivity(Intent.createChooser(share, "Share using"))
                } else {
                    // Handle error https://dev.brandsin.net/api/hajaty/store/show?store_id=66&user_id=817&locale=ar&page=0&limit=50 (1841ms)
                    println("dynamicLink == exception Link: ${task.exception}")
                }
            }
    }

    private fun showStoryOverlay() {
        if (binding.storyOverlay.alpha != 0F) return

        binding.storyOverlay.animate()
            .setDuration(100)
            .alpha(1F)
            .start()
    }

    private fun hideStoryOverlay() {
        if (binding.storyOverlay.alpha != 1F) return

        binding.storyOverlay.animate()
            .setDuration(200)
            .alpha(0F)
            .start()
    }

    private fun savePosition(pos: Int) {
        FavoriteStoryView.progressState.put(position, pos)
    }

    private fun restorePosition(): Int {
        return 0 // StoryView.progressState.get(position)
    }

    fun pauseCurrentStory() {
        simpleExoPlayer?.volume = PrefMethods.getSoundStory()
        simpleExoPlayer?.playWhenReady = false
        binding.storiesProgressView.pause()
    }

    fun resumeCurrentStory() {
        if (onResumeCalled) {
            simpleExoPlayer?.volume = PrefMethods.getSoundStory()
            simpleExoPlayer?.playWhenReady = true
            showStoryOverlay()
            binding.storiesProgressView.resume()
        }
    }
}