package com.brandsin.user.ui.chat.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.brandsin.user.R
import com.brandsin.user.database.BaseViewModel
import com.brandsin.user.model.constants.Const.NOTIFICATION_URL
import com.brandsin.user.model.menu.notifications.ReadNotificationResponse
import com.brandsin.user.network.requestCall
import com.brandsin.user.realtimeDatabase.dao.InboxDao
import com.brandsin.user.ui.chat.model.MessageModel
import com.brandsin.user.utils.MyApp
import com.brandsin.user.utils.PrefMethods
import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@SuppressLint("LogNotTimber")
class InboxViewModel : BaseViewModel() {

    private val inboxDao: InboxDao = InboxDao()

    var isImageUploaded: Boolean = false

    val isLoading = MutableLiveData(false)
    private val _usersInboxMutable: MutableLiveData<MutableList<MessageModel>> = MutableLiveData()
    val usersInboxLive: LiveData<MutableList<MessageModel>> = _usersInboxMutable

    private val _messagesMutableData: MutableLiveData<ArrayList<MessageModel>> = MutableLiveData()
    val messagesLiveData: LiveData<ArrayList<MessageModel>> = _messagesMutableData

    // List to store the last messages
    private var inboxesList = mutableListOf<MessageModel>()

    fun readChat() {
        // get all Users who start conversation before
        // enqueueSignal(Load)
        isLoading.postValue(true)
        inboxDao.getInboxListRef(PrefMethods.getUserData()?.id.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    isLoading.postValue(false)
                    inboxesList = ArrayList()
                    for (it in snapshot.children) {
                        val dateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.US)

                        var lastmessage : MessageModel? = it.children.firstOrNull()?.getValue(MessageModel::class.java)!!
                        val lastMessage81766 =
                            it.children.forEach {data->
                                val item = data.getValue(MessageModel::class.java)
                                if (dateFormat.parse(item!!.date)!!.after(dateFormat.parse(lastmessage!!.date)))
                                {
                                    lastmessage = item
                                }
//                                inboxesList.add(data.getValue(MessageModel::class.java)!!)
                            }
                        lastmessage?.let { inboxesList.add(it) }
                    }
                    _usersInboxMutable.value = inboxesList
                    // enqueueSignal(StopLoading)
                }

