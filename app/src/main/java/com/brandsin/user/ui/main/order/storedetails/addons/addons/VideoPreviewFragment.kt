package com.brandsin.user.ui.main.order.storedetails.addons.addons

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.MediaController
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.brandsin.user.databinding.FragmentVideoPreviewBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player

class VideoPreviewFragment : Fragment() {

    private var _binding: FragmentVideoPreviewBinding? = null
    private val binding get() = _binding!!

    private var exoPlayer: ExoPlayer? = null

    private var playBackPosition = 0L
    private var playWhenReady = true

    private var videoURLArgs: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        videoURLArgs = requireArguments().getString("videoUrl")

        initViews()

        exoPlayer?.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                // Handle playback errors here
                Log.e("TAG", "Playback error: ${error.message}")
            }
        })

        setBtnListener()
    }

    private fun setBtnListener() {
        binding.icBack.setOnClickListener {
            if (requireActivity().supportFragmentManager.backStackEntryCount > 0)
                requireActivity().onBackPressed()
            else
                findNavController().navigateUp()
        }
    }

    private fun initViews() {
        //Creating MediaController
        val mediaController = MediaController(requireActivity())
        mediaController.setAnchorView(binding.exoPlayerView)
        //specify the location of media file
        //Setting MediaController and URI, then starting the videoView
        binding.exoPlayerView.setMediaController(mediaController)
        binding.exoPlayerView.setVideoURI(Uri.parse(videoURLArgs))
        binding.exoPlayerView.requestFocus()
        binding.exoPlayerView.start()

        /*exoPlayer = ExoPlayer.Builder(requireContext()).build()

            binding.exoPlayerView.player = exoPlayer

            // Build the media item.
            // Set the media item to be played.
            exoPlayer?.setMediaItem(MediaItem.fromUri(Uri.parse(videoURLArgs)))

            binding.exoPlayerView.useController = false

            // Prepare the player.
            exoPlayer?.prepare()*/

        /*exoPlayer?.playWhenReady = true
        binding.exoPlayerView.player = exoPlayer
        val defaultHttpDataSourceFactory = DefaultHttpDataSource.Factory()
        val mediaItem = MediaItem.fromUri(videoURLArgs ?: "")
        val mediaSource = DashMediaSource.Factory(defaultHttpDataSourceFactory)
            .createMediaSource(mediaItem)

        exoPlayer?.setMediaSource(mediaSource)
        exoPlayer?.seekTo(playBackPosition)
        exoPlayer?.playWhenReady = playWhenReady
        exoPlayer?.prepare()*/
    }

    override fun onResume() {
        super.onResume()
        exoPlayer?.playWhenReady = true

        // Start the playback.
        exoPlayer?.play()
    }

    private fun releasePlayer() {
        exoPlayer?.let { player ->
            playBackPosition = player.currentPosition
            playWhenReady = player.playWhenReady
            player.release()
            exoPlayer = null
        }
    }

    override fun onPause() {
        super.onPause()

        exoPlayer?.pause()
        exoPlayer?.playWhenReady = false
    }

    override fun onStop() {
        super.onStop()

        exoPlayer?.pause()
        exoPlayer?.playWhenReady = false
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null

        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        exoPlayer?.stop()
        exoPlayer?.clearMediaItems()
    }
}