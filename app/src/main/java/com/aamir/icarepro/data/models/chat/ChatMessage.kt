package com.aamir.icarepro.data.models.chat

data class ChatMessage(
    val chat_conversation_id: Int,
    val created_at: String,
    val filename: Any,
    val id: Int,
    val message: String,
    var read_at: String,
    val receiver_id: Int,
    val sender_id: Int,
    val type: String,
    val updated_at: String
)