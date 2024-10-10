package com.brandsin.user.realtimeDatabase.dao

import com.brandsin.user.realtimeDatabase.RealtimeDatabase
import com.brandsin.user.ui.chat.model.MessageModel
import com.google.firebase.database.Query

class InboxDao {

    private var realtimeDatabase: RealtimeDatabase = RealtimeDatabase()

    fun createMessage(
        myId: String?, chatId: String?, messageId: String?, messageModel: MessageModel
    ) {
        realtimeDatabase.getMessageListRef().child(myId!!)
            .child(chatId!!)
            .child(messageId!!)
            .setValue(messageModel)
    }

    fun getInboxListRef(myId: String): Query {
        return realtimeDatabase.getMessageListRef().child(myId).orderByKey()
    }

    fun readMessage(myId: String?, chatId: String?): Query {
        return realtimeDatabase.getMessageListRef()
            .child(myId!!) // 817
            .child(chatId!!)
    }
}