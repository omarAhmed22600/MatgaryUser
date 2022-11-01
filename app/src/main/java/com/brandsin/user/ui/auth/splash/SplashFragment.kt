package com.brandsin.user.ui.auth.splash

import android.content.Intent
import android.graphics.PixelFormat
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.exoplayer2.SimpleExoPlayer
import com.brandsin.user.R
import com.brandsin.user.model.constants.Const
import com.brandsin.user.ui.activity.auth.AuthActivity
import com.brandsin.user.ui.activity.auth.BaseAuthFragment
import com.brandsin.user.ui.activity.home.HomeActivity
import com.brandsin.user.utils.PrefMethods
import kotlinx.android.synthetic.main.auth_fragment_splash.*
import kotlinx.coroutines.launch
import java.io.File


class SplashFragment : BaseAuthFragment(), MediaPlayer.OnCompletionListener {

    private var player: SimpleExoPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.auth_fragment_splash, container, false)

//        lifecycleScope.launch {
//            delay(2000)
//
//            when {
//                /*
//                * User logged in before and save his login state
//                */
//                PrefMethods.getLoginState() -> {
//                    when {
//                        /*  User logged in before and has default address */
//                        PrefMethods.getUserLocation() != null -> {
//                            requireActivity().startActivity(
//                                Intent(
//                                    requireActivity(),
//                                    HomeActivity::class.java
//                                )
//                            )
//                            requireActivity().finishAffinity()
//                        }
//                        /*  User logged in before and but doesn't has default address */
//                        else -> {
//                            findNavController().navigate(R.id.splash_to_permission)
//                        }
//                    }
//                }
//                /*
//                *  If user logged before But he't save his default address but doesn't save his login state
//                */
//                PrefMethods.getUserLocation() != null -> {
//                    PrefMethods.deleteUserData()
//                    if ((activity as AuthActivity).intent.getBooleanExtra(
//                            Const.ACCESS_LOGIN,
//                            false
//                        )
//                    ) {
//                        findNavController().navigate(R.id.splash_to_login)
//                    } else {
//                        findNavController().navigate(R.id.splash_to_login_ways)
//                    }
//                }
//                /*
//                * If user open the app for the first time Or logged before but he logged out and clear his default address
//                * */
//                else -> {
//                    PrefMethods.deleteUserData()
//                    findNavController().navigate(R.id.splash_to_permission)
//                }
//            }
//        }

        return view
    }

    override fun onStart() {
        super.onStart()
        startVideo()

    }

    private fun startVideo() {
        val url: Uri =
            Uri.parse("android.resource://" + activity!!.packageName.toString() + "/" + R.raw.splash)
        val file = File(url.toString())
        val mc = MediaController(context)
        activity!!.window.setFormat(PixelFormat.TRANSLUCENT)
        videoView1.setMediaController(null)
        videoView1.requestFocus();
        videoView1.setVideoURI(url)
        videoView1.setOnCompletionListener(this)
        videoView1.start()
    }

    override fun onCompletion(p0: MediaPlayer?) {
        lifecycleScope.launch {
            //delay(2000)

            when {
                /*
                * User logged in before and save his login state
                */
                PrefMethods.getLoginState() -> {
                    when {
                        /*  User logged in before and has default address */
                        PrefMethods.getUserLocation() != null -> {
                            requireActivity().startActivity(
                                Intent(
                                    requireActivity(),
                                    HomeActivity::class.java
                                )
                            )
                            requireActivity().finishAffinity()
                        }
                        /*  User logged in before and but doesn't has default address */
                        else -> {
                            findNavController().navigate(R.id.splash_to_permission)
                        }
                    }
                }
                /*
                *  If user logged before But he't save his default address but doesn't save his login state
                */
                PrefMethods.getUserLocation() != null -> {
                    PrefMethods.deleteUserData()
                    if ((activity as AuthActivity).intent.getBooleanExtra(
                            Const.ACCESS_LOGIN,
                            false
                        )
                    ) {
                        findNavController().navigate(R.id.splash_to_login)
                    } else {
                        findNavController().navigate(R.id.splash_to_login_ways)
                    }
                }
                /*
                * If user open the app for the first time Or logged before but he logged out and clear his default address
                * */
                else -> {
                    PrefMethods.deleteUserData()
                    findNavController().navigate(R.id.splash_to_permission)
                }
            }
        }

    }


}