package com.brandsin.user.ui.menu.favourits.storyView

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.DisplayMetrics
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
import com.brandsin.user.utils.PrefMethods
import com.brandsin.user.utils.PullDismissLayout
import com.brandsin.user.utils.Utils
import com.brandsin.user.utils.storyviewer.PageChangeListener
import com.brandsin.user.utils.storyviewer.PageViewOperator
import com.brandsin.user.utils.storyviewer.callBack.TouchCallbacks
import com.brandsin.user.utils.storyviewer.utils.CubeOutTransformer
import com.bumptech.glide.Glide
import timber.log.Timber

class FavoriteStoryView(
    var currentPage: Int,
    private val discoverStoryItem: StoriesItem
) : DialogFragment(), PullDismissLayout.Listener, Observer<Any?>, PageViewOperator, TouchCallbacks {

    lateinit var binding: DialogStoriesBinding
    private lateinit var pagerAdapter: FavoriteStoryPagerAdapter

    lateinit var viewModel: ShowStoryViewModel

    // Touch Events
    private var isDownClick = false
    private var elapsedTime: Long = 0
    private var width = 0
    private var height: Int = 0
    private var xValue = 0f
    private var yValue: Float = 0f
    private var num = 0
    private val isRtl = false
    private var favoriteStoryViewListener: FavoriteStoryViewListener? = null

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

    fun setStoryViewListener(favoriteStoryViewListener: FavoriteStoryViewListener?) {
        this.favoriteStoryViewListener = favoriteStoryViewListener
    }

    interface FavoriteStoryViewListener {
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

        viewModel.storyItem = discoverStoryItem

        viewModel.mutableLiveData.observe(viewLifecycleOwner, this)

        initView()
        setUpPager()

        (view.findViewById<View>(R.id.pull_dismiss_layout) as PullDismissLayout).setListener(this)

        (view.findViewById<View>(R.id.pull_dismiss_layout) as PullDismissLayout)
            .setmTouchCallbacks(this)

        if (favoriteStoryViewListener == null) {
            binding.seeStore.visibility = View.GONE
        }

        setBtnListener()
        subscribeData()
    }

    private fun initView() {
        Glide.with(this)
            .load(viewModel.storyItem?.store?.thumbnail)
            .error(R.drawable.app_logo)
            .into(binding.imgStore)

        binding.storeName.text =
            viewModel.storyItem?.store?.name

        binding.favoriteCount.text =
            viewModel.storyItem?.favCount.toString()

        binding.followersCount.text =
            viewModel.storyItem?.views.toString()

        if (viewModel.storyItem?.isFavorite == true) {
            binding.imgFavorite.setImageResource(R.drawable.ic_primary_favorite)
        } else {
            binding.imgFavorite.setImageResource(R.drawable.ic_normal_favorite)
        }

//        if (viewModel.storyItem?.store?.isFollowed == true) {
            binding.storeFollow.text = getString(R.string.followed)
/*        } else {
            binding.storeFollow.text = getString(R.string.follow)
        }*/
    }

    private fun setBtnListener() {
        binding.close.setOnClickListener {
            dismiss()
            dismissAllowingStateLoss()
        }

        binding.seeStore.setOnClickListener {
            binding.imageView6.visibility = View.GONE
            binding.textView11.visibility = View.GONE
            binding.btnShowStories.visibility = View.VISIBLE
            binding.btnShowStore.visibility = View.VISIBLE
        }

        binding.linFavoriteCount.setOnClickListener {
            // call api store follow
            if (PrefMethods.getLoginState(context)) {
                viewModel.addAndRemoveFavorite(viewModel.storyItem?.id)
            } else {
                showToast("please login first", 1)
            }
        }

        binding.storeFollow.setOnClickListener {
            // call api store follow
            if (PrefMethods.getLoginState(context)) {
                viewModel.newFollowStore(viewModel.storyItem?.storeId)
            } else {
                showToast("please login first", 1)
            }
        }

        binding.imgStore.setOnClickListener {
            num = 2
            dismiss()
            dismissAllowingStateLoss()
            favoriteStoryViewListener?.onDoneClicked(
                num,
                viewModel.storyItem!!
            )
        }
    }

    private fun subscribeData() {
        viewModel.getFollowResponseResponse.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseHandler.Success -> {
                    if (it.data?.message.toString() == "Store followed" || it.data?.message.toString() == "تم متابعة المتجر")
                    {
                        binding.storeFollow.text = getString(R.string.followed)
                    } else {
                        binding.storeFollow.text = getString(R.string.follow)

                    }
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
                    Timber.e("fav response")

                    if (it.data?.message.toString() == getString(R.string.added_story_to_my_favorite)) {
                        /*binding.favoriteCount.text =
                            binding.favoriteCount.text.toString().toInt().plus(1).toString()*/
                        binding.imgFavorite.setImageResource(R.drawable.ic_primary_favorite)
                    } else {
                        /*binding.favoriteCount.text =
                            binding.favoriteCount.text.toString().toInt().plus(1).toString()*/
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

    override fun onDismissed() {
        dismissAllowingStateLoss()
    }

    override fun onShouldInterceptTouchEvent(): Boolean {
        return false
    }

    private fun setUpPager() {
        pagerAdapter = FavoriteStoryPagerAdapter(
            this,
            childFragmentManager,
            viewModel.storyItem!!
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
            }

            override fun onPageScrollCanceled() {
                currentFragment().resumeCurrentStory()
            }
        })
    }

    override fun backPageView() {
        if (binding.viewPagerFixedViewPager.currentItem > 0) {
            try {
                fakeDrag(false)
            } catch (e: Exception) {
                //NO OP
            }
        }
    }

    override fun nextPageView() {
        dismiss()
        dismissAllowingStateLoss()
        fakeDrag(true)
        /*if (binding.viewPagerFixedViewPager.currentItem + 0 < (binding.viewPagerFixedViewPager.adapter?.count ?: 0)
        ) {
            try {
                fakeDrag(true)
            } catch (e: Exception) {
                //NO OP
            }
        } else { // there is no next story
            dismiss()
            dismissAllowingStateLoss()
            // Toast.makeText(context, "All stories displayed.", Toast.LENGTH_LONG).show()
        }*/
    }

    private fun fakeDrag(forward: Boolean) {
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
    }

    override fun onResume() {
        super.onResume()
        val params: WindowManager.LayoutParams? = dialog?.window?.attributes
        params?.width = ViewGroup.LayoutParams.MATCH_PARENT
        params?.height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog?.window?.attributes = params
    }

    private fun currentFragment(): FavoriteStoryDisplayFragment {
        return pagerAdapter.findFragmentByPosition(
            binding.viewPagerFixedViewPager,
            currentPage
        ) as FavoriteStoryDisplayFragment
    }

    companion object {
        val progressState = SparseIntArray()
    }

    override fun touchPull() {
        elapsedTime = 0
    }

    override fun touchDown(xValue: Float, yValue: Float) {
        this.xValue = xValue
        this.yValue = yValue
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