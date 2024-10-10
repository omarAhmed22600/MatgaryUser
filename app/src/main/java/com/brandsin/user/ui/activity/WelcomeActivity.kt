package com.brandsin.user.ui.activity

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.brandsin.user.R
import com.brandsin.user.database.BaseRepository
import com.brandsin.user.databinding.ActivityWelcomeBinding
import com.brandsin.user.model.IntroResponse
import com.brandsin.user.model.constants.Codes
import com.brandsin.user.model.constants.Params
import com.brandsin.user.ui.activity.auth.AuthActivity
import com.brandsin.user.ui.dialogs.toast.DialogToastFragment
import com.brandsin.user.utils.PrefMethods
import com.brandsin.user.utils.Utils
import com.bumptech.glide.Glide
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WelcomeActivity : AppCompatActivity() {
    lateinit var binding: ActivityWelcomeBinding
    var MAX_STEP = 0

    private lateinit var viewPager: ViewPager
    private var myViewPagerAdapter: MyViewPagerAdapter? = null

    val aboutTitleArray = ArrayList<String>()
    val aboutImagesArArray = ArrayList<String>()
    val aboutImagesEnArray = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_welcome)

        PrefMethods.saveHomePopup(true)
        if (PrefMethods.getWelcome()) {
            startActivity(Intent(this, AuthActivity::class.java))
            finish()
        } else {
            getIntro()
        }

        binding.btnNext.setOnClickListener {
            val current = viewPager.currentItem + 1
            if (current < MAX_STEP) {
                // move to next screen
                viewPager.currentItem = current
            } else {
                PrefMethods.saveWelcome(true)
                val intent = Intent(applicationContext, AuthActivity::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startActivity(
                        intent,
                        ActivityOptions.makeSceneTransitionAnimation(this@WelcomeActivity)
                            .toBundle()
                    )
                }
            }
        }

        binding.btnBack.setOnClickListener {
            val current = viewPager.currentItem - 1
            viewPager.currentItem = current
        }
    }

    fun setSlider() {
        binding.consPage.visibility = View.VISIBLE
        viewPager = findViewById(R.id.view_pager)
        window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        myViewPagerAdapter = MyViewPagerAdapter()
        viewPager.adapter = myViewPagerAdapter
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener)

        binding.dotsIndicator.setViewPager(viewPager)
        viewPager.adapter!!.registerDataSetObserver(binding.dotsIndicator.dataSetObserver)
    }

    private var viewPagerPageChangeListener: ViewPager.OnPageChangeListener =
        object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                if (position == MAX_STEP) {
                    binding.btnNext.text = getString(R.string.login)
                } else {
                    binding.btnNext.text = getString(R.string.next)
                }
            }

            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
            override fun onPageScrollStateChanged(arg0: Int) {}
        }

    inner class MyViewPagerAdapter : PagerAdapter() {
        private var layoutInflater: LayoutInflater? = null
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            layoutInflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater?
            val view: View = layoutInflater!!.inflate(R.layout.raw_intro_app, container, false)
            if (PrefMethods.getLanguage() == "ar") {
                Glide.with(applicationContext).load(aboutImagesArArray[position])
                    .into((view.findViewById<View>(R.id.iv_introImg) as ImageView))
            } else {
                Glide.with(applicationContext).load(aboutImagesEnArray[position])
                    .into((view.findViewById<View>(R.id.iv_introImg) as ImageView))
            }
            (view.findViewById<View>(R.id.tv_introTitle) as TextView).text =
                aboutTitleArray[position]
            container.addView(view)
            return view
        }

        override fun getCount(): Int {
            return aboutTitleArray.size
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view === obj
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val view = `object` as View
            container.removeView(view)
        }
    }

    private var exitTime: Long = 0

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    fun doExitApp() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show()
            exitTime = System.currentTimeMillis()
        } else {
            finishAffinity()
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    override fun onBackPressed() {
        doExitApp()
    }

    private fun getIntro() {
        val baeRepo = BaseRepository()
        val responseCall: Call<IntroResponse?> = baeRepo.apiInterface
            .getIntro("client")
        responseCall.enqueue(object : Callback<IntroResponse?> {
            override fun onResponse(
                call: Call<IntroResponse?>,
                response: Response<IntroResponse?>
            ) {
                if (response.isSuccessful) {
                    if (response.body()!!.success!!) {
                        if (response.body()!!.data!!.isNotEmpty()) {
                            MAX_STEP = response.body()!!.data!!.size
                            for (item in response.body()!!.data!!) {
                                if (item != null) {
                                    aboutTitleArray.add(item.introduction.toString())
                                    aboutImagesArArray.add(item.imageAr.toString())
                                    aboutImagesEnArray.add(item.imageEn.toString())
                                }
                            }
                            setSlider()
                        } else {
                            val intent = Intent(applicationContext, AuthActivity::class.java)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                startActivity(
                                    intent,
                                    ActivityOptions.makeSceneTransitionAnimation(this@WelcomeActivity)
                                        .toBundle()
                                )
                                finish()
                            }
                        }
                    }
                } else {
                    showToast(response.message(), 1)
                }
            }

            override fun onFailure(call: Call<IntroResponse?>, t: Throwable) {
                showToast(t.message!!, 1)
            }
        })
    }

    fun showToast(msg: String, type: Int) {
        //success 2
        //false  1
        val bundle = Bundle()
        bundle.putString(Params.DIALOG_TOAST_MESSAGE, msg)
        bundle.putInt(Params.DIALOG_TOAST_TYPE, type)
        Utils.startDialogActivity(
            this,
            DialogToastFragment::class.java.name,
            Codes.DIALOG_TOAST_REQUEST,
            bundle
        )
    }
}