                override fun onCancelled(error: DatabaseError) {
                    isLoading.postValue(false)

                    println("readChat ERROR ${error.message}")
                    // var errorMessage = error.message
                    // enqueueSignal(StopLoading, SomethingWentWrong.ErrorMessage)
                }
            })
    }

    fun readMessage(storeId: String?) {
        inboxDao.readMessage(
            PrefMethods.getUserData()?.id.toString(), // 817
            storeId.toString()  + PrefMethods.getUserData()?.id.toString().trim() // 817130
        ).get().addOnCompleteListener {
            val messageList = ArrayList<MessageModel>()
            if (it.result.exists()) {
                for (ds in it.result.children) {
                    val messages = ds.getValue(MessageModel::class.java)
                    messageList.add(messages!!)
                }
            }
            _messagesMutableData.postValue(messageList)
        }
    }

    fun sendMessage(
        avatarStore: String?, avatarUser: String?, senderName: String?,
        storeName: String?, storeId: String?,
        message: String, typeMessage: String
    ) {
        val dateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.US)
        val currentTime = dateFormat.format(Date())
        // Add new Message in the Conversation to Chat
        val messageId = UUID.randomUUID().toString()
        val messageModel: MessageModel
        if (typeMessage == "image") {
            messageModel = MessageModel(
                avaterstore = avatarStore.toString(),
                avateruser = avatarUser.toString(),
                messageType = "private",
                senderName = senderName.toString(),
                storename = storeName.toString(),
                senderId = PrefMethods.getUserData()?.id.toString().trim(),
                storeId = storeId.toString(),
                image = message,
                message = getString(R.string.photo),
                messageId = messageId,
                date = currentTime,
                type = typeMessage,
                typeBay = "user"
            )
        } else {
            messageModel = MessageModel(
                avaterstore = avatarStore.toString(),
                avateruser = avatarUser.toString(),
                messageType = "private",
                senderName = senderName.toString(),
                storename = storeName.toString(),
                senderId = PrefMethods.getUserData()?.id.toString().trim(),
                storeId = storeId.toString(),
                image = "",
                message = message,
                messageId = messageId,
                date = currentTime,
                type = typeMessage,
                typeBay = "user"
            )
        }

        inboxDao.createMessage(
            PrefMethods.getUserData()?.id.toString().trim(), // 817
            storeId.toString()  + PrefMethods.getUserData()?.id.toString().trim() ,// 817136
            messageId,
            messageModel
        )

        inboxDao.createMessage(
            storeId.toString().trim(), // 66
            storeId.toString()  + PrefMethods.getUserData()?.id.toString().trim(), // 81766
            messageId,
            messageModel
        )

        requestCall<ReadNotificationResponse?>({
            withContext(Dispatchers.IO) { // to return a result its like asyncTask() and await
                return@withContext getApiRepo().sendNotification(
                    messageModel.message.orEmpty(),
                    storeId?.toInt() ?: -1,
                    PrefMethods.getUserData()?.id?:-1
                )
            }
        })
        { res ->
            when (res!!.isSuccess) {
                true -> {
                    Timber.e("sent")
                }

                else -> {
                    Timber.e(res.message.toString())
                }
            }
        }




        isImageUploaded = true

        // if (isPlayServiceAvailable) {
        getToken(
            PrefMethods.getUserData()?.id.toString().trim(), message,
            typeMessage, MyApp.context
        )
        // }
        readMessage(storeId)
    }

    fun uploadImageToSend(
        fileName: Uri, avatarStore: String?, avatarUser: String?, senderName: String?,
        storeName: String?, storeId: String?
    ) {
        // enqueueSignal(Load)
        val storageReference = FirebaseStorage.getInstance()
            .getReference("PictureImage/")
            .child("${PrefMethods.getUserData()?.id.toString().trim()}/")
            .child(
                "${
                    PrefMethods.getUserData()?.id.toString()
                        .trim() + storeId.toString().trim()
                }/"
            )

        val storageReference2 = FirebaseStorage.getInstance()
            .getReference("PictureImage/")
            .child("${storeId.toString().trim()}/")
            .child(
                "${
                    PrefMethods.getUserData()?.id.toString().trim() + storeId.toString().trim()
                }/"
            )

        storageReference.putFile(fileName).addOnSuccessListener { taskSnapshot ->
            val task = taskSnapshot.storage.downloadUrl
            task.addOnCompleteListener { uri: Task<Uri> ->
                if (uri.isSuccessful) {
                    val path = uri.result.toString() // path, "image"
                    sendMessage(avatarStore, avatarUser, senderName, storeName, storeId, path, "image")
                }
            }
        }

        storageReference2.putFile(fileName).addOnSuccessListener { taskSnapshot ->
            val task = taskSnapshot.storage.downloadUrl
            task.addOnCompleteListener { uri: Task<Uri> ->
                if (uri.isSuccessful) {
                    val path = uri.result.toString()
                    // sendMessage(path, "image")
                }
            }
        }
    }


    /*
    inboxDao.createMessage(
            PrefMethods.getStoreData()?.id.toString().trim(), // 66
            messageItem?.senderId.toString().trim() +
                    PrefMethods.getStoreData()?.id.toString().trim(), // 817136
            messageId,
            messageModel
        )

        inboxDao.createMessage(
            messageItem?.senderId.toString().trim(), // 66
            messageItem?.senderId.toString().trim() +
                    PrefMethods.getStoreData()?.id.toString().trim(), // 81766
            messageId,
            messageModel
        )
     */

    private fun getToken(
        receiverId: String,
        message: String,
        type: String,
        context: Context
    ) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
            .child(receiverId)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val token = snapshot.child("token").value.toString()

                    val to = JSONObject()
                    val data = JSONObject()
                    val notification = JSONObject()

                    // data.put("title", userModel.userName)
                    // data.put("currentUserId", userModel.userId)
                    // data.put("currentUserImage", userModel.image)
                    data.put("partnerUserId", receiverId)

                    if (type == "text") {
                        data.put("message", message)
                    } else if (type == "photo") {
                        data.put("message", "photo")
                    }

                    // notification.put("title", userModel.userName)
                    notification.put("body", message)

                    to.put("to", token)
                    to.put("notification", notification)
                    to.put("data", data)

                    Log.d("getToken", "TO: $to")
                    Log.d("getToken", "Notification: $notification")
                    Log.d("getToken", "Data: $data")

                    sendNotification(to, context)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("getToken", "onCancelled: ${error.message}")
            }
        })
    }

    private fun sendNotification(
        to: JSONObject,
        context: Context
    ) {
        val request: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, NOTIFICATION_URL, to,
            Response.Listener { response: JSONObject ->
                Log.d("sendNotification", "onSuccess: $response")
            },
            Response.ErrorListener {
                Log.d("sendNotification", "onError: $it")
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val map: MutableMap<String, String> = HashMap()
                // map["Authorization"] = "key=${BuildConfig.SERVER_KEY}"
                map["Content-type"] = "application/json"
                return map
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }
        }
        val requestQueue = Volley.newRequestQueue(context)
        request.retryPolicy = DefaultRetryPolicy(
            15000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        requestQueue.add(request)
    }
}

