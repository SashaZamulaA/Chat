package com.example.aleksandr.chatfirebase.messages

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.example.aleksandr.chatfirebase.R
import com.example.aleksandr.chatfirebase.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*

class NewMessageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = "Select User"
//        val adapter = GroupAdapter<ViewHolder>()
//        adapter.add(UserItem())
//
//        recyclerview_newmessage.adapter = adapter

        fetchUsers()
    }
companion object {
    val USER_KEY = "USER_KEY"
}

    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/user")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                p0.children.forEach {
                    Log.d("NewMessage", it.toString())
                    val user = it.getValue(User::class.java)
                    if (user != null) {
                        adapter.add(UserItem(user))
                    }
                }
                adapter.setOnItemClickListener { item, view ->
                    val intent = Intent(view.context, ChatLogActivity::class.java)
                    val userItem = item as UserItem
//                    intent.putExtra(USER_KEY, userItem.user.username)
                    intent.putExtra(USER_KEY, userItem.user)
                    startActivity(intent)
                    finish()
                }

                recyclerview_newmessage.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

}

class UserItem(val user: User) : Item<ViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.username_textview_new_message.text = user.username

        Picasso.get()
            .load(user.profileImageUrl)
            .into(viewHolder.itemView.imageView_new_message)
    }

}
