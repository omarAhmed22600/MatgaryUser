package com.brandsin.user.utils.storyviewer

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bolaware.viewstimerstory.Momentz
import com.bolaware.viewstimerstory.MomentzCallback
import com.bolaware.viewstimerstory.MomentzView
import com.brandsin.user.R
import com.brandsin.user.databinding.FragmentStoryDetailsBinding
import com.brandsin.user.network.ResponseHandler
import com.brandsin.user.ui.activity.home.BaseHomeFragment
import com.brandsin.user.ui.main.home.showstory.ShowStoryViewModel
import com.brandsin.user.utils.PrefMethods
import com.brandsin.user.utils.storyviewer.model.StoryItem
import com.brandsin.user.utils.storyviewer.utils.hide
import com.brandsin.user.utils.storyviewer.utils.show
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.squareup.picasso.Callback
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import timber.log.Timber
import toPixel

class StoryDetailsFragment : BaseHomeFragment(), MomentzCallback {

    private var _binding: FragmentStoryDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ShowStoryViewModel by viewModels()

    private var storyId: Int? = null
    private var storyItem: StoryItem? = null

    private var simpleExoPlayer: ExoPlayer? = null

    private var pressTime = 0L
    private var onResumeCalled = false
    private var onVideoPrepared = false

    private var volume = PrefMethods.getSoundStory()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentStoryDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        storyId = requireArguments().getInt("STORY_ID")

        viewModel.getStoryDetailsById(storyId ?: 0)

        setBtnListener()
        subscribeData()

