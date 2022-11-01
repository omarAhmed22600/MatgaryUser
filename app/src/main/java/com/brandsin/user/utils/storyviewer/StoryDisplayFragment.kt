package  com.brandsin.user.utils.storyviewer

import android.content.Context
import android.graphics.Color
import android.net.Uri
import com.brandsin.user.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.danikula.videocache.HttpProxyCacheServer
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.brandsin.user.model.order.homepage.StoriesItem
import com.brandsin.user.utils.MyApp
import com.brandsin.user.utils.storyviewer.customview.StoriesProgressView
import com.brandsin.user.utils.storyviewer.utils.OnSwipeTouchListener
import com.brandsin.user.utils.storyviewer.utils.hide
import com.brandsin.user.utils.storyviewer.utils.show
import kotlinx.android.synthetic.main.fragment_story_display.*


class StoryDisplayFragment(val pageViewOperator: PageViewOperator) : Fragment(),
    StoriesProgressView.StoriesListener {

    private val position: Int by
    lazy { arguments?.getInt(EXTRA_POSITION) ?: 0 }

    private val storyUser: ArrayList<StoriesItem> by
    lazy {
        (arguments?.getParcelableArrayList<StoriesItem>(
            EXTRA_STORY_USER
        ) as ArrayList<StoriesItem>)
    }

    private val stories: ArrayList<StoriesItem> by
    lazy { storyUser }

    private var simpleExoPlayer: ExoPlayer? = null
    private lateinit var mediaDataSourceFactory: DataSource.Factory
   // var pageViewOperator: PageViewOperator? = null
    private var counter = 0
    private var pressTime = 0L
    private var limit = 500L
    private var onResumeCalled = false
    private var onVideoPrepared = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_story_display, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        storyDisplayVideo.useController = false
        setUpUi()
        updateStory()


    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
       // this.pageViewOperator = context as PageViewOperator
    }

    override fun onStart() {
        super.onStart()
        counter = restorePosition()
    }

    override fun onResume() {
        super.onResume()
        onResumeCalled = true
        if (counter < stories.size) {
            if (stories[counter].isVideo() && !onVideoPrepared) {
                simpleExoPlayer?.playWhenReady = false
                return
            }
        }

        simpleExoPlayer?.seekTo(5)
        simpleExoPlayer?.playWhenReady = true
        if (counter == 0) {
            storiesProgressView?.startStories()
        } else {
            // restart animation
            counter = StoryView.progressState.get(arguments?.getInt(EXTRA_POSITION) ?: 0)
            storiesProgressView?.startStories(counter)
        }
    }

    override fun onPause() {
        super.onPause()
        simpleExoPlayer?.playWhenReady = false
        storiesProgressView?.abandon()
    }

    override fun onComplete() {
        simpleExoPlayer?.release()
        pageViewOperator?.nextPageView()
    }

    override fun onPrev() {
        if (counter - 1 < 0) return
        --counter
        savePosition(counter)
        updateStory()
    }

    override fun onNext() {
        if (stories.size <= counter + 1) {
            return
        }
        ++counter
        savePosition(counter)
        updateStory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        simpleExoPlayer?.release()
    }

    private fun updateStory() {
        simpleExoPlayer?.stop()
        if (stories[counter].isVideo()) {
            storyDisplayVideo.show()
            storyDisplayImage.hide()
            storyDisplayVideoProgress.show()
            initializePlayer()
        } else {
            storyDisplayVideo.hide()
            storyDisplayVideoProgress.hide()
            storyDisplayImage.show()
            Glide.with(this).load(stories[counter].mediaUrl).into(storyDisplayImage)
        }

//        val cal: Calendar = Calendar.getInstance(Locale.ENGLISH).apply {
//            timeInMillis = stories[counter].createdAt
//        }
        //storyDisplayTime.text = DateFormat.format("MM-dd-yyyy HH:mm:ss", cal).toString()
    }

    private fun initializePlayer() {
        if (simpleExoPlayer == null) {
            simpleExoPlayer = ExoPlayer.Builder(requireContext(),
                DefaultRenderersFactory(requireContext())).build()
        } else {
            simpleExoPlayer?.release()
            simpleExoPlayer = null
            simpleExoPlayer = ExoPlayer.Builder(requireContext(),
                DefaultRenderersFactory(requireContext())).build()
        }

//        mediaDataSourceFactory = CacheDataSourceFactory(
//            MyApp.simpleCache,
//            DefaultHttpDataSourceFactory(
//                Util.getUserAgent(
//                    context,
//                    Util.getUserAgent(requireContext(), getString(R.string.app_name))
//                ),null,DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
//                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
//                false
//            )
//        )

        val proxy: HttpProxyCacheServer = MyApp.getInstance().getProxy(requireActivity())!!
        val proxyUrl = proxy.getProxyUrl(stories[counter].mediaUrl)
//        val mediaSource = ProgressiveMediaSource.Factory(mediaDataSourceFactory).createMediaSource(
//            Uri.parse(proxyUrl)
//        )
       // simpleExoPlayer?.prepare(mediaSource, false, false)


        val videoUri = Uri.parse(proxyUrl)
        val mediaItem:MediaItem = MediaItem.fromUri(videoUri)
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
        }

        storyDisplayVideo.setShutterBackgroundColor(Color.BLACK)
        storyDisplayVideo.player = simpleExoPlayer

        simpleExoPlayer?.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                storyDisplayVideoProgress.hide()
                if (counter == stories.size.minus(1)) {
                    pageViewOperator?.nextPageView()
                } else {
                    storiesProgressView?.skip()
                }
            }

            override fun onLoadingChanged(isLoading: Boolean) {
                super.onLoadingChanged(isLoading)
                if (isLoading) {
                    storyDisplayVideoProgress.show()
                    pressTime = System.currentTimeMillis()
                    pauseCurrentStory()
                } else {
                    storyDisplayVideoProgress.hide()
                    try {
                        storiesProgressView?.getProgressWithIndex(counter)?.setDuration(simpleExoPlayer?.duration ?: 8000L)
                    }catch (exc:Exception){}
                    onVideoPrepared = true
                    resumeCurrentStory()
                }
            }
        })
    }

    private fun setUpUi() {
        val touchListener = object : OnSwipeTouchListener(activity!!) {
            override fun onSwipeTop() {
              //  Toast.makeText(activity, "onSwipeTop", Toast.LENGTH_LONG).show()
            }

            override fun onSwipeBottom() {
              //  Toast.makeText(activity, "onSwipeBottom", Toast.LENGTH_LONG).show()
            }

            override fun onClick(view: View) {
                when (view) {
                    next -> {
                        if (counter == stories.size - 1) {
                            pageViewOperator?.nextPageView()
                        } else {
                            storiesProgressView?.skip()
                        }
                    }
                    previous -> {
                        if (counter == 0) {
                            pageViewOperator?.backPageView()
                        } else {
                            storiesProgressView?.reverse()
                        }
                    }
                }
            }

            override fun onLongClick() {
                hideStoryOverlay()
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
        previous.setOnTouchListener(touchListener)
        next.setOnTouchListener(touchListener)

        storiesProgressView?.setStoriesCountDebug(
            stories.size, position = arguments?.getInt(EXTRA_POSITION) ?: -1
        )
        storiesProgressView?.setAllStoryDuration(4000L)
        storiesProgressView?.setStoriesListener(this)

        Glide.with(this).load(
            storyUser[if (position < storyUser.size) position else storyUser.size-1].store!!.thumbnail
        ).circleCrop().into(storyDisplayProfilePicture)
        storyDisplayNick.text =
            storyUser[if (position < storyUser.size) position else storyUser.size-1].store!!.name

    }

    private fun showStoryOverlay() {
        if (storyOverlay == null || storyOverlay.alpha != 0F) return

        storyOverlay.animate()
            .setDuration(100)
            .alpha(1F)
            .start()
    }

    private fun hideStoryOverlay() {
        if (storyOverlay == null || storyOverlay.alpha != 1F) return

        storyOverlay.animate()
            .setDuration(200)
            .alpha(0F)
            .start()
    }

    private fun savePosition(pos: Int) {
        StoryView.progressState.put(position, pos)
    }

    private fun restorePosition(): Int {
        return 0//StoryView.progressState.get(position)
    }

    fun pauseCurrentStory() {
        simpleExoPlayer?.playWhenReady = false
        storiesProgressView?.pause()
    }

    fun resumeCurrentStory() {
        if (onResumeCalled) {
            simpleExoPlayer?.playWhenReady = true
            showStoryOverlay()
            storiesProgressView?.resume()
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