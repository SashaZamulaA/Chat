package com.example.aleksandr.chatfirebase.messages

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.aleksandr.chatfirebase.R
import com.example.aleksandr.chatfirebase.R.id.editText_chat_log
import com.example.aleksandr.chatfirebase.models.ChatMessage
import com.example.aleksandr.chatfirebase.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "ChatLog"
    }

    val adapter = GroupAdapter<ViewHolder>()

    var toUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_chat_log.adapter = adapter
        val userName = intent.getStringExtra(NewMessageActivity.USER_KEY)
        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

        supportActionBar?.title = toUser?.username



        listenForMessage()

        button_chat_log.setOnClickListener {
            Log.d(TAG, "Attempt to send message....")
            performeSendMessage()
        }
    }

    private fun listenForMessage() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, p1: String?) {
                val chatMessage = dataSnapshot.getValue(ChatMessage::class.java)

                if (chatMessage != null) {
                    Log.d(TAG, "Chat ${chatMessage.text}")
                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        val currentUser = LatestMessagesActivity.currentUser ?: return
                        adapter.add(ChatFromItem(chatMessage.text, currentUser))
                    } else {
                        adapter.add(ChatToItem(chatMessage.text, toUser!!))
                    }
                }
            }

                override fun onCancelled(p0: DatabaseError) {
        }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {
        }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {
        }

                override fun onChildRemoved(p0: DataSnapshot) {
        }
    })
}


private fun performeSendMessage() {

    val text = editText_chat_log.text.toString()

    val fromId = FirebaseAuth.getInstance().uid
    val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
    val toId = user.uid
    if (fromId == null) return

//    val reference = FirebaseDatabase.getInstance().getReference("/messages").push()
    val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
    val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()
    val chatMessage = ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis() / 1000)

    reference.setValue(chatMessage)
        .addOnSuccessListener {
            Log.d(TAG, "Save our chat message ${reference.key}")
        }
    toReference.setValue(chatMessage)

    val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
    latestMessageRef.setValue(chatMessage)

    val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
    latestMessageToRef.setValue(chatMessage)
}
}

class ChatFromItem(val text: String, val user: User) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_from_row.text = text

        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageView_from_row
        Picasso.get().load(uri).into(targetImageView)

    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatToItem(val text: String, val user: User) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_to_row.text = text

        val uri = user.profileImageUrl
        val targetImageView = viewHolder.itemView.imageView_to_row
        Picasso.get().load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}