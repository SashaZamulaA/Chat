package com.example.aleksandr.chatfirebase.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
class ChatMessage( val id: String, val text: String, val fromId:String, val toId:String, val timestamp: Long) {

    constructor() : this("", "", "", "", -1)
}