        if (PrefMethods.getSoundStory() == 0f) { // true
            simpleExoPlayer?.volume = PrefMethods.getSoundStory()
            binding.icSound.setImageResource(R.drawable.ic_sound_off)
        } else {
            simpleExoPlayer?.volume = PrefMethods.getSoundStory()
            binding.icSound.setImageResource(R.drawable.ic_sound_on)
        }
    }

    private fun initView(data: StoryItem?) {
        Glide.with(requireContext())
            .load(data?.store?.thumbnail)
            .error(R.drawable.app_logo)
            .into(binding.imgStore)

        binding.storeName.text = data?.store?.name
        binding.favoriteCount.text = data?.favCount.toString()
        binding.followersCount.text = data?.views.toString()

        if (data?.isFavorite == true) {
            binding.imgFavorite.setImageResource(R.drawable.ic_favorite_selected)
        } else {
            binding.imgFavorite.setImageResource(R.drawable.ic_normal_favorite)
        }

        if (data?.store?.isFollowed == true) {
            binding.storeFollow.text = getString(R.string.followed)
        } else {
            binding.storeFollow.text = getString(R.string.follow)
        }

        binding.imgStore.setOnClickListener {
            val action =
                StoryDetailsFragmentDirections.storyDetailsToStoreDetails(data?.storeId!!)
            findNavController().navigate(action)
        }
    }

    private fun setBtnListener() {
        binding.linFavoriteCount.setOnClickListener {
            // call api store follow
            if (PrefMethods.getLoginState(context)) {
                viewModel.addAndRemoveFavorite(storyItem?.id)
            } else {
                showToast("please login first", 1)
            }
        }

        binding.storeFollow.setOnClickListener {
            // call api store follow
            if (PrefMethods.getLoginState(requireActivity())) {
                viewModel.newFollowStore(storyItem?.storeId)
            } else {
                showToast("please login first", 1)
            }
        }

        binding.close.setOnClickListener {
            findNavController().navigate(R.id.story_details_to_home_new)
        }

        binding.icNormalShare.setOnClickListener {
            generateDynamicLink()
        }

        binding.icSound.setOnClickListener {
            checkVolumeOfStory()
        }
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
                    "https://dev.brandsin.net/story?story_Id=${storyItem?.id ?: ""}"
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
                    .setTitle(storyItem?.store?.name ?: "") // "Your Title"
                    .setDescription("Your Description")
                    .setImageUrl(Uri.parse(storyItem?.store?.thumbnail ?: ""))
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
                    val shareSub = resources.getString(R.string.app_name)
                    share.putExtra(Intent.EXTRA_SUBJECT, shareSub)
                    share.putExtra(Intent.EXTRA_TEXT, shareBody)
                    startActivity(Intent.createChooser(share, "Share using"))
                } else {
                    // Handle error https://dev.brandsin.net/api/hajaty/store/show?store_id=66&user_id=817&locale=ar&page=0&limit=50 (1841ms)
                    println("dynamicLink == exception Link: ${task.exception}")
                }
            }
    }

    private fun subscribeData() {
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

        viewModel.getFollowResponseResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseHandler.Success -> {
                    if (it.data?.success == true) {
                        showToast(it.data.message.toString(), 2)
                        viewModel.getStoryDetailsById(storyId ?: 0)
                    } else {
                        showToast(it.data?.message.toString(), 1)
                    }
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

        viewModel.addAndRemoveFavoriteResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseHandler.Success -> {
                    // showToast(it.data?.message.toString(), 2)
                    // viewModel.getAllFavoriteProduct()
                    if (it.data?.message.toString() == getString(R.string.added_story_to_my_favorite)) {
                        binding.imgFavorite.setImageResource(R.drawable.ic_favorite_selected)
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

        viewModel.getStoryDetailsByIdResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseHandler.Success -> {
                    storyItem = it.data?.storyItem?.get(0)
                    initView(storyItem)
                    // updateStory()
                    show(it.data?.storyItem)
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

    fun show(storyItem: ArrayList<StoryItem?>?) {
        for (xItem in storyItem!!) {
            if (xItem?.mediaUrl.isNullOrEmpty()) {
                val textView = TextView(requireActivity())
                textView.text = xItem?.text
                textView.textSize = 20f.toPixel(requireActivity()).toFloat()
                textView.gravity = Gravity.CENTER
                textView.setTextColor(Color.parseColor("#ffffff"))

                val momentz = MomentzView(textView, 5)
                momentz.view.solidColor
                viewModel.listOfViews.add(momentz)

            } else if (xItem?.mediaUrl?.endsWith(".jpeg") == true ||
                xItem?.mediaUrl?.endsWith(".jpg") == true ||
                xItem?.mediaUrl?.endsWith(".png") == true
            ) {
                binding.icSound.hide()

                val image = ImageView(requireActivity())
                val momentz = MomentzView(image, 10)

                Picasso.get()
                    .load(xItem.mediaUrl)
                    .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(image, object : Callback {
                        override fun onSuccess() {}

                        override fun onError(e: Exception?) {
                            Toast.makeText(
                                requireActivity(),
                                e?.localizedMessage,
                                Toast.LENGTH_LONG
                            ).show()
                            e?.printStackTrace()
                        }
                    })
                viewModel.listOfViews.add(momentz)

            } else if (xItem?.mediaUrl?.endsWith(".mp4") == true) {
                binding.icSound.show()

                val video = VideoView(requireActivity())

                if (PrefMethods.getSoundStory() == 0f) { // true
                    video.isSoundEffectsEnabled = false
                    binding.icSound.setImageResource(R.drawable.ic_sound_off)
                } else {
                    video.isSoundEffectsEnabled = true
                    binding.icSound.setImageResource(R.drawable.ic_sound_on)
                }

                val momentZ = MomentzView(video, 60)
                val str = xItem.mediaUrl
                val uri = Uri.parse(str)
                video.setVideoURI(uri)
                viewModel.listOfViews.add(momentZ)
            }
        }

        Momentz(requireActivity(), viewModel.listOfViews, binding.container, this).start()
    }

    override fun done() {
        //////////////////////
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

    override fun onPause() {
        super.onPause()
        simpleExoPlayer?.playWhenReady = false
        simpleExoPlayer?.volume = PrefMethods.getSoundStory()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
