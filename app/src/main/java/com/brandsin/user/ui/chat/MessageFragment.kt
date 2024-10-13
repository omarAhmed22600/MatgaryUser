package com.brandsin.user.ui.chat

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.brandsin.user.R
import com.brandsin.user.databinding.FragmentMessageBinding
import com.brandsin.user.network.longToast
import com.brandsin.user.ui.activity.BaseFragment
import com.brandsin.user.ui.chat.adapter.MessagesAdapter
import com.brandsin.user.ui.chat.viewmodel.InboxViewModel
import com.brandsin.user.utils.BasePushNotificationService
import com.brandsin.user.utils.PrefMethods
import com.brandsin.user.utils.storyviewer.utils.hide
import com.brandsin.user.utils.storyviewer.utils.show
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageFragment : BaseFragment() {

    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!

    private val viewModel: InboxViewModel by viewModels()

    private lateinit var messagesAdapter: MessagesAdapter

    private var avatarStore: String? = null
    private var avatarUser: String? = null
    private var senderId: String? = null
    private var senderName: String? = null
    private var storeId: String? = null
    private var storeName: String? = null

    private lateinit var message: String

    private val imageClickCallBack: (image: String) -> Unit = { image ->
        val bundle = Bundle()
        bundle.putString("Image_Message", image)
        findNavController().navigate(R.id.messageImagePreviewFragment, bundle)
    }
    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Timber.e("investigate1 -> on receive chat ")
            if (intent?.action.orEmpty() == BasePushNotificationService.REFRESH_CHAT) {
                Timber.e("investigate1 -> on receive update chat")
                val targetId = intent?.getIntExtra(BasePushNotificationService.CHAT,-1)
                if (targetId == (storeId?.toInt() ?: -1))
                {
                    Timber.e("it is the same id")
                    viewModel.readMessage(storeId.orEmpty())
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMessageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireArguments().let {
            avatarStore = it.getString("Avatar_Store")
            avatarUser = it.getString("Avatar_User")
            senderId = it.getString("Sender_Id")
            senderName = it.getString("Sender_Name")
            storeId = it.getString("Store_Id")
            storeName = it.getString("Store_Name")
        }

        viewModel.readMessage(storeId)

        initView()
        setBtnListeners()
        initRecycler()
        subscribeData()
        subscribeActivityResult()
    }

    private fun initView() {
        if (PrefMethods.getLanguage() == "ar") {
            binding.imgBack.setImageResource(R.drawable.arrow_next)
        } else {
            binding.imgBack.setImageResource(R.drawable.arrow_next_ar)
        }

        Glide.with(requireContext())
            .load(avatarStore?.trim())
            .apply(RequestOptions().circleCrop())
            .into(binding.userImage)

        binding.userName.text = storeName ?: ""
    }

    private fun initRecycler() {
        binding.progressBar.hide()
        binding.rvMessages.apply {
            messagesAdapter = MessagesAdapter(imageClickCallBack)
            setHasFixedSize(true)
            adapter = messagesAdapter
            /*layoutManager = LinearLayoutManager(context,LinearLayoutManager.VERTICAL,true)*/
            val layoutManager = LinearLayoutManager(context)
            layoutManager.stackFromEnd = true // Stack items from the end
            this.layoutManager = layoutManager

        }
    }

    private fun setBtnListeners() {
        binding.btnSend.setOnClickListener {
            if (validateData())
                viewModel.sendMessage(
                    avatarStore,
                    avatarUser,
                    senderName,
                    storeName,
                    storeId,
                    message,
                    "text"
                )
        }

        binding.imgBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSendImage.setOnClickListener {
            viewModel.isImageUploaded = false
            if (checkIfAllPermissionsGranted())
                performCameraAndGalleyAction()

        }
    }

    private fun subscribeData() {
        viewModel.messagesLiveData.observe(viewLifecycleOwner) { res ->
            if (res.isNotEmpty()) {
                Timber.e("list updated")

                // Sort the list of messages by date (if required)
                val dateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.US)
                val sortedMessages = res.sortedBy { dateFormat.parse(it.date) }.toMutableList()

                messagesAdapter.submitData(sortedMessages)

                binding.rvMessages.apply {
                    // Ensure the adapter is set
                    if (adapter == null) {
                        adapter = messagesAdapter
                    }

                    // Post the scrolling action to ensure it happens after the layout is updated
                    post {
                        smoothScrollToPosition(sortedMessages.size - 1)
                    }
                }

                binding.progressBar.hide()
            }
        }
    }


    private fun validateData(): Boolean {
        var isValid = true
        message = binding.edtMessage.text?.trim().toString()
        if (binding.edtMessage.text.toString().trim().isEmpty()) {
            isValid = false
        } else binding.edtMessage.setText("")
        return isValid
    }

    private fun subscribeActivityResult() {
        activityResultsDataLive.observe(viewLifecycleOwner) {
            if (it != null) {
                val imagePath: Uri?
                try {
                    if (it.data != null) {
                        if (!viewModel.isImageUploaded) {
                            imagePath = it.data!!
                            binding.progressBar.show()
                            viewModel.uploadImageToSend(
                                imagePath,
                                avatarStore,
                                avatarUser,
                                senderName,
                                storeName,
                                storeId
                            )
                        }
                    } else {
                        longToast(getString(R.string.someThing_went_wrong))
                        viewModel.isImageUploaded = false
                    }

                } catch (ex: Exception) {
                    longToast(getString(R.string.someThing_went_wrong))
                    viewModel.isImageUploaded = false
                }
            }
        }
    }


    private fun performCameraAndGalleyAction() {
        val i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        launchActivityForResult(i)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onResume() {
        super.onResume()
        Timber.e("MessageFragment -> onResume()")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireActivity().registerReceiver(
                broadcastReceiver,
                IntentFilter(BasePushNotificationService.REFRESH_CHAT),
                Context.RECEIVER_EXPORTED
            )
        } else {
            requireActivity().registerReceiver(
                broadcastReceiver,
                IntentFilter(BasePushNotificationService.REFRESH_CHAT),
            )
        }
    }
    override fun onPause() {
        super.onPause()
        Timber.e("MessageFragment -> onPause()")
        requireActivity().unregisterReceiver(broadcastReceiver)
    }
}


// Function to convert string date to Date
// Function to convert milliseconds to Date
fun String.toDate(): Date {
    val milliseconds = this.toLong()
    return Date(milliseconds)
}