package com.brandsin.user.ui.main.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ScaleGestureDetector
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.brandsin.user.databinding.FragmentImagesPreviewBinding
import com.brandsin.user.databinding.ItemImagePreviewBinding
import com.bumptech.glide.Glide

class ImagesPreviewFragment : Fragment() {

    private var _binding: FragmentImagesPreviewBinding? = null
    private val binding get() = _binding!!

    private var receivedCoversUrls: ArrayList<String> = ArrayList()
    private var type: String = ""

    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private var scaleFactor = 1.0f

    companion object {
        fun newInstance(bundle: Bundle): ImagesPreviewFragment {
            val fragment = ImagesPreviewFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentImagesPreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val receivedBundle = arguments
        if (receivedBundle != null) {
            receivedCoversUrls = requireArguments().getStringArrayList("COVERS_URL_LIST")!!
            type = requireArguments().getString("Type") ?: ""
        }

        println("imageClickCallBack(receivedCoversUrls)  == $receivedCoversUrls")
        println("imageClickCallBack(type)  == $type")

        initViewPager()
        // setupSlider()
        setBtnListener()
    }

    private fun setBtnListener() {
        binding.icBack.setOnClickListener {
            if (type == "chat") {
                findNavController().navigateUp()
            } else {
                navigateBack()
            }
        }
    }

    private fun navigateBack() {
        if (requireActivity().supportFragmentManager.backStackEntryCount > 0)
            requireActivity().onBackPressed()
        else
            findNavController().navigateUp()
    }

    private fun initViewPager() {
        // Set up the ViewPager and adapter
        val adapter = ImagePagerAdapter(receivedCoversUrls)
        binding.viewPager.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

class ImagePagerAdapter(
    private val images: ArrayList<String>
) : RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder>() {

    private lateinit var binding: ItemImagePreviewBinding

    inner class ImageViewHolder(itemView: ItemImagePreviewBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        val imageView = itemView.imageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        binding =
            ItemImagePreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        // Set the image for each item in the ViewPager
        val imageResource = images[position]
        // Load and display the image in the ImageView within the item
        // You can use libraries like Glide or Picasso for efficient image loading.

        Glide.with(holder.itemView.context)
            .load(imageResource)
            .into(holder.imageView)

    }

    override fun getItemCount(): Int {
        return images.size
    }
}
