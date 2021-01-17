package com.example.kotlinmessenger.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.kotlinmessenger.R
import com.example.kotlinmessenger.models.ChatMessage
import com.example.kotlinmessenger.models.User
import com.example.kotlinmessenger.views.ChatFromItem
import com.example.kotlinmessenger.views.ChatToItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatLogActivity : AppCompatActivity() {
    companion object {
        val TAG = "ChatLog"
    }

    val adapter = GroupAdapter<GroupieViewHolder>()

    var toUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_chat_log.adapter = adapter

        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

        supportActionBar?.title = toUser?.username

        listenForMessages()

        //Button click listener
        send_button_chat_log.setOnClickListener {
            Log.d(TAG, "Attemp to send")
            performSendMessage()
        }


    }

    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = toUser?.uid
        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        //Fetch Data of messages from Firebase
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, previousChildName: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)
                if (chatMessage != null) {
                    Log.d(TAG, chatMessage.text)

                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        val currentUser = LatestMessagesActivity.currentUser ?: return
                        adapter.add(ChatFromItem(chatMessage.text, currentUser))

                    } else {

                        adapter.add(ChatToItem(chatMessage.text, toUser!!))
                    }

                }

                //scroll to newest message
                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)

            }

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }
        })

    }


    private fun performSendMessage() {

        //Get sender info
        val text = edittext_chat_log.text.toString()
        val fromId = FirebaseAuth.getInstance().uid

        //Get receiver info
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user?.uid

        if (fromId == null) return

        //Push messages to each user message's list on the RealTimeDatabase
        val reference =
            FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()

        val toReference =
            FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        val chatMessage =
            ChatMessage(reference.key!!, text, fromId, toId!!, System.currentTimeMillis() / 1000)
        reference.setValue(chatMessage)

            .addOnSuccessListener {
                Log.d(TAG, "Saved chat message: ${reference.key}")
                edittext_chat_log.text.clear()
                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
            }
        toReference.setValue(chatMessage)

        //Update Latest message to the recently uploaded message
        val latestMessageRef =
            FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(chatMessage)

        val latestMessageToRef =
            FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)

    }


}

